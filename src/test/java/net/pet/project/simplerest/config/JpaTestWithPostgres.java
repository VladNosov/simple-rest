package net.pet.project.simplerest.config;

import net.pet.project.simplerest.config.jpa.PostgresContainerInitializer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Spring Boot JPA test with embedded postgres
 * @author VN
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@ContextConfiguration(initializers = PostgresContainerInitializer.class)
public @interface JpaTestWithPostgres {
}