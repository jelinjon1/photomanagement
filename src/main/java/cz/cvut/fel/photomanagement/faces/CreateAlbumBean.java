package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.entities.Album;
import cz.cvut.fel.photomanagement.entities.Photo;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A bean that feeds data regarding creation of new Album instances to Faces, handles user input from albums-new.xhtml.
 *
 * @author jelinjon
 */
@Named(value = "createAlbumBean")
@RequestScoped
public class CreateAlbumBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AlbumDatabaseService albumDatabaseService;
    @Inject
    private TableAlbumsBean tableAlbumsBean;
    @Inject
    private CollectionPhotosBean collectionPhotosBean;

    private List<Photo> photos;
    private String name;
    private String description;
    private LocalDate created;
    private LocalDate lastEdited;
    private boolean displayAlert = false;
    private String alertMessage = "";
    private static final Logger log = Logger.getLogger(CreateAlbumBean.class.getName());

    public CreateAlbumBean() {
    }

    public String saveAlbum() {
        Album newAlbum = new Album();

        newAlbum.setName(this.name);
        newAlbum.setDescription(this.description);

        albumDatabaseService.saveAlbum(newAlbum);
        log.log(Level.INFO, "CREATED ALBUM: " + newAlbum);

        //only add when coming from a redirect to create a new album
        if (collectionPhotosBean.isHandlingredirectToNewAlbum()) {
            collectionPhotosBean.addToAlbum(newAlbum);
        }
        return "albums.xhtml?redirect=true";
    }

    public String checkAlbumValidity() {

        // check for duplicate name
        if (albumDatabaseService.findByName(this.name) != null) {
            log.log(Level.WARNING, "DUPLICATE NAME PROVIDED");
            this.displayAlert = true;
            return null;
        }
        saveAlbum();
        tableAlbumsBean.refreshAlbums();
        return "albums.xhtml?redirect=true";
    }

    public void setCollectionPhotosBean(CollectionPhotosBean collectionPhotosBean) {
        this.collectionPhotosBean = collectionPhotosBean;
    }

    public void setTableAlbumsBean(TableAlbumsBean tableAlbumsBean) {
        this.tableAlbumsBean = tableAlbumsBean;
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

    public boolean isDisplayAlert() {
        return displayAlert;
    }

    public void setDisplayAlert(boolean displayAlert) {
        this.displayAlert = displayAlert;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }
}
