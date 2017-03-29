package nl.ordina.jwt.model;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public class SaltedValue {
	private String salt;
	private String value;

	public SaltedValue(String value, String salt) {
		this.value = value;
		this.salt = salt;
	}

	public String getSalt() {
		return salt;
	}

	public String getValue() {
		return value;
	}
}
