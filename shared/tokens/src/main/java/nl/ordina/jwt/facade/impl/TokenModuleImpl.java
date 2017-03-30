package nl.ordina.jwt.facade.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import nl.ordina.jwt.convertor.UserConverter;
import nl.ordina.jwt.dao.UserEntity;
import nl.ordina.jwt.dao.UserRepository;
import nl.ordina.jwt.facade.TokenModule;
import nl.ordina.jwt.model.Credentials;
import nl.ordina.jwt.model.Token;
import nl.ordina.jwt.model.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.UUID;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@Stateless
public class TokenModuleImpl implements TokenModule {

	private UserRepository userRepository;

	public TokenModuleImpl() {
		// Damn you, CDI-spec.
	}

	@Inject
	public TokenModuleImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Token createToken(final Credentials credentials) {
		User user = getUserByCredentials(credentials);

		try {
			RSAPrivateKey key = getRSAPrivateKey();
			String xsrf = generateXSRFToken();
			String jwtToken = JWT.create()
					.withIssuer("Ordina")
					.withClaim("XSRF", xsrf)
					.withIssuedAt(new Date())
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
		checkPassword(credentials.getPassword(), user.getSaltedPassword());

		return UserConverter.convertEntity(user);
	}

	private void checkPassword(final String suppliedPassword, final String saltedValue) {
		if (!suppliedPassword.equals(saltedValue)) {
			throw new NotAuthorizedException("wrong password");
		}
	}

	private String generateXSRFToken() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
