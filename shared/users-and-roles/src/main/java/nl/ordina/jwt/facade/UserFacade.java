package nl.ordina.jwt.facade;

import nl.ordina.jwt.model.User;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public interface UserFacade {
	User getByUsername(final String username);

	User getById(Long id);

	void createUser(final User user);

	void updateUser(final long id, final User user);
}
