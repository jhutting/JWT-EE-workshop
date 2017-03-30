package nl.ordina.jwt.facade;

import nl.ordina.jwt.model.Credentials;
import nl.ordina.jwt.model.Token;

public interface TokenModule {
	Token createToken(Credentials credentials);
}
