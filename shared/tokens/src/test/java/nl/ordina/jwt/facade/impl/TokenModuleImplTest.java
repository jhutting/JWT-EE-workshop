package nl.ordina.jwt.facade.impl;

import nl.ordina.jwt.dao.UserEntity;
import nl.ordina.jwt.dao.UserRepository;
import nl.ordina.jwt.model.Credentials;
import nl.ordina.jwt.model.User;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.NotAuthorizedException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenModuleImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private TokenModuleImpl cut;

	@Test
	@Ignore("TODO fix...")
	// private key should be injected
	public void createToken() {
		Credentials credentials = new Credentials("test", "test");
		UserEntity user = new UserEntity();


		when(userRepository.findByUsername("test")).thenReturn(user);
		cut.createToken(credentials);
	}

	@Test
	public void changePassword() {
		User user = new User();
		user.setUsername("test");
		user.setNewPassword("test");
		user.setRepeatedPassword("test");

		UserEntity updatedUser = new UserEntity();
		updatedUser.setResetToken("token to be removed");
		final String originalPassword = "test";
		final String originalSalt = "salt";
		updatedUser.setSaltedPassword(originalPassword);
		updatedUser.setSalt(originalSalt);
		when(userRepository.findByUsername(user.getUsername())).thenReturn(updatedUser);
		cut.changePassword(user);

		assertNull("ResetToken should be removed", updatedUser.getResetToken());
		assertNotSame("saltedPassword should have changed", updatedUser.getSaltedPassword(), originalPassword);
		assertNotSame("salt should have changed", updatedUser.getSalt(), originalSalt);
	}

	@Test(expected = IllegalArgumentException.class)
	public void changePasswordBadRepeat() {
		User user = new User();
		user.setNewPassword("test");
		user.setRepeatedPassword("something else");

		cut.changePassword(user);
	}

	@Test
	public void mailResetToken() {
		final String email = "secure@test.nl";
		UserEntity user = new UserEntity();
		user.setResetToken(null);

		when(userRepository.findByEmail(email)).thenReturn(user);
		cut.mailResetToken(email);

		assertNotNull("resetToken should be filled", user.getResetToken());
	}

	@Test(expected = NotAuthorizedException.class)
	public void mailResetTokenUnknownMail() {
		final String email = "giveMeYourPassword@test.nl";

		when(userRepository.findByEmail(email)).thenReturn(null);
		cut.mailResetToken(email);
	}
}