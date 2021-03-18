package net.pet.project.simplerest.module.jpa;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import static java.lang.String.format;

/**
 * Abstract realisation for Jpa test
 * Contains util methods for work with DB
 * @author VN
 */
public abstract class AbstractJpaTest {

    @Autowired
    protected JdbcTemplate jdbc;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeEach
    public void before() {
        cleanup();
        close();
    }

    @AfterEach
    public void after() {
        cleanup();
        close();
    }
    protected abstract void cleanup();

    protected EntityManager entityManager() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }
        return entityManager;
    }

    //=========================================== SQL ==================================================================

    protected <T> T selectFirstValue(final String table, final String column, Class<T> type) {
        final String query = format("SELECT %1$s FROM %2$s ORDER BY %1$s ASC LIMIT 1", column, table);
        return query(query, type);
    }

    protected int countRowInTable(final String tableName) {
        final String query = format("select count(*) from %s", tableName);
        return jdbc.queryForObject(query, Integer.class);
    }

    protected <T> T query(final String query, Class<T> type) {
        return jdbc.queryForObject(query, type);
    }

    /**
     * Clear tables
     */
    protected void truncate(String... tables) {
        EntityManager em = entityManager();
        em.getTransaction().begin();
        for (String table : tables) {
            em.createNativeQuery(String.format("truncate table %s", table)).executeUpdate();
        }
        em.getTransaction().commit();
    }

    //==================================== PRIVATE METHODS =============================================================

    private void close() {
        if (entityManager != null) {
            entityManager.clear();
            entityManager.close();
            entityManager = null;
        }
    }
}