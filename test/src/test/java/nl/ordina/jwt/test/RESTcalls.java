package nl.ordina.jwt.test;

import nl.ordina.jwt.JWTApi;
import nl.ordina.jwt.model.Credentials;
import nl.ordina.jwt.model.Role;
import nl.ordina.jwt.model.User;
import nl.ordina.jwt.userservice.UserApi;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */

public class RESTcalls {

	private static final long USER_ID = 4L;
	private Map<Role, Credentials> credentialsMap;

	@Before
	public void init() {
		credentialsMap = new EnumMap<>(Role.class);
		Credentials admin = new Credentials("admin", "test-json-web-token");
		credentialsMap.put(Role.ADMIN, admin);
	}

	@Test
	public void testLoginWorks() {
		Response response = getJwtApi().createToken(credentialsMap.get(Role.ADMIN));
		assertEquals("should receive 200 OK", 200, response.getStatus());
		assertNotNull("should receive a String token", response.readEntity(String.class));
	}

	@Test
	public void testLoginNotAuthorized() {
		Credentials credentials = new Credentials("admin", "wrong");
		Response response = getJwtApi().createToken(credentials);
		assertEquals("should receive 401 NOT AUTHORIZED", 401, response.getStatus());
	}

	@Test
	public void testRequestResetToken() {
		Response response = getJwtApi().requestResetToken("admin@localhost");
		assertEquals("should receive 204 OK", 204, response.getStatus());
	}

	@Test
	public void testRequestResetTokenWrongMail() {
		Response response = getJwtApi().requestResetToken("test");
		assertEquals("should receive 401 NOT AUTHORIZED", 401, response.getStatus());
	}

	@Test
	public void testGetUser() {
		Response response = getUserApi(Role.ADMIN).get(USER_ID);
		assertEquals("should receive 200 OK", 200, response.getStatus());
		assertNotNull("should receive User object", response.readEntity(User.class));
	}

	@Test
	public void testUpdateUser() {
		UserApi userApi = getUserApi(Role.ADMIN);
		User user = userApi.get(USER_ID).readEntity(User.class);
		Response response = userApi.update(USER_ID, user);
		assertEquals("should receive 204 OK", 204, response.getStatus());
	}

	private JWTApi getJwtApi() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target("http://localhost:8080/jwt-sso-service");
		return target.proxy(JWTApi.class);
	}

	private UserApi getUserApi(Role role) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		client.register(new AuthorizationHeadersRequestFilter(getToken(role)));
		ResteasyWebTarget target = client.target("http://localhost:8080/jwt-user-service");

		return target.proxy(UserApi.class);
	}

	private String getToken(Role role) {
		Credentials credentials = this.credentialsMap.get(role);
		Response response = getJwtApi().createToken(credentials);
		if (credentials == null || response.getStatus() != 200) {
			return null;
		}
		return response.readEntity(String.class);
	}

	class AuthorizationHeadersRequestFilter implements ClientRequestFilter {

		private final String authToken;

		AuthorizationHeadersRequestFilter(String token) {
			authToken = "Bearer " + token;
		}

		@Override
		public void filter(ClientRequestContext requestContext) throws IOException {
			requestContext.getHeaders().add("Authorization", authToken);
		}
	}
}
