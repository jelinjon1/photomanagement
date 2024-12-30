/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 *
 * @author Jonáš
 */
@Entity
public class AlbumPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Photo photo;
    private Album album;
    private int placement; // order violates sql syntax
    private int importance;

    public AlbumPhoto() {
    }

    public AlbumPhoto(Photo photo, Album album) {
        this.photo = photo;
        this.album = album;
    }

    public AlbumPhoto(Photo photo, Album album, int placement, int importance) {
        this.photo = photo;
        this.album = album;
        this.placement = placement;
        this.importance = importance;
    }

    @Override
    public String toString() {
        return "AlbumPhoto{" + "id=" + id + ", album=" + album.getName() + ", placement=" + placement + ", importance=" + importance + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public int getPlacement() {
        return placement;
    }

    public void setPlacement(int placement) {
        this.placement = placement;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }


}
