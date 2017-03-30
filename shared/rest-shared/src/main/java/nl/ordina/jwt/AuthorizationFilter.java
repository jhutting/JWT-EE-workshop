package nl.ordina.jwt;

import nl.ordina.jwt.facade.VerificationModule;
import nl.ordina.jwt.model.Role;
import nl.ordina.jwt.model.Token;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	private VerificationModule verificationModule;

	@Inject
	public AuthorizationFilter(VerificationModule verificationModule) {
		this.verificationModule = verificationModule;
	}

	public AuthorizationFilter() {
		// Damn you, CDI-spec.
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Class<?> resourceClass = resourceInfo.getResourceClass();
		List<Role> classRoles = extractRoles(resourceClass);

		Method resourceMethod = resourceInfo.getResourceMethod();
		List<Role> methodRoles = extractRoles(resourceMethod);

		Token token = getTokenFromCookies(requestContext);
		if (token.getJwtToken() == null) {
			token = getTokenFromHeader(requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));
		}

		try {
			// methods take precedence over class checks
			if (methodRoles.isEmpty()) {
				verificationModule.verifyRoleAndToken(classRoles, token);
			} else {
				verificationModule.verifyRoleAndToken(methodRoles, token);
			}
		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
	}

	private Token getTokenFromCookies(ContainerRequestContext requestContext) {
		Token token = new Token();
		Map<String, Cookie> cookies = requestContext.getCookies();
		Cookie cookie = cookies.get("JWT");
		if (cookie != null) {
			token.setJwtToken(cookie.getValue());
		}

		Cookie xsrf = cookies.get("X-XSRF-TOKEN");
		if (xsrf != null) {
			token.setXsrf(xsrf.getValue());
		}

		token.setFromCookie(true);
		return token;
	}

	private Token getTokenFromHeader(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		Token result = new Token();
		result.setFromCookie(false);
		result.setJwtToken(authorizationHeader.substring("Bearer".length()).trim());
		return result;
	}

	// Extract the roles from the annotated element
	private List<Role> extractRoles(AnnotatedElement annotatedElement) {
		if (annotatedElement == null) {
			return new ArrayList<>();
		} else {
			Secured secured = annotatedElement.getAnnotation(Secured.class);
			if (secured == null) {
				return new ArrayList<>();
			} else {
				Role[] allowedRoles = secured.value();
				return Arrays.asList(allowedRoles);
			}
		}
	}
}
