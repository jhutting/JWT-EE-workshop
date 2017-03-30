package nl.ordina.jwt.ssoservice;

import nl.ordina.jwt.JWTApi;
import nl.ordina.jwt.facade.TokenModule;
import nl.ordina.jwt.facade.UserFacade;
import nl.ordina.jwt.facade.VerificationModule;
import nl.ordina.jwt.model.Credentials;
import nl.ordina.jwt.model.Token;
import nl.ordina.jwt.model.User;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

public class TokenEndpoint implements JWTApi {

	private TokenModule tokenModule;
	private VerificationModule verificationModule;
	private UserFacade userFacade;

	public TokenEndpoint() {
		// Damn you, CDI-spec.
	}

	@Inject
	public TokenEndpoint(VerificationModule verificationModule, TokenModule tokenModule, UserFacade userFacade) {
		this.verificationModule = verificationModule;
		this.tokenModule = tokenModule;
		this.userFacade = userFacade;
	}

	@Override
	public Response createToken(final Credentials credentials) {
		Token token = tokenModule.createToken(credentials);
		NewCookie cookie = new NewCookie("JWT", token.getJwtToken(), "/", "localhost", "Secure JWT cookie", -1, true, true);
		NewCookie xsrfCookie = new NewCookie("XSRF-TOKEN", token.getXsrf(), "/", "localhost", "XSRF-TOKEN", -1, true, false);
		return Response.ok(token.getJwtToken()).cookie(cookie).cookie(xsrfCookie).build();
	}

	@Override
	public Response updatePassword(final String authorization, final User user) {
		validateForPasswordUpdate(authorization, user);
		tokenModule.changePassword(user);
		return Response.noContent().build();
	}

	@Override
	public Response requestResetToken(String email) {
		tokenModule.mailResetToken(email);
		return Response.noContent().build();
	}

	private void validateForPasswordUpdate(String authorization, User user) {
		if (user.getResetToken() != null) {
			validateResetToken(user);
		} else {
			Token token = new Token();
			token.setFromCookie(false);
			token.setJwtToken(authorization);
			verificationModule.verifyIdenticalUserOrAdmin(token, user.getId());
		}
	}

	private void validateResetToken(User user) {
		User storedUser = userFacade.getByUsername(user.getUsername());
		if (storedUser == null || !user.getResetToken().equals(storedUser.getResetToken())) {
			throw new NotAuthorizedException("update not allowed");
		}
	}
}
