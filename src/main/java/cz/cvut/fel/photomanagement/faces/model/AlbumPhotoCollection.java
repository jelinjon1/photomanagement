/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jonáš
 */
public class AlbumPhotoCollection {

    private List<AlbumPhoto> photos;
    private int importance;
    private boolean lowImportance;
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

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public boolean isLowImportance() {
        return lowImportance;
    }

}
