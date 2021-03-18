package net.pet.project.simplerest.service.administrating;

import lombok.extern.slf4j.Slf4j;
import net.pet.project.simplerest.model.administrating.User;
import net.pet.project.simplerest.repository.administrating.UserRepository;
import net.pet.project.simplerest.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for {@link User}
 * @author Vlad Nosov
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService extends AbstractService<User, Long, UserRepository> {

    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
    }
}