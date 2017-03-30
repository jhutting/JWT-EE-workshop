package nl.ordina.jwt.facade.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import nl.ordina.jwt.facade.VerificationModule;
import nl.ordina.jwt.model.Role;
import nl.ordina.jwt.model.Token;

import javax.enterprise.inject.Default;
import javax.ws.rs.NotAuthorizedException;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@Default
public class VerificationModuleImpl implements VerificationModule {

	@Override
	public DecodedJWT verifyToken(final Token token) {
		try {
			RSAPublicKey key = getRSAPublicKey();
			JWTVerifier verifier = JWT.require(Algorithm.RSA256(key))
					.withIssuer("Ordina")
					.withAudience("myApp-users")
					.build();
			DecodedJWT result = verifier.verify(token.getJwtToken());

			if (token.isFromCookie() &&
				(token.getXsrf() == null || !token.getXsrf().equals(result.getClaim("XSRF").asString()))) {
				throw new NotAuthorizedException("XSRF token didn't match");
			}

			return result;
		} catch (JWTVerificationException e) {
			throw new NotAuthorizedException(e);
		} catch (Exception e) {
			// We don't know how to handle this, so just log it and return 500 server error
			throw new RuntimeException(e);
		}
	}

	@Override
	public void verifyIdenticalUserOrAdmin(final Token token, final Long userId) {
		DecodedJWT jwt = verifyToken(token);
		if (!userId.equals(Long.valueOf(jwt.getId())) && getRole(jwt) != Role.ADMIN) {
			throw new NotAuthorizedException("Altering someone elses password is not allowed unless you're Admin.");
		}
	}

	@Override
	public void verifyRoleAndToken(List<Role> allowedRoles, Token token) {
		DecodedJWT jwt = verifyToken(token);

		if (!allowedRoles.isEmpty() && !allowedRoles.contains(getRole(jwt))) {
			throw new NotAuthorizedException("Role is not allowed here");
		}
	}

	private Role getRole(DecodedJWT jwt) {
		return Role.valueOf(jwt.getClaim("Role").asString());
	}

	private RSAPublicKey getRSAPublicKey() throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File("/opt/keys/public_key.der").toPath());

		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) kf.generatePublic(spec);
	}
}
