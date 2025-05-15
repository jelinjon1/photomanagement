package cz.cvut.fel.photomanagement.services;

import cz.cvut.fel.photomanagement.entities.Album;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Unit tests for AlbumDatabaseService using test container database.
 *
 * @author jelinjon
 */
@Testcontainers
public class AlbumDatabaseServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("album_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private static EntityManager entityManager;

    @BeforeAll
    static void setUp() {
        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", postgresContainer.getJdbcUrl());
        props.put("jakarta.persistence.jdbc.user", postgresContainer.getUsername());
        props.put("jakarta.persistence.jdbc.password", postgresContainer.getPassword());
        props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");

        EntityManagerFactory entityManagerFactory
                = Persistence.createEntityManagerFactory("photomanagementTestingPu", props);
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    void testSaveAlbum() {
        EntityTransaction et = this.entityManager.getTransaction();
        et.begin();

        AlbumDatabaseService albumDatabaseService = new AlbumDatabaseService();
        albumDatabaseService.setEntityManager(entityManager);

        Album newAlbum = new Album();
        newAlbum.setName("Vacation Photos");
        newAlbum.setDescription("Photos from my vacation.");

        albumDatabaseService.saveAlbum(newAlbum);

        et.commit();

        Album persistedAlbum = entityManager.find(Album.class, newAlbum.getId());
        assertNotNull(persistedAlbum, "Album should be persisted with a generated ID");
        assertEquals(newAlbum.getName(), persistedAlbum.getName(), "Album name should match");
        assertEquals(newAlbum.getDescription(), persistedAlbum.getDescription(), "Album description should match");
    }
    @Test
    void testFindByName() {
        EntityTransaction et = this.entityManager.getTransaction();
        et.begin();

        AlbumDatabaseService albumDatabaseService = new AlbumDatabaseService();
        albumDatabaseService.setEntityManager(entityManager);

        Album newAlbum = new Album();
        newAlbum.setName("Vacation Photos");
        newAlbum.setDescription("Photos from my vacation.");
        albumDatabaseService.saveAlbum(newAlbum);

        et.commit();

        Album foundAlbum = albumDatabaseService.findByName("Vacation Photos");

        assertNotNull(foundAlbum, "Album should be found by name");
        assertEquals(newAlbum.getName(), foundAlbum.getName(), "Album name should match");
        assertEquals(newAlbum.getDescription(), foundAlbum.getDescription(), "Album description should match");
    }
}
