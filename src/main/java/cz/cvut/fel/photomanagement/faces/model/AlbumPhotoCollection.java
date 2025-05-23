package cz.cvut.fel.photomanagement.faces.model;

import cz.cvut.fel.photomanagement.entities.AlbumPhoto;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of {@link cz.cvut.fel.photomanagement.entities.AlbumPhoto} inside an
 * {@link cz.cvut.fel.photomanagement.entities.Album} sorted into smaller collections based on importance value, to be
 * later displayed while differentiating between these collections.
 *
 * @author jelinjon
 */
public class AlbumPhotoCollection {

    private List<AlbumPhoto> photos;
    private final boolean lowImportance;

    public AlbumPhotoCollection(boolean lowImportance, List<AlbumPhoto> photos) {
        this.lowImportance = lowImportance;
        this.photos = photos;
    }

    public AlbumPhotoCollection(boolean lowImportance) {
        this.lowImportance = lowImportance;
        this.photos = new ArrayList<>();
    }

    public void addPhoto(AlbumPhoto photo) {
        photos.add(photo);
    }

    public List<AlbumPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<AlbumPhoto> photos) {
        this.photos = photos;
    }

    public boolean isLowImportance() {
        return lowImportance;
    }

    public int getSize() {
        return this.photos.size();
    }

    @Override
    public String toString() {
        return "AlbumPhotoCollection{" + "photos=" + photos + ", lowImportance=" + lowImportance + '}';
    }
}
