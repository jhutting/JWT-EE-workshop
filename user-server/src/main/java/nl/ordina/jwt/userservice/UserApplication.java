package nl.ordina.jwt.userservice;

import nl.ordina.jwt.exception.NotFoundExceptionHandler;

import javax.ws.rs.core.Application;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public class UserApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return new HashSet<>(Arrays.asList(UserEndpoint.class,

				NotFoundExceptionHandler.class));
	}
}
