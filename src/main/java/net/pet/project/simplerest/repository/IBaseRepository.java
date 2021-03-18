package net.pet.project.simplerest.repository;

import net.pet.project.simplerest.model.IEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Common realisation repository layer for entity {@link IEntity}
 * @param <E> - entity that stored in DB
 * @param <T> - identifier type
 * @author Vlad Nosov
 */
@NoRepositoryBean
public interface IBaseRepository<E extends IEntity<T>, T> extends JpaRepository<E, T>, JpaSpecificationExecutor<E> {
}