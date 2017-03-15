package nl.ordina.jwt.producer;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
public class EntityManagerProducer {

	@PersistenceContext(name = "users")
	private EntityManager entityManager;

	@Produces
	@Dependent
	public EntityManager getEntityManager() {
		return entityManager;
	}
}
