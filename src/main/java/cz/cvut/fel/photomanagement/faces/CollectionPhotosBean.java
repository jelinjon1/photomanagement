/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.faces.model.Album;
import cz.cvut.fel.photomanagement.faces.model.Photo;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;
import cz.cvut.fel.photomanagement.services.FileLoader;
import cz.cvut.fel.photomanagement.services.PhotoDatabaseService;
import cz.cvut.fel.photomanagement.services.PhotoLoader;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private Map<Long, Boolean> selectedPhotos = new HashMap<>();
    private Long selectedAlbumId = 1L;
    private List<Album> albumOptions;
    private Photo selectedPhoto = null;
    private Long selectedPhotoParameter;
    @Inject
    private PhotoLoader photoLoader;
    private DataModel<Photo> photosList;
    @Inject
    private FileLoader fileLoader;
    private DataModel<File> filesList;
    private String filesPath;
    private String filesPathPrefix;
    private ArrayList<String> paths;

    @PostConstruct
    public void initDB() {
        filesList = fileLoader.loadFiles();
        filesPathPrefix = fileLoader.getPhotosDirectoryPath();
        paths = new ArrayList<>();

        photoLoader.loadAndPersistPhotos();
        photosList = new ListDataModel<>(new ArrayList<>(photoDatabaseService.listAllPhotos()));
        albumOptions = albumDatabaseService.listAllAlbums();

        if (photosList != null) {
            for (Photo photo : photosList) {
                selectedPhotos.put(photo.getId(), false);
            }
        }
    }

    public void loadFiles() {
        filesList = fileLoader.loadFiles(filesPath);
    }

    public String navigateTo(String fileName) {
        if (fileName.length() == 0) {
            paths.clear();
        } else if (paths.contains(fileName)) {
            int index = paths.indexOf(fileName);
            for (int i = paths.size() - 1; i >= 0; i--) {
                if (paths.get(i) == fileName) {
                    break;
                } else {
                    paths.remove(i);
                }
            }
        } else {
            paths.add(fileName);
        }

        System.out.println("NAVIGATE TO:");
        System.out.println(fileName);
        return "photos.xhtml";
    }

    public void addToPaths(String fileName) {
        Objects.requireNonNull(fileName);
        paths.add(fileName);
    }

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

    public DataModel<File> getFilesList() {
        return filesList;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    public String getFilesPathPrefix() {
        return filesPathPrefix;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
    }

}
