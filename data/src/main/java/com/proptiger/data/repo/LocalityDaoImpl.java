/**
 * 
 */
package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

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
	@Autowired
	private EntityManagerFactory emf;
	public List<Locality> getLocalities(Selector selector){
		SolrQuery solrQuery = createSolrQuery(selector);
		
		QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
		List<SolrResult> response = queryResponse.getBeans(SolrResult.class);
		
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
    	
    	sortBy.setField("localityPriority");
    	sortBy.setSortOrder(sortOrder);
    	sorting.add(sortBy);
    	
    	sortBy = new SortBy();
    	sortBy.setField("localityLabel");
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
	
	public List<Locality> findByLocalityIds(List<Integer> localityIds, Selector propertySelector){
		
		Selector selector = new Selector();
		
		Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String,Map<String,Object>>>>();
    	List<Map<String, Map<String, Object>>> list = new ArrayList<>();
    	Map<String, Map<String, Object>> searchType = new HashMap<>();
    	Map<String, Object> filterCriteria = new HashMap<>();
    	    	    	    	    	
    	filterCriteria.put("localityId", localityIds);
    	searchType.put("equal", filterCriteria);
    	list.add(searchType);
    	filter.put("and", list);
    	
    	selector.setFilters(filter);
    	selector.setFields(propertySelector.getFields());
    	selector.setPaging(propertySelector.getPaging());
    	if( selector.getPaging() == null )
    		selector.setPaging( new Paging(0, localityIds.size()));
    	
    	return getLocalities(selector);
	}
    

	private SolrQuery createSolrQuery(Selector selector){
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFilterQueries("DOCUMENT_TYPE:LOCALITY");
		
		if (selector.getSort() == null) {
            selector.setSort(new LinkedHashSet<SortBy>());
        }

        selector.getSort().addAll(getDefaultSort());
        
        SolrQueryBuilder<SolrResult> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, SolrResult.class);
		solrQueryBuilder.buildQuery(selector, null);
		
		return solrQuery;
	}
		
	private Set<SortBy> getDefaultSort() {
        Set<SortBy> sortBySet = new LinkedHashSet<SortBy>();
        SortBy sortBy = new SortBy();
        sortBy.setField("localityPriority");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);

        sortBy = new SortBy();
        sortBy.setField("localityLabel");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);
                
        return sortBySet;
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
		Query query = em.createNativeQuery("select *, count(enquiry1_.ID) as ENQUIRY_COUNT from proptiger.LOCALITY locality0_ "
				+ " left outer join  proptiger.ENQUIRY enquiry1_ ON (locality0_.LOCALITY_ID = enquiry1_.LOCALITY_ID AND "
				+ " UNIX_TIMESTAMP(enquiry1_.CREATED_DATE) >"
				+ " "+enquiryCreationTimeStamp +")"
				+ " where (locality0_.CITY_ID = "
				+ " "+cityId
				+ " or locality0_.SUBURB_ID = "
				+ " "+suburbId+ ")"
				+ " group by locality0_.LOCALITY_ID order by locality0_.PRIORITY ASC , ENQUIRY_COUNT DESC", Locality.class);
		List<Locality> result = query.getResultList();
		return result;
    }
}
