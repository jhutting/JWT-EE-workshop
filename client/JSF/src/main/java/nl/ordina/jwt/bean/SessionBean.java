package nl.ordina.jwt.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@ManagedBean
@SessionScoped
public class SessionBean {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void logout() {
		token = null;
	}
}
