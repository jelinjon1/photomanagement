/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.faces.model.Album;
import cz.cvut.fel.photomanagement.faces.model.AlbumPhoto;
import cz.cvut.fel.photomanagement.faces.model.AlbumPhotoCollection;
import cz.cvut.fel.photomanagement.faces.util.CyclicIterator;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;
import cz.cvut.fel.photomanagement.services.PhotoDatabaseService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jonáš
 */
@Named(value = "tableAlbumsBean")
@SessionScoped
public class TableAlbumsBean implements Serializable {
    @EJB
    private AlbumDatabaseService albumDatabaseService;
    @EJB
    private PhotoDatabaseService photoDatabaseService;
    private DataModel<Album> albumsList;
    private Album selectedAlbum = null;
    private String selectedAlbumParameter = null;
    private CyclicIterator iterator = null;
    private int activeViewIndex;
    private DataModel<AlbumPhotoCollection> importanceCollections;
    private int importanceThreshold = 5;
    private long deletingAlbumId = 0;

    public TableAlbumsBean() {
    }
    
    @PostConstruct
    public void initDB() {
        albumsList = new ListDataModel<>(new ArrayList<>(albumDatabaseService.listAllAlbums()));
    }

    // sort album photos into appropriate AlbumPhotoCollections
    // init value from rating in AlbumPhoto.Photo.rating
    public void sortAlbumPhotos() {
        List<AlbumPhoto> allAlbumPhotos = selectedAlbum.getPhotos();
        List<AlbumPhotoCollection> allCollections = new ArrayList<>();
        AlbumPhotoCollection temp = null;
        for (AlbumPhoto photo : allAlbumPhotos) {

            // low imp photo scenario
            if (photo.getImportance() < importanceThreshold) {
                // if collection isnt initiated or is of high importance, (add to list) and create new one
                if (temp == null || !temp.isLowImportance()) {
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
        selectedAlbum = albumDatabaseService.findByName(selectedAlbumParameter);
    }

    public DataModel<Album> getAlbumsList() {
        albumsList = new ListDataModel<>(new ArrayList<>(albumDatabaseService.listAllAlbums()));
        return albumsList;
    }

    public void selectAlbum() {
        selectedAlbum = albumsList.getRowData();
    }

    public void deleteAlbum() {
        deletingAlbumId = 0;
        selectedAlbum = albumsList.getRowData();
        albumDatabaseService.deleteAlbum(selectedAlbum.getId());
    }

    public String goToDetailRedirect() {
        selectedAlbum = albumsList.getRowData();
        System.out.println("SELECTED ALBUM PHOTOS: " + selectedAlbum.getPhotos());
        this.iterator = new CyclicIterator(selectedAlbum.getPhotos());

        sortAlbumPhotos();
        return "albums-detail.xhtml?faces-redirect=true";
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

    public int getActiveViewIndex() {
        return activeViewIndex;
    }

    public void setActiveViewIndex(int activeViewIndex) {
        this.activeViewIndex = activeViewIndex;
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
}
