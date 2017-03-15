package nl.ordina.jwt.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@Provider
public class DefaultExceptionHandler implements ExceptionMapper<RuntimeException> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandler.class);

	@Override
	public Response toResponse(RuntimeException e) {
		String logID = UUID.randomUUID().toString();
		LOGGER.error("Error occured in REST service with ID [" + logID + "]: ", e);
		return Response.serverError().entity(logID).build();
	}
}
