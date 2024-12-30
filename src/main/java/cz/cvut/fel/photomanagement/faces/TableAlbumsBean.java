/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.faces.model.Album;
import cz.cvut.fel.photomanagement.faces.model.AlbumPhoto;
import cz.cvut.fel.photomanagement.faces.model.AlbumPhotoCollection;
import cz.cvut.fel.photomanagement.faces.model.Photo;
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

    public TableAlbumsBean() {
    }
    
    @PostConstruct
    public void initDB() {
        albumsList = new ListDataModel<>(new ArrayList<>(albumDatabaseService.listAllAlbums()));
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
        System.out.println(albumPhoto);
    }

    public void deletePhoto(Photo photo) {
        selectedAlbum.deletePhoto(photo);
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
        selectedAlbum = albumsList.getRowData();
        albumDatabaseService.deleteAlbum(selectedAlbum.getId());
    }

    public String goToDetailRedirect() {
        selectedAlbum = albumsList.getRowData();
        this.iterator = new CyclicIterator(selectedAlbum.getPhotos());

        AlbumPhotoCollection lowImp = new AlbumPhotoCollection(0, new ArrayList<>(selectedAlbum.getPhotos().subList(0, 6)));
        AlbumPhotoCollection highImp = new AlbumPhotoCollection(6, new ArrayList<>(selectedAlbum.getPhotos().subList(6, 11)));
        AlbumPhotoCollection lowImp2 = new AlbumPhotoCollection(0, new ArrayList<>(selectedAlbum.getPhotos().subList(11, 17)));

        this.importanceCollections = new ListDataModel<>(
                List.of(lowImp, highImp, lowImp2)
        );
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

}
