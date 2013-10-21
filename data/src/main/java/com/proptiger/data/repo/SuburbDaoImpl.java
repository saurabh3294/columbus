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

import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.filter.MySqlQueryBuilder;
import com.proptiger.data.pojo.Selector;

/**
 * @author mandeep
 *
 */
@Repository
public class SuburbDaoImpl {
    @Autowired
    private EntityManagerFactory emf;
    
    public List<Suburb> getSuburbs(Selector selector) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        List<Suburb> result = new ArrayList<Suburb>();

        MySqlQueryBuilder<Suburb> mySqlQueryBuilder = new MySqlQueryBuilder<Suburb>(builder, Suburb.class);
        
        mySqlQueryBuilder.buildQuery(selector, null);
        //executing query to get result
        result = em.createQuery(mySqlQueryBuilder.getQuery()).getResultList();

        return result;
    }
}
