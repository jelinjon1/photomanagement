/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.entities;

import cz.cvut.fel.photomanagement.comparator.AlbumPhotoByDate;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jonáš
 */
@Entity
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AlbumPhoto> albumPhotos;
    private String name;
    private String description;
    private LocalDate created;
    private LocalDate lastEdited;
    private String coverImage = null;

    public Album() {
        this.created = LocalDate.now();
        this.lastEdited = this.created;
    }

    public Album(String name) {
        this.name = name;
    }

    public Album(List<Photo> photos, String name, String description, String coverImage) {
        this.name = name;
        this.description = description;
        this.created = LocalDate.now();
        this.lastEdited = this.created;
        this.coverImage = coverImage;
    }

    @Override
    public String toString() {
        return "Album{" + "id=" + id + ", albumPhotos=" + albumPhotos + ", name=" + name + ", description=" + description + ", created=" + created + ", lastEdited=" + lastEdited + ", coverImage=" + coverImage + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.description);
        hash = 37 * hash + Objects.hashCode(this.created);
        hash = 37 * hash + Objects.hashCode(this.lastEdited);
        hash = 37 * hash + Objects.hashCode(this.coverImage);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Album other = (Album) obj;
        if (!Objects.equals(this.albumPhotos, other.albumPhotos)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.created, other.created)) {
            return false;
        }
        if (!Objects.equals(this.lastEdited, other.lastEdited)) {
            return false;
        }
        return Objects.equals(this.coverImage, other.coverImage);
    }

    public List<AlbumPhoto> getPhotos() {
        Comparator sortByDate = new AlbumPhotoByDate();
        albumPhotos.sort(sortByDate);
        return albumPhotos;
    }

    public void setPhotos(List<AlbumPhoto> photos) {
        this.albumPhotos = photos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(LocalDate lastEdited) {
        this.lastEdited = lastEdited;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Long getId() {
        return id;
    }

    public void addPhoto(Photo photo) {
        AlbumPhoto ap = new AlbumPhoto(photo, this);
        albumPhotos.add(ap); //todo add
    }

    public void deleteAlbumPhoto(AlbumPhoto photo) {
        System.out.println("BEFORE: " + albumPhotos);
        Objects.requireNonNull(photo);
        this.albumPhotos.remove(photo);
        System.out.println("AFTER: " + albumPhotos);
    }
}
