package net.pet.project.simplerest.config.jpa;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * PostgresSQL container for test
 * @author VN
 */
public class PsqlContainer extends PostgreSQLContainer<PsqlContainer> {
    private static final String IMAGE_VERSION = "postgres:13.2";
    private static PsqlContainer container;

    private PsqlContainer() {
        super(IMAGE_VERSION);
    }

    public static PsqlContainer getInstance() {
        if (container == null) {
            container = new PsqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }
}