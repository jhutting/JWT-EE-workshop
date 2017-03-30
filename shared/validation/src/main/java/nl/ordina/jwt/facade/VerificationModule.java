package nl.ordina.jwt.facade;

import com.auth0.jwt.interfaces.DecodedJWT;
import nl.ordina.jwt.model.Role;
import nl.ordina.jwt.model.Token;

import java.util.List;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public interface VerificationModule {
	DecodedJWT verifyToken(Token token);

	void verifyIdenticalUserOrAdmin(Token token, Long userId);

	void verifyRoleAndToken(List<Role> allowedRoles, Token token);
}
