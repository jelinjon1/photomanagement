/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package photomanagement.faces;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import photomanagement.faces.model.Photo;
import photomanagement.services.PhotoDatabaseService;

/**
 *
 * @author Jonáš
 */
@Named(value = "collectionPhotosBean")
@SessionScoped
public class CollectionPhotosBean implements Serializable {

    @EJB
    private PhotoDatabaseService photoDatabaseService;
    private DataModel<Photo> photosList;
    private Photo selectedPhoto = null;
    private Long selectedPhotoParameter;

    @PostConstruct
    public void initDB() {
        photosList = new ListDataModel<>(new ArrayList<>(photoDatabaseService.listAllPhotos()));
    }

    /**
     * Load bookmarkable detail page data.
     */
    public void loadSelectedAlbum() {
        selectedPhoto = photoDatabaseService.findPhotoById(selectedPhotoParameter);
    }

    public DataModel<Photo> getPhotosList() {
        photosList = new ListDataModel<>(new ArrayList<>(photoDatabaseService.listAllPhotos()));
        return photosList;
    }

    public void selectPhoto() {
        selectedPhoto = photosList.getRowData();
    }

    public String goToDetailRedirect() {
        selectedPhoto = photosList.getRowData();
        return "photos-detail.xhtml?faces-redirect=true";
    }

    public void clearSelectedPhoto() {
        selectedPhoto = null;
    }

    public Photo getSelectedPhoto() {
        return selectedPhoto;
    }

    public void setSelectedPhoto(Photo selectedPhoto) {
        this.selectedPhoto = selectedPhoto;
    }

    public Long getSelectedPhotoParameter() {
        return selectedPhotoParameter;
    }

    public void setSelectedPhotoParameter(Long selectedPhotoParameter) {
        this.selectedPhotoParameter = selectedPhotoParameter;
    }
}
