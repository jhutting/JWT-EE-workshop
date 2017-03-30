package nl.ordina.jwt.facade.impl;

import nl.ordina.jwt.convertor.UserConverter;
import nl.ordina.jwt.dao.UserEntity;
import nl.ordina.jwt.dao.UserRepository;
import nl.ordina.jwt.facade.UserFacade;
import nl.ordina.jwt.model.User;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@Stateless
public class UserFacadeImpl implements UserFacade {
	private UserRepository userRepository;

	@Inject
	public UserFacadeImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserFacadeImpl() {
		// Damn you, CDI-spec.
	}

	@Override
	public User getByUsername(final String username) {
		return UserConverter.convertEntity(userRepository.findByUsername(username));
	}

	@Override
	public User getById(final Long id) {
		return UserConverter.convertEntity(userRepository.getOne(id));
	}

	@Override
	public void createUser(final User user) {
		UserEntity newUser = UserConverter.convertUser(user);
		userRepository.save(newUser);
	}

	@Override
	public void updateUser(final long id, final User user) {
		UserEntity updateUser = userRepository.findOne(id);
		updateUser.setUsername(user.getUsername());
		updateUser.setEmail(user.getEmail());
		// updating Role and password are handled by other facades
	}
}
