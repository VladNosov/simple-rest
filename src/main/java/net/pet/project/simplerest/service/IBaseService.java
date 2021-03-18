package net.pet.project.simplerest.service;

import net.pet.project.simplerest.exception.NotFoundException;
import net.pet.project.simplerest.model.IEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Interface for base CRUD service for {@link IEntity}
 * @param <E> - entity that stored in DB
 * @param <T> - identifier type
 * @author Vlad Nosov
 */
public interface IBaseService<E extends IEntity<T>, T> {

    List<E> getAll();

    Page<E> getAll(Pageable page);

    E get(T id) throws NotFoundException;

    Optional<E> find(T id);

    boolean exist(T id);

    E create(E entity);

    E update(E entity) throws NotFoundException;

    void delete(T id) throws NotFoundException;
}