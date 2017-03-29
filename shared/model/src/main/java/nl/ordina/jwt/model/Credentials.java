package nl.ordina.jwt.model;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public class Credentials {

	private String username;
	private String password;

	public Credentials() {
		// required for Jackson
	}

	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
