package cz.cvut.fel.photomanagement.services;

import cz.cvut.fel.photomanagement.entities.Photo;
import cz.cvut.fel.photomanagement.exception.PersistenceException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implements methods for accessing the database table Photo.
 *
 * @author jelinjon
 */
@Stateless
public class PhotoDatabaseService implements Serializable {

    private static final long serialVersionUID = 1L;

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
        return entityManager
                .createNamedQuery(Photo.QUERY_ALL, Photo.class)
                .getResultList();
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

    public List<Photo> findAllPhotosInADirectory(String relativePath) {
        return entityManager
                .createNamedQuery(Photo.QUERY_BY_LOCAL_DIR, Photo.class)
                .setParameter("localPath", relativePath)
                .getResultList();
    }

    public Photo merge(Photo photo) {
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

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
