/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package photomanagement.services;

import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import photomanagement.exception.PersistenceException;
import photomanagement.faces.model.Album;

/**
 *
 * @author Jonáš
 */
@Singleton
public class AlbumDatabaseService {

    @PersistenceContext
    protected EntityManager entityManager;

    public List<Album> listAllAlbums() {
        return Collections.unmodifiableList(this.findAllAlbums());
    }

    public Album findByName(String name) {
        return entityManager.createQuery("SELECT a FROM Album a WHERE a.name == " + name, Album.class).getResultList().getFirst();
    }

    public Album findAlbumById(Long id) {
        return entityManager.find(Album.class, id);
    }

    public List<Album> findAllAlbums() {
        return entityManager.createQuery("SELECT a FROM Album a", Album.class).getResultList();
    }

    @Transactional
    public void saveAlbum(Album album) {
        entityManager.persist(album);
    }

    @Transactional
    public Album update(Album album) {
        Objects.requireNonNull(album);
        try {
            return entityManager.merge(album);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);

        }
    }

    @Transactional
    public void deleteAlbum(Long id) {
        Album album = entityManager.find(Album.class, id);
        if (album != null) {
            entityManager.remove(album);
        }
    }
}
