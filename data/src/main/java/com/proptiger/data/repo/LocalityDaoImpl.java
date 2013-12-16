/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
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
    
    /**
     * This method is getting all the popular localities of city, criteria of popularity is first with priority in asc
     * and in case of tie total enquiry in desc 
     * @param cityId
     * @param suburbId 
     * @param enquiryCreationDate 
     * @return
     */
    public List<Locality> getPopularLocalities(
			Integer cityId, Integer suburbId, Long enquiryCreationTimeStamp){
    	EntityManager em = emf.createEntityManager();
		Query query = em.createNativeQuery("select *, count(enquiry1_.ID) as col_1_0_ from proptiger.LOCALITY locality0_ "
				+ " left outer join  proptiger.ENQUIRY enquiry1_ ON (locality0_.LOCALITY_ID = enquiry1_.LOCALITY_ID AND "
				+ " UNIX_TIMESTAMP(enquiry1_.CREATED_DATE) >"
				+ " "+enquiryCreationTimeStamp +")"
				+ " where (locality0_.CITY_ID = "
				+ " "+cityId
				+ " or locality0_.SUBURB_ID = "
				+ " "+suburbId+ ")"
				+ " group by locality0_.LOCALITY_ID order by locality0_.PRIORITY ASC , col_1_0_ DESC", Locality.class);
		List<Locality> result = query.getResultList();
		return result;
    }
}
