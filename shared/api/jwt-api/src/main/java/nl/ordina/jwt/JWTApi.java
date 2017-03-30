package nl.ordina.jwt;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.ordina.jwt.model.Credentials;
import nl.ordina.jwt.model.User;

@Path("/token")
public interface JWTApi {
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	Response createToken(final Credentials credentials);

	@Path("/updatePassword")
	@Secured
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	Response updatePassword(@HeaderParam("authorization") final String authorization, final User user);

	@Path("/requestResetToken")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	Response requestResetToken(final String email);
}
