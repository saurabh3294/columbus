package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.City;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.filter.MySqlQueryBuilder;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Selector;

/**
 * @author Rajeev Pandey
 *
 */
@Repository
public class CityDao {
	@Autowired
    private SolrDao solrDao;
	
	public List<City> getCities(Selector selector){
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFilterQueries("DOCUMENT_TYPE:CITY");
		
		SolrQueryBuilder<City> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, City.class);
		solrQueryBuilder.buildQuery(selector, null);
		
		QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
		List<SolrResult> response = queryResponse.getBeans(SolrResult.class);
		
		System.out.println(solrQuery.toString());
		List<City> data = new ArrayList<>();
		for(int i=0; i<response.size(); i++)
		{
			data.add(response.get(i).getProject().getLocality().getSuburb().getCity());
		}
		
		return data;
	}
	
	/*@Autowired
	private EntityManagerFactory emf;
	
	public List<City> getCities(Selector selector) {

		EntityManager em = emf.createEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		List<City> result = new ArrayList<City>();

		MySqlQueryBuilder<City> mySqlQueryBuilder = new MySqlQueryBuilder<City>(builder, City.class);
		
		mySqlQueryBuilder.buildQuery(selector, null);
		//executing query to get result
		result = em.createQuery(mySqlQueryBuilder.getQuery()).getResultList();

		return result;
	}*/
	
}
