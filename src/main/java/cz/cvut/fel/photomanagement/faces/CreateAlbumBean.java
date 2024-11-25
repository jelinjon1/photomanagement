/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.time.LocalDate;
import java.util.List;
import cz.cvut.fel.photomanagement.faces.model.Album;
import cz.cvut.fel.photomanagement.faces.model.Photo;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;

/**
 *
 * @author Jonáš
 */
@Named(value = "createAlbumBean")
@RequestScoped
public class CreateAlbumBean {

    @EJB
    private AlbumDatabaseService albumDatabaseService;
    private List<Photo> photos;
    private String name;
    private String description;
    private LocalDate created;
    private LocalDate lastEdited;
    private String coverImage;

    public CreateAlbumBean() {
    }

    public String createAlbumAndRedirect() {
        Album temp = new Album();
        temp.setName(this.name);
        temp.setDescription(this.description);
        albumDatabaseService.saveAlbum(temp);
        return "albums.xhtml?redirect=true";
    }

    public AlbumDatabaseService getAlbumDatabaseService() {
        return albumDatabaseService;
    }

    public void setAlbumDatabaseService(AlbumDatabaseService albumDatabaseService) {
        this.albumDatabaseService = albumDatabaseService;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(LocalDate lastEdited) {
        this.lastEdited = lastEdited;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

}
