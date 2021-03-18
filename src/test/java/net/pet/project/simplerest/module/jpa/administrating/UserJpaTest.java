package net.pet.project.simplerest.module.jpa.administrating;

import net.pet.project.simplerest.config.JpaTestWithPostgres;
import net.pet.project.simplerest.model.administrating.User;
import net.pet.project.simplerest.module.jpa.AbstractJpaTest;
import net.pet.project.simplerest.repository.administrating.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static net.pet.project.simplerest.util.ConstraintViolationUtils.getFirstMessage;
import static net.pet.project.simplerest.util.TestData.createValidUserWithoutId;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@JpaTestWithPostgres
@DisplayName("JPA: User")
public class UserJpaTest extends AbstractJpaTest {

    @Autowired
    private UserRepository repository;

    protected void cleanup() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("save(user)")
    class Save {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("create valid User (id=null)")
            public void save_withoutId_saveInDB() {
                User inDB = repository.saveAndFlush(createValidUserWithoutId());

                assertAll(() -> assertThat(inDB).isNotNull(),
                        () -> assertThat(inDB.getId()).isNotNull(),
                        () -> assertThat(repository.count()).isEqualTo(1),
                        () -> assertThat(repository.existsById(inDB.getId())).isTrue());
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {
            @DisplayName("create User with invalid login")
            @MethodSource("net.pet.project.simplerest.util.TestData#createBlankWithId")
            @ParameterizedTest(name = "id=\"{1}\" login({0}) => 'must not be blank'")
            public void save_withInvalidLogin_throwConstraintViolationException(String value, Long id) {
                User user = createValidUserWithoutId();
                user.setId(id);
                user.setLogin(value);

                ConstraintViolationException ex = Assertions.assertThrows(
                        ConstraintViolationException.class, () -> repository.saveAndFlush(user));

                assertAll(() -> assertThat(ex.getConstraintViolations()).isNotNull(),
                        () -> assertThat(ex.getConstraintViolations().size()).isEqualTo(1),
                        () -> assertThat(getFirstMessage(ex, "login")).isEqualTo("must not be blank"));
                System.out.println("");
            }

            @Test
            @DisplayName("not unique login => throw DataIntegrityViolationException")
            public void save_withNotUniqueLogin_throwDataIntegrityViolationException() {
                repository.saveAndFlush(createValidUserWithoutId());

                DataIntegrityViolationException ex = Assertions.assertThrows(
                        DataIntegrityViolationException.class, () -> repository.saveAndFlush(createValidUserWithoutId()));

                assertAll(() -> assertThat(ex.getMessage()).isNotNull(),
                        () -> assertThat(ex.getMessage()).contains("constraint [users_login_key]"));
            }
        }
    }

    @Nested
    @DisplayName("deleteById(id)")
    class DeleteById {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("delete(id) => delete from DB")
            public void delete_idExist_deleteFromDB() {
                User user = repository.saveAndFlush(createValidUserWithoutId());

                repository.deleteById(user.getId());

                assertAll(() -> assertThat(repository.count()).isEqualTo(0),
                        () -> assertThat(repository.existsById(user.getId())).isFalse());
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {

            @Test
            @DisplayName("delete when id not exist => throw EmptyResultDataAccessException")
            public void delete_idNotExist_throwEmptyResultDataAccessException() {
                EmptyResultDataAccessException ex = Assertions.assertThrows(
                        EmptyResultDataAccessException.class, () -> repository.deleteById(1L));

                assertAll(() -> assertThat(ex.getMessage()).isNotNull(),
                        () -> assertThat(ex.getMessage()).matches("No class .+ with id [0-9]+ exists!"));
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
            @DisplayName("findByLogin(login exist) => return user")
            public void findByLogin_userExist_returnUser() {
                User user = repository.saveAndFlush(createValidUserWithoutId());

                Optional<User> actual = repository.findByLogin(user.getLogin());

                assertAll(() -> assertThat(actual.isPresent()).isTrue(),
                        () -> assertThat(actual.get().getId()).isEqualTo(user.getId()));
            }

            @Test
            @DisplayName("findByLogin(login not exist) => return null")
            public void findByLogin_userNotExist_returnNull() {
                repository.saveAndFlush(createValidUserWithoutId());

                Optional<User> actual = repository.findByLogin("notExistLogin");

                assertThat(actual.isPresent()).isFalse();
            }
        }
    }
}