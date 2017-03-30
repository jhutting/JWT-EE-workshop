package nl.ordina.jwt.ssoservice;

import nl.ordina.jwt.JWTApi;
import nl.ordina.jwt.facade.TokenModule;
import nl.ordina.jwt.facade.UserFacade;
import nl.ordina.jwt.model.Credentials;
import nl.ordina.jwt.model.Token;

import javax.inject.Inject;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

public class TokenEndpoint implements JWTApi {

	private TokenModule tokenModule;

	public TokenEndpoint() {
		// Damn you, CDI-spec.
	}

	@Inject
	public TokenEndpoint(TokenModule tokenModule, UserFacade userFacade) {
		this.tokenModule = tokenModule;
	}

	@Override
	public Response createToken(final Credentials credentials) {
		Token token = tokenModule.createToken(credentials);
		NewCookie cookie = new NewCookie("JWT", token.getJwtToken(), "/", "localhost", "Secure JWT cookie", -1, true, true);
		NewCookie xsrfCookie = new NewCookie("XSRF-TOKEN", token.getXsrf(), "/", "localhost", "XSRF-TOKEN", -1, true, false);
		return Response.ok(token.getJwtToken()).cookie(cookie).cookie(xsrfCookie).build();
	}

}
