package net.pet.project.simplerest.repository.administrating;

import net.pet.project.simplerest.model.administrating.User;
import net.pet.project.simplerest.repository.IBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link User}
 * @author VN
 */
@Repository
public interface UserRepository extends IBaseRepository<User, Long> {
    Optional<User> findByLogin(String login);
}