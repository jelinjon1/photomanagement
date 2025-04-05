/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.entities.Album;
import cz.cvut.fel.photomanagement.entities.AlbumPhoto;
import cz.cvut.fel.photomanagement.faces.model.AlbumPhotoCollection;
import cz.cvut.fel.photomanagement.faces.util.CyclicIterator;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;
import cz.cvut.fel.photomanagement.services.FileManager;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Jonáš
 */
@Named(value = "tableAlbumsBean")
@SessionScoped
public class TableAlbumsBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private AlbumDatabaseService albumDatabaseService;
    @Inject
    private FileManager fileLoader;

    private DataModel<Album> albumsDataModel;
    private Album selectedAlbum = null;
    private Long selectedAlbumId = 0L;
    private Long lastSelectedAlbumId = -1L;
    private String selectedAlbumParameter = null;
    private CyclicIterator iterator = null;
    private DataModel<AlbumPhotoCollection> importanceCollections;
    private int importanceThreshold = 5;
    private Integer importanceIgnore = null;
    private long deletingAlbumId = 0;


    public TableAlbumsBean() {
    }
    
    @PostConstruct
    public void initDB() {
        albumsDataModel = new ListDataModel<>(new ArrayList<>(albumDatabaseService.listAllAlbums()));
    }

    public void debugPrint() {
        System.out.println(this.importanceThreshold);
    }

    public void downloadAlbum() {
        try {
            List<AlbumPhoto> photos = selectedAlbum.getPhotos();
            if (photos.isEmpty()) {
                System.out.println("No photos found for album: " + selectedAlbum.getName());
                return;
            }

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + selectedAlbum.getName() + ".zip\"");
            response.setContentType("application/zip");

            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());

            for (AlbumPhoto photo : photos) {
                String filePath = this.fileLoader.getPhotosDirectoryPath() + photo.getPhoto().getRelativePathFromRoot();
                System.out.println("FILEPATH FOR " + photo.getPhoto().getFileName() + " is: " + filePath);
                File file = new File(filePath);
                if (file.exists()) {
                    addToZip(file, zos);
                }
            }

            zos.close();
            facesContext.responseComplete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addToZip(File file, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }

    // sort album photos into appropriate AlbumPhotoCollections
    // init value from rating in AlbumPhoto.Photo.rating
    public void sortAlbumPhotos() {
        List<AlbumPhoto> allAlbumPhotos = selectedAlbum.getPhotos();
        List<AlbumPhotoCollection> allCollections = new ArrayList<>();
        AlbumPhotoCollection temp = null;
        for (AlbumPhoto photo : allAlbumPhotos) {
            if (this.importanceIgnore != null && photo.getImportance() == this.importanceIgnore) {
                continue;
            }

            // low imp photo scenario
            if (photo.getImportance() < importanceThreshold) {
                //if current collection contains 3 photos - create a new one
                // if collection isnt initiated or is of high importance, (add to list) and create new one
                if (temp == null || !temp.isLowImportance() || temp.getSize() == 3) {
                    // add current high importance collection / full low importance collection to all collections before creating a new one
                    if (temp != null) {
                        allCollections.add(temp);
                    }
                    temp = new AlbumPhotoCollection(true);
                    temp.addPhoto(photo);
                } else {
                    temp.addPhoto(photo);
                }
            } else {
                // if collection isnt initiated or is of low importance, (add to list) and create new one
                if (temp == null || temp.isLowImportance()) {
                    if (temp != null) {
                        allCollections.add(temp);
                    }
                    temp = new AlbumPhotoCollection(false);
                    temp.addPhoto(photo);
                } else {
                    temp.addPhoto(photo);
                }
            }
        }
        if (!allCollections.contains(temp)) {
            allCollections.add(temp);
        }

        this.importanceCollections = new ListDataModel<>(allCollections);
    }

    public void updateAlbumPhoto(int albumPhotoId) {
        AlbumPhoto albumPhoto = importanceCollections
                .getRowData()
                .getPhotos()
                .stream()
                .filter(a -> a.getId() == albumPhotoId)
                .findFirst()
                .get();
        albumDatabaseService.update(albumPhoto.getAlbum());
        sortAlbumPhotos();
    }

    public void deletePhoto(AlbumPhoto photo) {
        selectedAlbum.deleteAlbumPhoto(photo);
        albumDatabaseService.update(selectedAlbum);
    }

    /**
     * Load bookmarkable detail page data.
     */
    public void loadSelectedAlbum() {
        Album newAlbum = albumDatabaseService.findAlbumById(selectedAlbumId);
        if (!Objects.equals(selectedAlbumId, lastSelectedAlbumId)) {
            selectedAlbum = newAlbum;
            lastSelectedAlbumId = selectedAlbumId;
            this.iterator = new CyclicIterator(selectedAlbum.getPhotos());
            sortAlbumPhotos();
            System.out.println("reset the whole album thing");
        } else {
            System.out.println("did not reset the whole album thing");
        }
    }

    public void refreshAlbums() {
        this.albumsDataModel = new ListDataModel<>(new ArrayList<>(albumDatabaseService.listAllAlbums()));
    }

    public DataModel<Album> getAlbumsDataModel() {
        return albumsDataModel;
    }

    public void selectAlbum() {
        selectedAlbum = albumsDataModel.getRowData();
    }

    public void deleteAlbum() {
        deletingAlbumId = 0;
        selectedAlbum = albumsDataModel.getRowData();
        albumDatabaseService.deleteAlbum(selectedAlbum.getId());
        this.refreshAlbums();
    }

    public void clearSelectedAlbum() {
        selectedAlbum = null;
    }

    public Album getSelectedAlbum() {
        return selectedAlbum;
    }

    public void setSelectedAlbum(Album selectedAlbum) {
        this.selectedAlbum = selectedAlbum;
    }

    public String getSelectedAlbumParameter() {
        return selectedAlbumParameter;
    }

    public void setSelectedAlbumParameter(String selectedAlbumParameter) {
        this.selectedAlbumParameter = selectedAlbumParameter;
    }

    public String createAlbum() {
        this.selectedAlbum = null;
        return "albums-new.xhtml?faces-redirect=true";
    }

    public CyclicIterator getIterator() {
        return iterator;
    }

    public void setIterator(CyclicIterator iterator) {
        this.iterator = iterator;
    }

    public DataModel<AlbumPhotoCollection> getImportanceCollections() {
        return importanceCollections;
    }

    public void setImportanceCollections(DataModel<AlbumPhotoCollection> importanceCollections) {
        this.importanceCollections = importanceCollections;
    }

    public int getImportanceThreshold() {
        return importanceThreshold;
    }

    public void setImportanceThreshold(int importanceThreshold) {
        this.importanceThreshold = importanceThreshold;
    }

    public long getDeletingAlbumId() {
        return deletingAlbumId;
    }

    public void setDeletingAlbumId(long deletingAlbumId) {
        this.deletingAlbumId = deletingAlbumId;
    }

    public Integer getImportanceIgnore() {
        return importanceIgnore;
    }

    public void setImportanceIgnore(Integer importanceIgnore) {
        this.importanceIgnore = importanceIgnore;
    }

    public Long getSelectedAlbumId() {
        return selectedAlbumId;
    }

    public void setSelectedAlbumId(Long selectedAlbumId) {
        this.selectedAlbumId = selectedAlbumId;
    }

}
