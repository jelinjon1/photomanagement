/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import cz.cvut.fel.photomanagement.faces.model.Album;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;

/**
 *
 * @author Jonáš
 */
@Named(value = "tableAlbumsBean")
@SessionScoped
public class TableAlbumsBean implements Serializable {
    @EJB
    private AlbumDatabaseService albumDatabaseService;
    private DataModel<Album> albumsList;
    private Album selectedAlbum = null;
    private String selectedAlbumParameter = null;

    public TableAlbumsBean() {
    }
    
    @PostConstruct
    public void initDB() {
        albumsList = new ListDataModel<>(new ArrayList<>(albumDatabaseService.listAllAlbums()));
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
}
