/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.services;

import cz.cvut.fel.photomanagement.entities.Album;
import cz.cvut.fel.photomanagement.exception.PersistenceException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jonáš
 */
@Stateless
public class AlbumDatabaseService implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    protected EntityManager entityManager;

    public List<Album> listAllAlbums() {
        return Collections.unmodifiableList(this.findAllAlbums());
    }

    public Album findByName(String name) {
        List<Album> albums = entityManager.createQuery("SELECT a FROM Album a WHERE a.name LIKE :albumName", Album.class).setParameter("albumName", name).getResultList();
        if (!albums.isEmpty()) {
            return albums.get(0);
        } else {
            return null;
        }
    }

    public Album findAlbumById(Long id) {
        return entityManager.find(Album.class, id);
    }

    public List<Album> findAllAlbums() {
        return entityManager.createQuery("SELECT a FROM Album a", Album.class).getResultList();
    }

    public void saveAlbum(Album album) {
        entityManager.persist(album);
    }

    public Album update(Album album) {
        Objects.requireNonNull(album);
        try {
            return entityManager.merge(album);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);

        }
    }

    public void deleteAlbum(Long id) {
        Album album = entityManager.find(Album.class, id);
//        for (AlbumPhoto albumPhoto : album.getPhotos()) {
//            entityManager.remove(albumPhoto);
//        }
        if (album != null) {
            entityManager.remove(album);
        }
    }
}
