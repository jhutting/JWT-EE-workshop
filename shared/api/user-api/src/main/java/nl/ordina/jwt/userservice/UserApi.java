package nl.ordina.jwt.userservice;

import nl.ordina.jwt.Secured;
import nl.ordina.jwt.model.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@Path("/user")
public interface UserApi {

	@Path("/{id}")
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	Response get(@PathParam("id") final Long id);

	@Path("/create")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	Response add(final User user);

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	Response update(final User user);
}
