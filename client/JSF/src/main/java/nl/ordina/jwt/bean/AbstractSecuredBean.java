package nl.ordina.jwt.bean;

import nl.ordina.jwt.facade.VerificationFacade;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@ManagedBean
@RequestScoped
public abstract class AbstractSecuredBean {

	private SessionBean session;

	private VerificationFacade verificationFacade;

	@Inject
	public AbstractSecuredBean(SessionBean session, VerificationFacade verificationFacade) {
		this.session = session;
		this.verificationFacade = verificationFacade;
	}

	public AbstractSecuredBean() {
		// CDI...
	}

	public String onLoad() {
		if (session == null || session.getToken() == null || verificationFacade.verifyToken(session.getToken()) != null) {
			return "login";
		}
		return null;
	}
}
