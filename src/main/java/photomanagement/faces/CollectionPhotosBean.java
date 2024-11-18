/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package photomanagement.faces;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import photomanagement.faces.model.Album;
import photomanagement.faces.model.Photo;
import photomanagement.services.AlbumDatabaseService;
import photomanagement.services.PhotoDatabaseService;
import photomanagement.services.PhotoLoader;

/**
 *
 * @author Jonáš
 */
@Named(value = "collectionPhotosBean")
@SessionScoped
public class CollectionPhotosBean implements Serializable {

    @EJB
    private PhotoDatabaseService photoDatabaseService;
    @EJB
    private AlbumDatabaseService albumDatabaseService;
    private DataModel<Photo> photosList;
    private Map<Long, Boolean> selectedPhotos = new HashMap<>();
    private Long selectedAlbumId = 1L;
    private Photo selectedPhoto = null;
    private Long selectedPhotoParameter;
    @Inject
    private PhotoLoader photoLoader;
    private List<Album> albumOptions;
    private int lule = 0;

    public void addToAlbum() {
        Album album = albumDatabaseService.findAlbumById(selectedAlbumId); // Retrieve album by selected ID
        if (album == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Album not found", null));
            return;
        }

        for (Map.Entry<Long, Boolean> entry : selectedPhotos.entrySet()) {
            if (entry.getValue()) { // If selected
                Long photoId = entry.getKey();
                for (Photo photo : photosList) {
                    if (photo.getId().equals(photoId)) {
                        album.addPhoto(photo);
                        break;
                    }
                }
            }
        }

        albumDatabaseService.update(album);
        selectedPhotos.replaceAll((id, selected) -> false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Photos added to album successfully", null));
    }

    public int getSelectedPhotoCount() {
        return (int) selectedPhotos.values().stream().filter(Boolean::booleanValue).count();
    }



    @PostConstruct
    public void initDB() {
        photoLoader.loadAndPersistPhotos();
        photosList = new ListDataModel<>(new ArrayList<>(photoDatabaseService.listAllPhotos()));
        albumOptions = albumDatabaseService.listAllAlbums();

        if (photosList != null) {
            for (Photo photo : photosList) {
                selectedPhotos.put(photo.getId(), false);
            }
        }
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

    public Map<Long, Boolean> getSelectedPhotos() {
        return selectedPhotos;
    }

    public Long getSelectedAlbumId() {
        return selectedAlbumId;
    }

    public void setSelectedAlbumId(Long selectedAlbumId) {
        this.selectedAlbumId = selectedAlbumId;
    }

    public List<Album> getAlbumOptions() {
        return albumOptions;
    }

    public void setAlbumOptions(List<Album> albumOptions) {
        this.albumOptions = albumOptions;
    }
}
