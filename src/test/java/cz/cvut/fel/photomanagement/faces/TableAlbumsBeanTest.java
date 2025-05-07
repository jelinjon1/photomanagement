/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.entities.Album;
import cz.cvut.fel.photomanagement.entities.AlbumPhoto;
import cz.cvut.fel.photomanagement.entities.Photo;
import cz.cvut.fel.photomanagement.faces.model.AlbumPhotoCollection;
import jakarta.faces.model.DataModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author Jonáš
 */
@ExtendWith(MockitoExtension.class)
public class TableAlbumsBeanTest {

    @InjectMocks
    private TableAlbumsBean tableAlbumsBean;

    @Test
    public void sortAlbumPhotosWithoutIgnoreTest() {
        List<Photo> testPhotos = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            Photo temp = new Photo();
            temp.setFileName("photo-" + i);
            temp.setRating(r.nextInt(6));
            testPhotos.add(temp);
        }
        Album testAlbum = new Album(testPhotos, "Test Album", "Test Album Description");
        tableAlbumsBean.setSelectedAlbum(testAlbum);

        tableAlbumsBean.sortAlbumPhotos();
        DataModel<AlbumPhotoCollection> cols = tableAlbumsBean.getImportanceCollections();

        for (Photo photo : testPhotos) {
            boolean photoContained = false;
            for (AlbumPhotoCollection col : cols) {
                if ((photo.getRating() * 2) < tableAlbumsBean.getImportanceThreshold() && !col.isLowImportance()) {
                    continue;
                }
                if ((photo.getRating() * 2) >= tableAlbumsBean.getImportanceThreshold() && col.isLowImportance()) {
                    continue;
                } else {
                    for (AlbumPhoto albumPhoto : col.getPhotos()) {
                        if (albumPhoto.getPhoto() == photo) {
                            photoContained = true;
                            break;
                        }
                    }
                }
            }
            Assertions.assertTrue(photoContained, "Photo {" + photo + "} is not contained in any of the collections.");
        }
    }

    @Test
    public void sortAlbumPhotosWithIgnoreTest() {
        List<Photo> testPhotos = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            Photo temp = new Photo();
            temp.setFileName("photo-" + i);
            temp.setRating(r.nextInt(6));
            testPhotos.add(temp);
        }
        Album testAlbum = new Album(testPhotos, "Test Album", "Test Album Description");
        tableAlbumsBean.setSelectedAlbum(testAlbum);

        tableAlbumsBean.setImportanceIgnore(4);
        tableAlbumsBean.sortAlbumPhotos();
        DataModel<AlbumPhotoCollection> cols = tableAlbumsBean.getImportanceCollections();

        for (Photo photo : testPhotos) {
            for (AlbumPhotoCollection col : cols) {
                if ((photo.getRating() * 2) < tableAlbumsBean.getImportanceThreshold() && !col.isLowImportance()) {
                    continue;
                }
                if ((photo.getRating() * 2) >= tableAlbumsBean.getImportanceThreshold() && col.isLowImportance()) {
                    continue;
                } else {
                    if ((photo.getRating() * 2) < tableAlbumsBean.getImportanceIgnore()) {
                        for (AlbumPhoto albumPhoto : col.getPhotos()) {
                            if (albumPhoto.getPhoto() == photo) {
                                fail("Photo {" + photo + "} is present in a collection, despite its rating being bellow the ignore threshhold.");

                            }
                        }
                    }
                }
            }
        }

    }
}
