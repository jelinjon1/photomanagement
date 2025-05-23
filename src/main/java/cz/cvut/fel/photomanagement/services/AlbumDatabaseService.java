package cz.cvut.fel.photomanagement.services;

import cz.cvut.fel.photomanagement.entities.Album;
import cz.cvut.fel.photomanagement.exception.PersistenceException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implements methods for accessing the database table Album.
 *
 * @author jelinjon
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
        try {
            return entityManager
                    .createNamedQuery(Album.QUERY_BY_ALBUM_NAME, Album.class)
                    .setParameter("givenName", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Album findAlbumById(Long id) {
        return entityManager.find(Album.class, id);
    }

    public List<Album> findAllAlbums() {
        return entityManager
                .createNamedQuery(Album.QUERY_ALL, Album.class)
                .getResultList();
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
        if (album != null) {
            entityManager.remove(album);
        }
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
