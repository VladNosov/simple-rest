package net.pet.project.simplerest.service.administrating;

import lombok.extern.slf4j.Slf4j;
import net.pet.project.simplerest.exception.NotFoundException;
import net.pet.project.simplerest.exception.ServiceException;
import net.pet.project.simplerest.model.administrating.User;
import net.pet.project.simplerest.repository.administrating.UserRepository;
import net.pet.project.simplerest.service.AbstractService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

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

    @NotNull
    public User getByLogin(@Nullable String login) throws NotFoundException {
        return findByLogin(login).orElseThrow(() -> new NotFoundException("login", login));
    }

    @NotNull
    public Optional<User> findByLogin(@Nullable String login) {
        Assert.notNull(login, "login must not be null");
        log.info("find user by login {}", login);
        return repository.findByLogin(login);
    }

    //================================================= VALIDATE METHODS ===============================================

    @Override
    protected User validateBeforeCreate(User entity) {
        return checkLoginUnique(super.validateBeforeCreate(entity));
    }

    @Override
    protected User validateBeforeUpdate(User entity) {
        return checkLoginUnique(super.validateBeforeUpdate(entity));
    }

    /**
     * Check that login {@link User#getLogin()} is unique
     * @throws ServiceException if login isn't unique
     * @return not modified user
     */
    protected User checkLoginUnique(User entity) throws ServiceException {
        Optional.ofNullable(entity)
                .map(User::getLogin)
                .flatMap(repository::findByLogin)
                .ifPresent(e -> {
                    if (Objects.isNull(entity.getId()) || !entity.getId().equals(e.getId())) {
                        throwNotUniqueException("login", e.getLogin());
                    }
                });
        return entity;
    }
}