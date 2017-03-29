package nl.ordina.jwt.exception;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@Provider
public class NotAuthorizedExceptionHandler implements ExceptionMapper<NotAuthorizedException> {
	@Override
	public Response toResponse(NotAuthorizedException e) {
		return Response.status(Response.Status.UNAUTHORIZED).build();
	}
}
