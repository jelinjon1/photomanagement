package cz.cvut.fel.photomanagement.faces.model;

import cz.cvut.fel.photomanagement.entities.AlbumPhoto;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jonáš
 */
public class AlbumPhotoCollection {

    private List<AlbumPhoto> photos;
    private final boolean lowImportance;
    // todo rethink boolean/int/enum for levels of importance

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
