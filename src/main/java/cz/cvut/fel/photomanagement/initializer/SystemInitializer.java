/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.initializer;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

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
//        Album album1 = new Album();
//        album1.setName("Summer vacation 2018");
//        album1.setDescription("Trip to Canada");
//
//        Album album2 = new Album();
//        album2.setName("Winter vacation 2019");
//        album2.setDescription("Trip to Europe");
//
//        entityManager.persist(album1);
//        entityManager.persist(album2);
    }
}
