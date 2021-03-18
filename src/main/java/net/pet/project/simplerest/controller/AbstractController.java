package net.pet.project.simplerest.controller;

import io.swagger.v3.oas.annotations.Operation;
import net.pet.project.simplerest.dto.IDto;
import net.pet.project.simplerest.exception.ControllerException;
import net.pet.project.simplerest.model.IEntity;
import net.pet.project.simplerest.service.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Abstract crud realisation for rest controller {@link RestController}
 * @param <E> - entity
 * @param <D> - dto
 * @param <T> - type of identifier
 * @author Vlad Nosov
 */
public abstract class AbstractController<E extends IEntity<T>, D extends IDto, T> {

    private static final String ERROR_MSG = "something went wrong";

    protected final IBaseService<E, T> service;

    public AbstractController(IBaseService<E, T> service) {
        this.service = service;
    }

    /**
     * Return list of entities {@link E}
     */
    @Deprecated
    @Operation(
            summary = "Get all entities",
            description = "Return all entities without pagination and filtration"
    )
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<D> getAll() {
        return Optional.ofNullable(service.getAll())
                .map(l -> l.stream().map(this::toDTO).collect(toList()))
                .orElseThrow(() -> new ControllerException(ERROR_MSG));
    }

    /**
     * Return page with entities {@link E}
     */
    @Operation(
            summary = "Get page with entities",
            description = "Return page with entities without filtering and sorting"
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<D> getPage(@PageableDefault Pageable page) {
        return Optional.ofNullable(service.getAll(page))
                .map(p -> p.map(this::toDTO))
                .orElseThrow(() -> new ControllerException(ERROR_MSG));
    }

    /**
     * Get entity {@link E} with id
     * @param id identifier
     * @return page with entities
     */
    @Operation(
            summary = "Get entity by id",
            description = "Get entity by id"
    )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public D get(@PathVariable("id") T id) {
        return Optional.ofNullable(service.get(id))
                .map(this::toDTO)
                .orElseThrow(() -> new ControllerException(ERROR_MSG));
    }

    /**
     * Create entity {@link E}
     * @param dto entity that need create
     * @return created entity
     */
    @Operation(
            summary = "Create entity",
            description = "Create entity"
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public D create(@Valid @RequestBody D dto) {
        return Optional.of(dto)
                .map(this::toEntity)
                .map(this::checkNew)
                .map(service::create)
                .map(this::toDTO)
                .orElseThrow(() -> new ControllerException(ERROR_MSG));
    }

    /**
     * Update existing entity {@link E}
     * @param id entity identifier
     * @param dto entity that need save
     * @return updated entity
     */
    @Operation(
            summary = "Update entity by id",
            description = "Update existing entity"
    )
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public D update(@PathVariable("id") T id, @Valid @RequestBody D dto) {
        return Optional.of(dto)
                .map(this::toEntity)
                .map(e -> assureIdConsistent(e, id))
                .map(service::update)
                .map(this::toDTO)
                .orElseThrow(() -> new ControllerException(ERROR_MSG));
    }

    /**
     * Delete entity {@link E} with id
     * @param id entity identifier
     */
    @Operation(
            summary = "Delete entity by id",
            description = "Delete entity by id"
    )
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") T id) {
        service.delete(id);
    }

    //================================================= UTIL METHODS ===================================================

    protected abstract E toEntity(D dto);
    protected abstract D toDTO(E entity);

    @SuppressWarnings("unchecked")
    protected String getEntityName() {
        return ((Class<E>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();
    }

    protected E assureIdConsistent(E bean, T id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.getId() != id) {
            throw new IllegalArgumentException(bean + " must be with id=" + id);
        }
        return bean;
    }

    /**
     * Проверка, что сущность не содержит id
     * @throws IllegalArgumentException если сущность содержит id
     */
    protected E checkNew(final E entity) throws IllegalArgumentException{
        Assert.notNull(entity, getEntityName() + " must not be null");
        if (!entity.isNew()) {
            throw new IllegalArgumentException(getEntityName() + " must be new (id=null)");
        }
        return entity;
    }
}