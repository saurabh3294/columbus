package com.proptiger.data.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.model.proptiger.Media;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.pojo.FIQLSelector;

/**
 * 
 * @author azi
 * 
 */
public class MediaDaoImpl {
    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private MediaDao             mediaDao;

    public List<Media> getFilteredMedia(FIQLSelector selector) {
        EntityManager entityManager = emf.createEntityManager();
        AbstractQueryBuilder<Media> builder = new JPAQueryBuilder<>(entityManager, Media.class);
        builder.buildQuery(selector);
        List<Media> result = builder.retrieveResults();
        entityManager.close();
        return result;
    }
}