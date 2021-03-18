package net.pet.project.simplerest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Objects;

/**
 * Interface for entity with identifier {@link T}
 * @param <T> type of identifier
 * @author Vlad Nosov
 */
public interface IEntity<T> extends Serializable {
    T getId();

    void setId(final T id);

    @JsonIgnore
    default boolean isNew() {
        return Objects.isNull(getId());
    }
}