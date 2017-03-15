package nl.ordina.jwt.userservice;

import nl.ordina.jwt.facade.UserFacade;
import nl.ordina.jwt.model.User;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public class UserEndpoint implements UserApi {

	private UserFacade userFacade;

	@Inject
	public UserEndpoint(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	public UserEndpoint() {
		// CDI-spec...
	}

	@Override
	public Response get(final Long id) {
		return Response.ok(userFacade.getById(id)).build();
	}

	@Override
	public Response add(final User user) {
		userFacade.createUser(user);
		return Response.noContent().build();
	}

	@Override
	public Response update(final User user) {
		userFacade.updateUser(user);
		return Response.noContent().build();
	}
}
