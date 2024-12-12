/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.comparator.SortByDateTaken;
import cz.cvut.fel.photomanagement.faces.model.Album;
import cz.cvut.fel.photomanagement.faces.model.Photo;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;
import cz.cvut.fel.photomanagement.services.FileLoader;
import cz.cvut.fel.photomanagement.services.PhotoDatabaseService;
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
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private DataModel<Photo> photosDataModel;
    @Inject
    private FileLoader fileLoader;
    private DataModel<File> filesDataModel;
    private String filesPath;
    private ArrayList<String> paths;


    @PostConstruct
    public void initDB() {
        paths = new ArrayList<>();
        albumOptions = albumDatabaseService.listAllAlbums();

//        if (photosDataModel != null) {
//            for (Photo photo : photosDataModel) {
//                selectedPhotos.put(photo.getId(), false);
//            }
//        }
    }

    public void updatePhoto(Photo photo) {
        photoDatabaseService.update(photo);
        System.out.println(photo);
    }

    public void loadFiles() throws IOException, NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            sb.append("/");
            sb.append(path);
        }
        filesPath = sb.toString();
        List<File> allFiles = fileLoader.loadFiles(filesPath);

        filesDataModel = new ListDataModel<>(allFiles.stream()
                .filter(File::isDirectory)
                .collect(Collectors.toList()));

        List<Photo> photosList = new ArrayList<>();
        List<Photo> existingPhotos = photoDatabaseService.findAllExistingPhotos(filesPath);

        Map<String, Photo> fileNameMap = existingPhotos
                .stream()
                .collect(Collectors.toMap(p -> p.getFileName(), p -> p));

        for (File file : allFiles) {
            if (file.isFile() && isPhoto(file)) {
                Photo existingPhoto = fileNameMap.get(file.getName());
                if (existingPhoto != null) {
                    photosList.add(existingPhoto);
                    System.out.println("Loaded existing photo: " + existingPhoto);
                } else {
                    Photo photo = new Photo(file, filesPath);
                    photoDatabaseService.savePhoto(photo);
                    photosList.add(photo);
                    System.out.println("Saved new photo: " + photo);
                }
            }
        }
        Comparator dateComparator = new SortByDateTaken();
        photosList.sort(dateComparator);
        photosDataModel = new ListDataModel<>(photosList);
    }


    private boolean isPhoto(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
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
        return "photos.xhtml";
    }

    /*
    check if photo is already inside /bin
        if so: delete permanently
    else:
    check if ./bin exists
        if so: move photo to bin
        else: create ./bin and move photo
     */
    public void deletePhoto(Photo photo) {
        if ((photo.getLocalPath().endsWith("/bin"))) {
            photoDatabaseService.deletePhoto(photo);
        } else {
            fileLoader.createDirectory(photo.getLocalPath(), "bin");
            fileLoader.moveToBin(photo.getFileName(), photo.getLocalPath());
        }

//        if (photo != null) {
//            List<Photo> photos = (List<Photo>) photosDataModel.getWrappedData();
//            photos.removeIf(p -> p == photo);
//            photosDataModel = new ListDataModel<>(photos);
//
//            photoDatabaseService.remove(photo);
//        }
    }

    public void addToAlbum() {
        Album album = albumDatabaseService.findAlbumById(selectedAlbumId); // Retrieve album by selected ID
        if (album == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Album not found", null));
            return;
        }

        for (Photo photo : photosDataModel) {
            album.addPhoto(photo);
        }

        albumDatabaseService.update(album);
//        selectedPhotos.replaceAll((id, selected) -> false);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Photos added to album successfully", null));
    }

    public String formatLastModified(long lastModified) {
        LocalDateTime dateTime = Instant.ofEpochMilli(lastModified)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
        return dateTime.format(formatter);
    }

    public void addToPaths(String fileName) {
        Objects.requireNonNull(fileName);
        if (!fileName.equals("")) {
            paths.add(fileName);
        }
    }

    public int getSelectedPhotoCount() {
        return (int) selectedPhotos.values().stream().filter(Boolean::booleanValue).count();
    }

    public void loadSelectedAlbum() {
        selectedPhoto = photoDatabaseService.findPhotoById(selectedPhotoParameter);
    }

    public DataModel<Photo> getPhotosDataModel() {
        return photosDataModel;
    }

    public void selectPhoto() {
        selectedPhoto = photosDataModel.getRowData();
    }

    public String goToDetailRedirect() {
        selectedPhoto = photosDataModel.getRowData();
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

    public DataModel<File> getFilesDataModel() {
        return filesDataModel;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
    }

}
