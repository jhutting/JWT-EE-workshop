package nl.ordina.jwt.facade.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import nl.ordina.jwt.convertor.UserConverter;
import nl.ordina.jwt.dao.UserEntity;
import nl.ordina.jwt.dao.UserRepository;
import nl.ordina.jwt.facade.TokenModule;
import nl.ordina.jwt.model.Credentials;
import nl.ordina.jwt.model.Role;
import nl.ordina.jwt.model.SaltedValue;
import nl.ordina.jwt.model.Token;
import nl.ordina.jwt.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Random;
import java.util.UUID;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@Stateless
public class TokenModuleImpl implements TokenModule {

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenModuleImpl.class);

	private static final int ITERATIONS = 1024;
	private static final int KEY_LENGTH = 256;

	private static final int ONE_MINUTE = 60 * 1000;
	private static final int ONE_HOUR = 60 * ONE_MINUTE;

	private UserRepository userRepository;

	private EnumMap<Role, Integer> expirationInMSbyRole;

	public TokenModuleImpl() {
		// Damn you, CDI-spec.
	}

	@Inject
	public TokenModuleImpl(UserRepository userRepository) {
		this.userRepository = userRepository;

		expirationInMSbyRole = new EnumMap<>(Role.class);
		expirationInMSbyRole.put(Role.NORMAL, 9 * ONE_HOUR);
		expirationInMSbyRole.put(Role.CONTROLLER, ONE_HOUR);
		expirationInMSbyRole.put(Role.ADMIN, 30 * ONE_MINUTE);
	}

	@Override
	public Token createToken(final Credentials credentials) {
		User user = getUserByCredentials(credentials);

		try {
			RSAPrivateKey key = getRSAPrivateKey();
			String xsrf = generateXSRFToken();
			String jwtToken = JWT.create()
					.withIssuer("Ordina")
					.withClaim("Role", user.getRole().name())
					.withClaim("XSRF", xsrf)
					.withIssuedAt(new Date())
					.withExpiresAt(getExpirationDate(user.getRole()))
					.withSubject("credentials")
					.withAudience("myApp-users")
					.withJWTId(String.valueOf(user.getId()))
					.sign(Algorithm.RSA256(key));

			Token result = new Token();
			result.setJwtToken(jwtToken);
			result.setXsrf(xsrf);
			return result;
		} catch (Exception e) {
			// We don't know how to handle this, so just log it and return 500 server error
			throw new RuntimeException(e);
		}
	}

	@Override
	public void changePassword(User user) {
		if (!user.getNewPassword().equals(user.getRepeatedPassword())) {
			throw new IllegalArgumentException("passwords not identical");
		}

		String databaseSalt = getSalt();
		String compareSalt = databaseSalt + getLocalSalt();
		String saltedPassword = new String(hash(user.getNewPassword(), compareSalt));

		UserEntity change = userRepository.findByUsername(user.getUsername());
		change.setResetToken(null);
		change.setSalt(databaseSalt);
		change.setSaltedPassword(saltedPassword);
	}

	@Override
	public void mailResetToken(String email) {
		// TODO
		throw new NotImplementedException();
		// since we can't email in this workshop - just print it to the log
		//LOGGER.info("resettoken {} for email {}", token, email);
	}

	private RSAPrivateKey getRSAPrivateKey() throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File("/opt/keys/private_key.der").toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return (RSAPrivateKey) kf.generatePrivate(spec);
	}

	private User getUserByCredentials(Credentials credentials) {
		UserEntity user = userRepository.findByUsername(credentials.getUsername());
		if (user == null) {
			throw new NotAuthorizedException("Unknown user");
		}
		checkPassword(credentials.getPassword(), new SaltedValue(user.getSaltedPassword(), user.getSalt()));

		return UserConverter.convertEntity(user);
	}

	private void checkPassword(final String suppliedPassword, final SaltedValue saltedValue) {
		String compareSalt = saltedValue.getSalt() + getLocalSalt();
		if (!equalsPassword(suppliedPassword, compareSalt, saltedValue.getValue())) {
			throw new NotAuthorizedException("wrong password");
		}
	}

	private String getSalt() {
		final Random r = new SecureRandom();
		byte[] salt = new byte[32];
		r.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

	private String getLocalSalt() {
		// TODO read this from an environment specific (properties?) file
		return "LocallyStoredSaltToPreventTargettingSpecificUsers";
	}

	// derived from https://www.owasp.org/index.php/Hashing_Java
	private byte[] hash(String password, String salt) {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
				salt.getBytes(StandardCharsets.UTF_8),
				ITERATIONS,
				KEY_LENGTH);
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			return skf.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
		} finally {
			spec.clearPassword();
		}
	}

	private boolean equalsPassword(String password, String salt, String expectedHash) {
		String pwdHash = new String(hash(password, salt));

		return pwdHash.equals(expectedHash);
	}

	private Date getExpirationDate(Role role) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, expirationInMSbyRole.get(role));
		return cal.getTime();
	}

	private String generateXSRFToken() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
