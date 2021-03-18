package net.pet.project.simplerest.util;

import net.pet.project.simplerest.model.IEntity;
import net.pet.project.simplerest.model.administrating.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

public final class TestData {

    public static <T extends IEntity<?>> Set<T> inSet(T content) {
        return singleton(content);
    }

    public static <T extends IEntity<?>> Page<T> inPage(T content) {
        return inPage(singletonList(content));
    }

    public static <T extends IEntity<?>> Page<T> inPage(List<T> content) {
        return new PageImpl<>(content);
    }

    public static <E extends IEntity<T>, T> E withId(T id, E entity) {
        entity.setId(id);
        return entity;
    }

    public static <T extends IEntity<?>> T withoutId(T entity) {
        entity.setId(null);
        return entity;
    }

    /*
     * CREATE ENTITY
     */

    public static User createValidUserWithId() {
        return withId(1L, createValidUserWithoutId());
    }

    public static User createValidUserWithoutId() {
        return User.builder()
                .login("admin")
                .password("password")
                .build();
    }
}
