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
    }
}
