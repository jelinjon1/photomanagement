package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.entities.Album;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 *
 * @author Jonáš
 */
@Testcontainers
@ExtendWith(MockitoExtension.class)
public class CreateAlbumBeanTest {

    @Mock
    private AlbumDatabaseService albumDatabaseService;

    @Mock
    private CollectionPhotosBean collectionPhotosBean;

    @Mock
    private TableAlbumsBean tableAlbumsBean;

    @InjectMocks
    private CreateAlbumBean createAlbumBean;

    @BeforeEach
    void setUp() {
        createAlbumBean.setAlbumDatabaseService(albumDatabaseService);
        createAlbumBean.setCollectionPhotosBean(collectionPhotosBean);
        createAlbumBean.setTableAlbumsBean(tableAlbumsBean);
    }

    @Test
    void testSaveAlbum() {
        String albumName = "Vacation Photos";
        String albumDescription = "Photos from my vacation.";

        createAlbumBean.setName(albumName);
        createAlbumBean.setDescription(albumDescription);

        doNothing().when(albumDatabaseService).saveAlbum(any(Album.class));

        String result = createAlbumBean.saveAlbum();

        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains("redirect=true"), "Result should contain redirect=true");

        verify(albumDatabaseService).saveAlbum(any(Album.class));
    }

    @Test
    void testCheckAlbumValidity() {
        String albumName = "Vacation Photos";
        createAlbumBean.setName(albumName);

        when(albumDatabaseService.findByName(albumName)).thenReturn(null);

        String result = createAlbumBean.checkAlbumValidity();

        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains("redirect=true"), "Result should contain redirect=true");

        verify(albumDatabaseService).saveAlbum(any(Album.class));
    }

    @Test
    void testCheckAlbumValidityWithDuplicateName() {
        String albumName = "Vacation Photos";
        createAlbumBean.setName(albumName);

        when(albumDatabaseService.findByName(albumName)).thenReturn(new Album());

        String result = createAlbumBean.checkAlbumValidity();

        assertNull(result, "Result should be null if there's a duplicate name");
        assertTrue(createAlbumBean.isDisplayAlert(), "Alert should be displayed for duplicate name");
    }
}
