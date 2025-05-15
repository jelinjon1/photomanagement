/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.entities.Album;
import cz.cvut.fel.photomanagement.entities.Photo;
import cz.cvut.fel.photomanagement.faces.util.Breadcrumb;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;
import cz.cvut.fel.photomanagement.services.PhotoDatabaseService;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 *
 * @author jelinjon
 */
@Testcontainers
@ExtendWith(MockitoExtension.class)
public class CollectionPhotosBeanTest {

    @InjectMocks
    private PhotoDatabaseService photoDatabaseService;

    @InjectMocks
    private AlbumDatabaseService albumDatabaseService;

    @InjectMocks
    private CollectionPhotosBean collectionPhotosBean;

    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", postgresContainer.getJdbcUrl());
        props.put("jakarta.persistence.jdbc.user", postgresContainer.getUsername());
        props.put("jakarta.persistence.jdbc.password", postgresContainer.getPassword());
        props.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");

        EntityManagerFactory entityManagerFactory
                = Persistence.createEntityManagerFactory("photomanagementTestingPu", props);
        entityManager = entityManagerFactory.createEntityManager();

        photoDatabaseService.setEntityManager(entityManager);
        albumDatabaseService.setEntityManager(entityManager);
        collectionPhotosBean.setPhotoDatabaseService(photoDatabaseService);
        collectionPhotosBean.setAlbumDatabaseService(albumDatabaseService);
    }

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("album_test_db")
            .withUsername("test_user")
            .withPassword("test_password");


    @Test
    void testGetBreadcrumbs() {

        String testPath = "/photos/vacations/vacations01";
        List<Breadcrumb> testBreadcrumbs = Arrays.asList(
                new Breadcrumb("photos", Path.of("photos")),
                new Breadcrumb("vacations", Path.of("photos", "vacations")),
                new Breadcrumb("vacations01", Path.of("photos", "vacations", "vacations01"))
        );
        DataModel<Breadcrumb> expectedDataModel = new ListDataModel(testBreadcrumbs);

        collectionPhotosBean.setFilesPath(testPath);
        DataModel<Breadcrumb> actualDataModel = collectionPhotosBean.getBreadcrumbs();

        assertNotNull(actualDataModel);
        assertThat(actualDataModel).usingRecursiveComparison().isEqualTo(expectedDataModel);
    }

    @Test
    void loadFilesTest() {
        // requires access to local file system
        // could be done via redefining the variable that sets the path in the local system
    }

    @Test
    void extractTime() {
//        // needs an existing test file and an id
//        // requires a different way to get access to the taken time to verify the read data
//        String fileName = "trees.jpg";
//        Path testPath = Paths.get("src", "test", "resources", "images", fileName);
//        File testFile = testPath.toFile();
//        if (testFile.exists()) {
//            System.out.println(collectionPhotosBean.extractDate(testFile));
//        }
    }

    @Test
    void generateThumbnailTest() {
        // sort of tested in the selenium upload photos test
        // needs an existing test file and an id
        // could try to fake with a redefined system path variable
    }

    @Test
    void rotateAndResize() {
        // dont know how to assert rotation
        // could assert scaling down
    }

    @Test
    void testAddToNewAlbum() {
        this.collectionPhotosBean.setSelectedAlbumId(0L);
        String result = collectionPhotosBean.addToAlbum();

        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains("redirect=true"), "Result should contain redirect=true");
    }

    @Test
    void testAddToExistingAlbum() {
        EntityTransaction et = this.entityManager.getTransaction();
        et.begin();

        // create new album, save album in db
        Album newAlbum = new Album();
        newAlbum.setName("Vacation Photos");
        newAlbum.setDescription("Photos from my vacation.");
        albumDatabaseService.saveAlbum(newAlbum);

        // create photo datamodel, save photos in db
        List<Photo> expectedPhotos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Photo temp = new Photo();
            temp.setFileName("photo-" + i);
            photoDatabaseService.savePhoto(temp);
            expectedPhotos.add(temp);
        }
        DataModel<Photo> photosDataModel = new ListDataModel(expectedPhotos);
        collectionPhotosBean.setPhotosDataModel(photosDataModel);
        collectionPhotosBean.setSelectedAlbumId(1L);

        et.commit();

        et = this.entityManager.getTransaction();
        et.begin();

        collectionPhotosBean.addToAlbum();

        et.commit();

        Album persistedAlbum = entityManager.find(Album.class, newAlbum.getId());
        List<Photo> actualPhotos = persistedAlbum
                .getPhotos()
                .stream()
                .map(albumPhoto -> albumPhoto.getPhoto())
                .collect(Collectors.toList());

        assertThat(actualPhotos).usingRecursiveComparison().isEqualTo(expectedPhotos);
    }

}
