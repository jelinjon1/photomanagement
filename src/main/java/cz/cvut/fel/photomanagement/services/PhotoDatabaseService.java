/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.services;

import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import cz.cvut.fel.photomanagement.faces.model.Photo;

/**
 *
 * @author Jonáš
 */
@Singleton
public class PhotoDatabaseService {

    @PersistenceContext
    protected EntityManager entityManager;

    public List<Photo> listAllPhotos() {
        return Collections.unmodifiableList(this.findAllPhotos());
    }

    public Photo findPhotoById(Long id) {
        return entityManager.find(Photo.class, id);
    }

    public List<Photo> findAllPhotos() {
        return entityManager.createQuery("SELECT a FROM Photo a", Photo.class).getResultList();
    }

    @Transactional
    public void savePhoto(Photo Photo) {
        entityManager.persist(Photo);
    }

    @Transactional
    public void deletePhoto(Long id) {
        Photo Photo = entityManager.find(Photo.class, id);
        if (Photo != null) {
            entityManager.remove(Photo);
        }
    }
}
