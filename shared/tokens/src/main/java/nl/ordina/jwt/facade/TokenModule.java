package nl.ordina.jwt.facade;

import nl.ordina.jwt.model.Credentials;
import nl.ordina.jwt.model.Token;
import nl.ordina.jwt.model.User;

public interface TokenModule {
	Token createToken(Credentials credentials);

	void changePassword(User user);

}
