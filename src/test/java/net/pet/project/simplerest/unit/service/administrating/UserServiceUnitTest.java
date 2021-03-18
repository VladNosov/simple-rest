package net.pet.project.simplerest.unit.service.administrating;

import net.pet.project.simplerest.exception.ApplicationException;
import net.pet.project.simplerest.exception.NotFoundException;
import net.pet.project.simplerest.model.administrating.User;
import net.pet.project.simplerest.repository.administrating.UserRepository;
import net.pet.project.simplerest.service.administrating.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static java.lang.String.format;
import static net.pet.project.simplerest.util.TestData.createValidUserWithId;
import static net.pet.project.simplerest.util.TestData.createValidUserWithoutId;
import static net.pet.project.simplerest.util.TestData.withId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Unit: UserService")
public class UserServiceUnitTest {

    @Mock
    private UserRepository repository;

    private UserService service;

    @BeforeEach
    void initMock() {
        MockitoAnnotations.openMocks(this);
        service = new UserService(repository);
    }

    @Nested
    @DisplayName("get(id)")
    class Get {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("get(id exist in DB) => repository.findById(id)")
            public void get_whenIdIsExist_callRepositoryFindById() {
                User entity = createValidUserWithId();

                when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
                service.get(entity.getId());

                verify(repository, times(1)).findById(anyLong());
            }

            @Test
            @DisplayName("get(id exist in DB) => return entity != null")
            public void get_whenIdIsExist_returnNonNull() {
                User entity = createValidUserWithId();

                when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
                User actual = service.get(entity.getId());

                assertThat(actual).isEqualTo(entity);
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {

            @Test
            @DisplayName("get(null) => throw IllegalArgumentException(\"id must not be null\")")
            public void get_whenIdIsNull_throwIllegalArgumentException() {
                IllegalArgumentException ex = Assertions.assertThrows(
                        IllegalArgumentException.class, () -> service.get(null));

                assertThat(ex.getMessage()).isEqualTo("id must not be null");
                verify(repository, never()).findById(anyLong());
            }

            @Test
            @DisplayName("get(id not exist in DB) => throw NotFoundException(\"Entity with id [1] not found\")")
            public void get_whenIdIsNotExist_throwNotFoundException() {
                when(repository.findById(anyLong())).thenReturn(Optional.empty());
                NotFoundException ex = Assertions.assertThrows(
                        NotFoundException.class, () -> service.get(1L));

                assertThat(ex.getMessage()).isEqualTo("Entity with id [1] not found");
                verify(repository, times(1)).findById(anyLong());
            }
        }
    }

    @Nested
    @DisplayName("getByLogin(login)")
    class GetByLogin {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("getByLogin(login exist in DB) => repository.getByLogin(login)")
            public void getByLogin_whenLoginIsExist_callRepositoryFindByLogin() {
                User entity = createValidUserWithId();

                when(repository.findByLogin(entity.getLogin())).thenReturn(Optional.of(entity));
                User actual = service.getByLogin(entity.getLogin());

                assertThat(actual).isEqualTo(entity);
                verify(repository, times(1)).findByLogin(anyString());
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {

            @Test
            @DisplayName("getByLogin(null) => throw IllegalArgumentException(\"login must not be null\")")
            public void getByLogin_whenLoginIsNull_throwIllegalArgumentException() {
                IllegalArgumentException ex = Assertions.assertThrows(
                        IllegalArgumentException.class, () -> service.getByLogin(null));

                assertThat(ex.getMessage()).isEqualTo("login must not be null");
                verify(repository, never()).findByLogin(anyString());
            }

            @Test
            @DisplayName("getByLogin(login not exist in DB) => throw NotFoundException(\"Entity with login not found\")")
            public void getByLogin_whenLoginIsNotExist_throwNotFoundException() {
                when(repository.findByLogin(anyString())).thenReturn(Optional.empty());
                NotFoundException ex = Assertions.assertThrows(
                        NotFoundException.class, () -> service.getByLogin("login"));

                assertThat(ex.getMessage()).isEqualTo("Entity with login [login] not found");
                verify(repository, times(1)).findByLogin(anyString());
            }
        }
    }

    @Nested
    @DisplayName("findByLogin(login)")
    class FindByLogin {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("findByLogin(login exist in DB) => repository.findByLogin(login)")
            public void findByLogin_whenLoginIsExist_callRepositoryFindByLogin() {
                User entity = createValidUserWithId();

                when(repository.findByLogin(entity.getLogin())).thenReturn(Optional.of(entity));
                Optional<User> actual = service.findByLogin(entity.getLogin());

                assertAll(() -> assertThat(actual.isPresent()).isTrue(),
                        () -> assertThat(actual.get()).isEqualTo(entity));
                verify(repository, times(1)).findByLogin(anyString());
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {

            @Test
            @DisplayName("findByLogin(null) => throw IllegalArgumentException(\"login must not be null\")")
            public void findByLogin_whenLoginIsNull_throwIllegalArgumentException() {
                IllegalArgumentException ex = Assertions.assertThrows(
                        IllegalArgumentException.class, () -> service.findByLogin(null));

                assertThat(ex.getMessage()).isEqualTo("login must not be null");
                verify(repository, never()).findByLogin(anyString());
            }

            @Test
            @DisplayName("findByLogin(login not exist in DB) => return null")
            public void findByLogin_whenLoginIsNotExist_returnNull() {
                when(repository.findByLogin(anyString())).thenReturn(Optional.empty());
                Optional<User> actual = service.findByLogin("login");

                assertThat(actual.isEmpty()).isTrue();
                verify(repository, times(1)).findByLogin(anyString());
            }
        }
    }

    @Nested
    @DisplayName("create(User)")
    class Create {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("create(valid user) => repository.save(user)")
            public void create_whenIsValid_callRepositorySave() {
                User entity = createValidUserWithoutId();

                service.create(entity);

                verify(repository, times(1)).save(entity);
            }

            @Test
            @DisplayName("create(valid user) => return user")
            public void create_whenIsValid_returnCreatedEntity() {
                when(repository.save(createValidUserWithoutId())).thenReturn(createValidUserWithId());
                User actual = service.create(createValidUserWithoutId());

                assertAll(() -> assertThat(actual).isNotNull(),
                        () -> assertThat(actual).isEqualTo(createValidUserWithId()));
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {

            @Test
            @DisplayName("create(null) => throw IllegalArgumentException(\"User must not be null\")")
            public void create_whenEntityIsNull_throwIllegalArgumentException() {
                IllegalArgumentException ex = Assertions.assertThrows(
                        IllegalArgumentException.class, () -> service.create(null));

                assertThat(ex.getMessage()).isEqualTo("User must not be null");
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("create(id != null) => throw IllegalArgumentException(\"User must be new (id=null)\")")
            public void create_whenIdIsNotNull_throwIllegalArgumentException() {
                User entity = createValidUserWithoutId();
                entity.setId(1L);

                IllegalArgumentException ex = Assertions.assertThrows(
                        IllegalArgumentException.class, () -> service.create(entity));

                assertThat(ex.getMessage()).isEqualTo("User must be new (id=null)");
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("create(user with not unique login) => throw ApplicationException(\"User with login already exist\")")
            public void create_whenLoginIsNotUnique_throwApplicationException() {
                User entity = createValidUserWithoutId();

                when(repository.findByLogin(entity.getLogin())).thenReturn(Optional.of(createValidUserWithId()));
                ApplicationException ex = Assertions.assertThrows(
                        ApplicationException.class, () -> service.create(entity));

                assertThat(ex.getMessage()).isEqualTo(format("User with login '%s' already exist", entity.getLogin()));
                verify(repository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("update(User)")
    class Update {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("update(valid User with exist id) => repository.save(user)")
            public void update_whenIsValidAndIdExist_callRepositorySave() {
                User entity = createValidUserWithId();

                when(repository.existsById(entity.getId())).thenReturn(true);
                when(repository.save(entity)).thenReturn(entity);
                service.update(entity);

                verify(repository, times(1)).save(entity);
            }

            @Test
            @DisplayName("update(valid User) => return User != null")
            public void update_whenIsValid_returnCreatedEntity() {
                when(repository.save(createValidUserWithId())).thenReturn(createValidUserWithId());
                when(repository.existsById(anyLong())).thenReturn(true);
                User actual = service.update(createValidUserWithId());

                assertAll(() -> assertThat(actual).isNotNull(),
                        () -> assertThat(actual).isEqualTo(createValidUserWithId()));
            }

            @Test
            @DisplayName("update(User login not changed) => not throw unique login validation ex")
            public void update_whenByLoginFindSameEntity_notThrowIllegalArgumentException() {
                User entity = createValidUserWithId();

                when(repository.existsById(entity.getId())).thenReturn(true);
                when(repository.findByLogin(entity.getLogin())).thenReturn(Optional.of(createValidUserWithId()));
                when(repository.save(any(User.class))).thenReturn(entity);
                service.update(entity);

                verify(repository, times(1)).save(any());
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {

            @Test
            @DisplayName("update(null) => throw IllegalArgumentException(\"User must not be null\")")
            public void update_whenEntityIsNull_throwIllegalArgumentException() {
                IllegalArgumentException ex = Assertions.assertThrows(
                        IllegalArgumentException.class, () -> service.update(null));

                assertThat(ex.getMessage()).isEqualTo("User must not be null");
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("update(id == null) => throw IllegalArgumentException(\"id must not be null\")")
            public void update_whenIdIsNull_throwIllegalArgumentException() {
                User entity = createValidUserWithoutId();

                IllegalArgumentException ex = Assertions.assertThrows(
                        IllegalArgumentException.class, () -> service.update(entity));

                assertThat(ex.getMessage()).isEqualTo("id must not be null");
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("update(id not exist in DB) => throw NotFoundException(\"Entity with id not found\")")
            public void update_whenIdNotExistInDB_throwNotFoundException() {
                User entity = createValidUserWithId();

                when(repository.existsById(entity.getId())).thenReturn(false);
                NotFoundException ex = Assertions.assertThrows(
                        NotFoundException.class, () -> service.update(entity));

                assertThat(ex.getMessage()).matches("Entity with id \\[[0-9]+\\] not found");
                verify(repository, never()).save(any());
            }

            @Test
            @DisplayName("update(User with not unique login) => throw ApplicationException(\"User with login already exist\")")
            public void update_whenLoginIsNotUnique_throwApplicationException() {
                User entity = createValidUserWithId();

                when(repository.existsById(entity.getId())).thenReturn(true);
                when(repository.findByLogin(entity.getLogin())).thenReturn(Optional.of(withId(2L, createValidUserWithoutId())));
                ApplicationException ex = Assertions.assertThrows(
                        ApplicationException.class, () -> service.update(entity));

                assertThat(ex.getMessage()).isEqualTo(format("User with login '%s' already exist", entity.getLogin()));
                verify(repository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("delete(id)")
    class Delete {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("delete(id exist) => repository.deleteById(id)")
            public void delete_whenIsValidAndIdExist_callRepositorySave() {
                long id = 1L;

                when(repository.existsById(id)).thenReturn(true);
                service.delete(id);

                verify(repository, times(1)).deleteById(id);
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {

            @Test
            @DisplayName("delete(null) => throw IllegalArgumentException(\"id must not be null\")")
            public void delete_whenIdIsNull_throwIllegalArgumentException() {
                IllegalArgumentException ex = Assertions.assertThrows(
                        IllegalArgumentException.class, () -> service.delete(null));

                assertThat(ex.getMessage()).isEqualTo("id must not be null");
                verify(repository, never()).deleteById(any());
            }

            @Test
            @DisplayName("delete(id not exist in DB) => throw NotFoundException(\"Entity with id not found\")")
            public void delete_whenIdNotExistInDB_throwNotFoundException() {
                long id = 1L;

                when(repository.existsById(id)).thenReturn(false);
                NotFoundException ex = Assertions.assertThrows(
                        NotFoundException.class, () -> service.delete(id));

                assertThat(ex.getMessage()).matches("Entity with id \\[[0-9]+\\] not found");
                verify(repository, never()).deleteById(any());
            }
        }
    }
}