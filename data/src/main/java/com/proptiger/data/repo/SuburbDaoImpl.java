/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Selector;

/**
 * @author mandeep
 *
 */
@Repository
public class SuburbDaoImpl {
	@Autowired
    private SolrDao solrDao;

	public List<Suburb> getSuburbs(Selector selector){
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFilterQueries("DOCUMENT_TYPE:SUBURB");
		
		SolrQueryBuilder<Suburb> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, Suburb.class);
		solrQueryBuilder.buildQuery(selector, null);
		
		QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
		List<SolrResult> response = queryResponse.getBeans(SolrResult.class);
		
		System.out.println(solrQuery.toString());
		List<Suburb> data = new ArrayList<>();
		for(int i=0; i<response.size(); i++)
		{
			data.add(response.get(i).getProject().getLocality().getSuburb());
		}
		
		return data;
	}
    /*@Autowired
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
    }*/
}
