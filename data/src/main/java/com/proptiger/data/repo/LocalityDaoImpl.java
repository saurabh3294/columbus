/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;

/**
 * @author mandeep
 *
 */
@Repository
public class LocalityDaoImpl {

	@Autowired
	private SolrDao solrDao;
	
	public List<Locality> getLocalities(Selector selector){
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFilterQueries("DOCUMENT_TYPE:LOCALITY");
		
		SolrQueryBuilder<Locality> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, Locality.class);
		solrQueryBuilder.buildQuery(selector, null);
		
		QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
		List<SolrResult> response = queryResponse.getBeans(SolrResult.class);
		
		System.out.println(solrQuery.toString());
		List<Locality> data = new ArrayList<>();
		for(int i=0; i<response.size(); i++)
		{
			data.add(response.get(i).getProject().getLocality());
		}
		
		return data;
	}
	public List<Locality> findByLocationOrderByPriority(Object locationId, String locationType, Paging paging, SortOrder sortOrder){
		if(sortOrder == null)
			sortOrder = SortOrder.DESC;
		if(paging == null)
			paging = new Paging();
		
		Selector selector = new Selector();
		
    	Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String,Map<String,Object>>>>();
    	List<Map<String, Map<String, Object>>> list = new ArrayList<>();
    	Map<String, Map<String, Object>> searchType = new HashMap<>();
    	Map<String, Object> filterCriteria = new HashMap<>();
    	LinkedHashSet<SortBy> sorting = new LinkedHashSet<>();
    	SortBy sortBy = new SortBy();
    	
    	sortBy.setField("priority");
    	sortBy.setSortOrder(sortOrder);
    	sorting.add(sortBy);
    	
    	sortBy = new SortBy();
    	sortBy.setField("label");
    	sortBy.setSortOrder(SortOrder.ASC);
    	sorting.add(sortBy);
    	
    	String param="";
    	switch(locationType)
    	{
    		case "city":
    			param = "cityId";
    			break;
    		case "suburb":
    			param = "suburbId";
    			break;
    		default:
    			param = "localityId";
    	}
    	
    	filterCriteria.put(param, locationId);
    	searchType.put("equal", filterCriteria);
    	list.add(searchType);
    	filter.put("and", list);
    	
    	selector.setFilters(filter);
    	selector.setPaging(paging);
    	selector.setSort(sorting);
    	
    	return getLocalities(selector);
	}
    
//	
//    @Autowired
//    private EntityManagerFactory emf;
//    
//    public List<Locality> getLocalities(Selector selector) {
//        EntityManager em = emf.createEntityManager();
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        List<Locality> result = new ArrayList<Locality>();
//
//        MySqlQueryBuilder<Locality> mySqlQueryBuilder = new MySqlQueryBuilder<Locality>(builder, Locality.class);
//        
//        mySqlQueryBuilder.buildQuery(selector, null);
//        //executing query to get result
//        result = em.createQuery(mySqlQueryBuilder.getQuery()).getResultList();
//
//        return result;
//    }
}
