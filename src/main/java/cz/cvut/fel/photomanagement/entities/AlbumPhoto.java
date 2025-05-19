package cz.cvut.fel.photomanagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;

/**
 * Represents a relationship between an {@link cz.cvut.fel.photomanagement.entities.Album} and a
 * {@link cz.cvut.fel.photomanagement.entities.Photo}, where one Photo can be saved in many albums but has different
 * importance value inside each.
 *
 * @author jelinjon
 */
@Entity
public class AlbumPhoto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(nullable = false)
    private Photo photo;
    @JoinColumn(nullable = false)
    private Album album;
    @Min(0)
    @Max(10)
    @Column(nullable = false)
    private int importance;

    public AlbumPhoto() {
    }

    public AlbumPhoto(Photo photo, Album album) {
        this.photo = photo;
        this.album = album;
        this.importance = (photo.getRating() == null) ? 0 : photo.getRating() * 2;
    }

    public AlbumPhoto(Photo photo, Album album, int importance) {
        this.photo = photo;
        this.album = album;
        this.importance = importance;
    }

    @Override
    public String toString() {
        return "AlbumPhoto{" + "id=" + id + ", photo=" + photo.getFileName() + ", album=" + album.getName() + ", importance=" + importance + '}';
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

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }


}
