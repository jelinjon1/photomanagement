/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces.model;

import java.util.List;

/**
 *
 * @author Jonáš
 */
public class AlbumPhotoCollection {

    private List<AlbumPhoto> photos;
    private int importance;

    public AlbumPhotoCollection(int importance, List<AlbumPhoto> photos) {
        this.photos = photos;
        this.importance = importance;
    }

    public boolean compareImportance(int threshhold) {
        return importance < 10;
    }

    public void pushAlbumPhoto(AlbumPhoto photo) {
        this.photos.add(photo);
    }

    public void appendAlbumPhoto() {

    }

    public List<AlbumPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<AlbumPhoto> photos) {
        this.photos = photos;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

}
