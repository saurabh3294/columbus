/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.filter.MySqlQueryBuilder;
import com.proptiger.data.pojo.Selector;

/**
 * @author mandeep
 *
 */
@Repository
public class LocalityDaoImpl {
    @Autowired
    private EntityManagerFactory emf;
    
    public List<Locality> getLocalities(Selector selector) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        List<Locality> result = new ArrayList<Locality>();

        MySqlQueryBuilder<Locality> mySqlQueryBuilder = new MySqlQueryBuilder<Locality>(builder, Locality.class);
        
        mySqlQueryBuilder.buildQuery(selector, null);
        //executing query to get result
        result = em.createQuery(mySqlQueryBuilder.getQuery()).getResultList();

        return result;
    }
}
