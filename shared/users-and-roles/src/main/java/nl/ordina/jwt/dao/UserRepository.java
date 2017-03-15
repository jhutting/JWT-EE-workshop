package nl.ordina.jwt.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.cdi.Eager;

/**
 * @author Johan Hutting (Johan.Hutting@Ordina.nl)
 */
@Eager
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	UserEntity findByUsername(String username);
}
