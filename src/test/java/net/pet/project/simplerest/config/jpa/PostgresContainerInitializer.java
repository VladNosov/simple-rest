package net.pet.project.simplerest.config.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.testcontainers.containers.wait.strategy.Wait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * DB Initialisation for tests
 *
 * Call before start spring context and run postgres image with created tables
 * @author Vlad Nosov
 */
@Slf4j
public class PostgresContainerInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        PsqlContainer postgres = PsqlContainer.getInstance()
                .withDatabaseName("simple")
                .withUsername("admin")
                .withPassword("admin")
                .withExposedPorts(5432)
                .waitingFor(Wait.forListeningPort());
        postgres.start();

        final String url = postgres.getJdbcUrl();
        final int port = postgres.getMappedPort(5432);
        final String username = postgres.getUsername();
        final String password = postgres.getPassword();
        StringBuilder msg = new StringBuilder();
        msg.append("\n\n***********************************************")
                .append("\n******   INITIALIZE Postgres FOR TEST    ******")
                .append("\n***********************************************")
                .append(String.format("\n   (%s:%s)  %s(%s) \n\n", username, password, url, port));
        log.info(msg.toString());
        TestPropertyValues values = TestPropertyValues.of(
                "spring.datasource.url=" + url,
                "spring.datasource.username=" + username,
                "spring.datasource.password=" + password,
                "spring.test.database.replace=none",
                "spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true",
                "spring.datasource.driver-class-name=org.postgresql.Driver"
        );

        values.applyTo(applicationContext);

        applicationContext.registerBean(PsqlContainer.class, () -> postgres,
                beanDefinition -> beanDefinition.setDestroyMethodName("stop"));
        checkConnection();
    }

    private void checkConnection() {
        try {
            //todo 21.03.18 doesn't work wait while image start, so sleep can help
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PsqlContainer db = PsqlContainer.getInstance();
        try(Connection conn = DriverManager.getConnection(db.getJdbcUrl(), db.getUsername(), db.getPassword())) {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM USERS");
            resultSet.next();
            log.info("\n\n=====   DB START Success: {}   =====\n", resultSet.getString(2));
        } catch (SQLException e) {}
    }
}