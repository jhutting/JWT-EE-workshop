package nl.ordina.jwt.bean;

import nl.ordina.jwt.JWTApi;
import nl.ordina.jwt.model.Credentials;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@ManagedBean
@RequestScoped
public class LoginBean implements Serializable {
	private String username;
	private String password;
	private String ssoEndpoint;

	@Inject
	private transient SessionBean session;

	public LoginBean() {
		loadProperties();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String login() {
		Credentials credentials = new Credentials(username, password);
		String token = getToken(credentials);

		return token == null ? "" : "welcome";
	}

	private String getToken(Credentials credentials) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(ssoEndpoint);

		JWTApi api = target.proxy(JWTApi.class);
		Response response = api.createToken(credentials);
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			return null;
		}

		String token = response.readEntity(String.class);
		session.setToken(token);
		return token;
	}

	private void loadProperties() {
		String fileName = System.getProperty("jboss.server.config.dir") + "/jwt.properties";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
			Properties properties = new Properties();
			properties.load(fis);
			ssoEndpoint = properties.getProperty("sso-endpoint");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
}
