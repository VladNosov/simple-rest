package net.pet.project.simplerest.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.pet.project.simplerest.exception.ApplicationException;
import net.pet.project.simplerest.exception.NotFoundException;
import net.pet.project.simplerest.exception.ServiceException;
import net.pet.project.simplerest.model.IEntity;
import net.pet.project.simplerest.repository.IBaseRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Abstract CRUD service realisation for {@link IEntity}
 * @param <E> - entity that stored in DB
 * @param <T> - identifier type
 * @param <R> - repository {@link IBaseRepository} for work with {@link E}
 * @author VN
 */
@Slf4j
@Transactional(readOnly = true)
public abstract class AbstractService<E extends IEntity<T>, T, R extends IBaseRepository<E, T>> implements IBaseService<E, T> {

    protected final R repository;

    public AbstractService(R repository) {
        this.repository = repository;
    }

    @Override
    public List<E> getAll() {
        log.info("get all entity");
        return repository.findAll();
    }

    @Override
    public Page<E> getAll(Pageable page) {
        log.info("get all entity");
        return repository.findAll(page);
    }

    @NotNull
    @Override
    public E get(T id) throws NotFoundException {
        return find(id).orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Optional<E> find(T id) {
        Assert.notNull(id, "id must not be null");
        log.info("get entity by id {}", id);
        return repository.findById(id);
    }

    @Override
    public boolean exist(T id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public E create(E entity) {
        E created = repository.save(checkNew(validateBeforeCreate(entity)));
        log.info("create entity {}", created);
        return created;
    }

    @Override
    @Transactional
    public E update(E entity) throws NotFoundException {
        E saved = repository.save(checkExist(validateBeforeUpdate(entity)));
        log.info("updated entity {} with id={}", saved, saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public void delete(T id) throws NotFoundException {
        repository.deleteById(preDelete(checkExist(id)));
        log.info("delete entity by {}", id);
    }

    //================================================= CUSTOMIZE METHODS ==============================================

    /**
     * Entity validation before call method {@link AbstractService#create(IEntity)}
     * Override this method if you need to add custom logic before create
     * IMPORTANT: this method must be pure (doesn't modif <strong>entity</strong>)
     * @return not modified entity
     */
    protected E validateBeforeCreate(final E entity) {
        //override me for customize
        return entity;
    }

    /**
     * Entity validation before call method {@link AbstractService#update(IEntity)}
     * Override this method if you need to add custom logic before update
     * IMPORTANT: this method must be pure (doesn't modif <strong>entity</strong>)
     * @return not modified entity
     */
    protected E validateBeforeUpdate(final E entity) {
        //override me for customize
        return entity;
    }

    /**
     *
     * Preparatory actions before entity was delete. For example, remove association in outer table
     * IMPORTANT: this method must be pure (doesn't modif <strong>entity</strong>)
     * @return not modified id
     */
    protected T preDelete(@NonNull final T id) {
        //override me for customize
        return id;
    }

    //================================================= UTIL METHODS ===================================================

    protected String getEntityName() {
        return getEntityType().getSimpleName();
    }

    @SuppressWarnings("unchecked")
    protected Class<E> getEntityType() {
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected void throwNotUniqueException(final String fieldName, final String value) throws ServiceException {
        throwNotUniqueException(getEntityName(), fieldName, value);
    }

    protected void throwNotUniqueException(final String entityName,
                                           final String fieldName,
                                           final String value) throws ServiceException {
        throw new ApplicationException(format(
                "%s with %s '%s' already exist",
                entityName,
                fieldName,
                value
        ));
    }

    protected E checkExist(final E entity) {
        Assert.notNull(entity, getEntityName() + " must not be null");
        checkExist(entity.getId());
        return entity;
    }

    /**
     * Check that entity with id is exist
     * @throws IllegalArgumentException if id = null
     * @throws NotFoundException if entity doesn't exist
     */
    protected T checkExist(T id) {
        Assert.notNull(id, "id must not be null");
        if (!repository.existsById(id)) {
            throw new NotFoundException(id);
        }
        return id;
    }

    /**
     * Check that entity new (id is null)
     * @throws IllegalArgumentException if entity contains nonNull id
     */
    protected E checkNew(final E entity) throws IllegalArgumentException {
        Assert.notNull(entity, getEntityName() + " must not be null");
        if (!entity.isNew()) {
            throw new IllegalArgumentException(getEntityName() + " must be new (id=null)");
        }
        return entity;
    }
}