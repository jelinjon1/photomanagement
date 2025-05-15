package cz.cvut.fel.photomanagement.faces.util;

import cz.cvut.fel.photomanagement.entities.Album;
import java.io.Serializable;

/**
 * Represents a dropdown menu option present on photos.xhtml.
 *
 * @author jelinjon
 */
public class AlbumMenuOption implements Serializable {

    private final Album album;

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
