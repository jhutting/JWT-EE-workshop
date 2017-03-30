package nl.ordina.jwt.ssoservice;

import nl.ordina.jwt.exception.NotAuthorizedExceptionHandler;

import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public class TokenApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return new HashSet<>(Arrays.asList(TokenEndpoint.class,

				NotAuthorizedExceptionHandler.class));
	}
}
