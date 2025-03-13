/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces.util;

import cz.cvut.fel.photomanagement.faces.model.Album;

/**
 *
 * @author Jonáš
 */
public class AlbumMenuOption {

    private Album album;

    public String getName() {
        if (album != null) {
            return album.getName();
        } else {
            return "Create new album";
        }
    }

    public Long getId() {
        if (album != null) {
            return album.getId();
        } else {
            return 0L;
        }
    }

    public AlbumMenuOption(Album album) {
        this.album = album;
    }
}
