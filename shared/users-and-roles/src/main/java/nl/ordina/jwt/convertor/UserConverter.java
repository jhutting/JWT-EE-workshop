package nl.ordina.jwt.convertor;

import nl.ordina.jwt.dao.UserEntity;
import nl.ordina.jwt.model.User;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public class UserConverter {
	public static User convertEntity(UserEntity entity) {
		// to prevent boilerplate we could use Dozer, but UserEntity is small enough not to bother.
		User result = new User();
		result.setId(entity.getId());
		result.setRole(entity.getRole());
		result.setUsername(entity.getUsername());
		result.setEmail(entity.getEmail());
		result.setResetToken(entity.getResetToken());
		// the remaining fields are irrelevant for REST responses.
		return result;
	}

	public static UserEntity convertUser(User user) {
		UserEntity result = new UserEntity();
		result.setUsername(user.getUsername());
		result.setRole(user.getRole());
		result.setEmail(user.getEmail());
		return result;
	}
}
