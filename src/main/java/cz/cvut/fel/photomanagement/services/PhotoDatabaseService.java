/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.services;

import cz.cvut.fel.photomanagement.exception.PersistenceException;
import cz.cvut.fel.photomanagement.faces.model.Photo;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jonáš
 */
@Stateless
public class PhotoDatabaseService {

    @PersistenceContext
    protected EntityManager entityManager;

    public List<Photo> listAllPhotos() {
        return Collections.unmodifiableList(this.findAllPhotos());
    }

    public Photo findPhotoById(Long id) {
        Objects.requireNonNull(id);
        return entityManager.find(Photo.class, id);
    }

    public List<Photo> findAllPhotos() {
        try {
            return entityManager.createQuery("SELECT a FROM Photo a", Photo.class).getResultList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void savePhoto(Photo photo) {
        Objects.requireNonNull(photo);
        try {
            entityManager.persist(photo);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void deletePhoto(Long id) {
        Photo Photo = entityManager.find(Photo.class, id);
        if (Photo != null) {
            entityManager.remove(Photo);
        }
    }

    public void deletePhoto(Photo photo) {
        if (photo != null) {
            entityManager.remove(photo);
        }
    }

    public List<Photo> findAllExistingPhotos(String relativePath) {
        return entityManager
                .createNamedQuery(Photo.QUERY_BY_LOCAL_DIR, Photo.class)
                .setParameter("localPath", relativePath)
                .getResultList();
    }

    public Photo update(Photo photo) {
        Objects.requireNonNull(photo);
        try {
            return entityManager.merge(photo);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void remove(Photo entity) {
        Objects.requireNonNull(entity);
        try {
            final Photo toRemove = entityManager.merge(entity);
            if (toRemove != null) {
                entityManager.remove(toRemove);
            }
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

//    public Photo getPhotoByHash(String photoHash) {
//        Objects.requireNonNull(photoHash);
//        try {
//            String query = "SELECT p FROM Photo p WHERE p.hash==" + photoHash;
//            return entityManager.createQuery(query, Photo.class).getSingleResult();
//        } catch (RuntimeException e) {
//            throw new PersistenceException(e);
//        }
//    }
    public Photo getPhotoByHash(String hash) {
        Objects.requireNonNull(hash);
        try {
            return entityManager.createQuery(
                    "SELECT p FROM Photo p WHERE p.hash = :hash", Photo.class)
                    .setParameter("hash", hash)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new PersistenceException(e);
        }
    }
}
