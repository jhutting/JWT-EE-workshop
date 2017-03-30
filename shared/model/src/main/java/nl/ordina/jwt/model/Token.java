package nl.ordina.jwt.model;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public class Token {
	private String jwtToken;
	private String xsrf;
	private boolean fromCookie = true;

	public boolean isFromCookie() {
		return fromCookie;
	}

	public void setFromCookie(boolean fromCookie) {
		this.fromCookie = fromCookie;
	}

	public String getXsrf() {
		return xsrf;
	}

	public void setXsrf(String xsrf) {
		this.xsrf = xsrf;
	}

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}
}
