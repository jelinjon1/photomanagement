/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package photomanagement.initializer;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import photomanagement.faces.model.Album;
import photomanagement.faces.model.Photo;

/**
 *
 * @author Jonáš
 */
@Singleton
@Startup
public class SystemInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void initData() {
        Album album1 = new Album();
        album1.setName("Summer vacation 2018");
        album1.setDescription("Trip to Canada");
        album1.setCoverImage("palm-tree.jpg");

        Album album2 = new Album();
        album2.setName("Winter vacation 2019");
        album2.setDescription("Trip to Europe");
        album2.setCoverImage("bird-branch.jpg");

        Photo photo1 = new Photo();
        photo1.setFileName("bird-branch.jpg");
        photo1.setDescription("bird sitting on a branch");

        Photo photo2 = new Photo();
        photo2.setFileName("christmas-tree-detail.jpg");
        photo2.setDescription("detailed picture of a decorated christmas tree");

        Photo photo3 = new Photo();
        photo3.setFileName("palm-tree.jpg");
        photo3.setDescription("palm tree on a beach");

        Photo photo4 = new Photo();
        photo4.setFileName("tree-detail.jpg");
        photo4.setDescription("detailed shot of a tree in a forest");

        Photo photo5 = new Photo();
        photo5.setFileName("wood-logs.jpg");
        photo5.setDescription("multiple logs of wood");

        entityManager.persist(album1);
        entityManager.persist(album2);
        entityManager.persist(photo1);
        entityManager.persist(photo2);
        entityManager.persist(photo3);
        entityManager.persist(photo4);
        entityManager.persist(photo5);

    }
}
