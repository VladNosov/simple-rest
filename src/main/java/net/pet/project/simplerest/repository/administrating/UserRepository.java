package net.pet.project.simplerest.repository.administrating;

import net.pet.project.simplerest.model.administrating.User;
import net.pet.project.simplerest.repository.IBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link User}
 * @author Vlad Nosov
 */
@Repository
public interface UserRepository extends IBaseRepository<User, Long> {
}