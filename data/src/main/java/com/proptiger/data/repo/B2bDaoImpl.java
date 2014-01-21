package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.B2b;
import com.proptiger.data.model.enums.DocumentType;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.service.pojo.PaginatedResponse;

@Repository
public class B2bDaoImpl implements B2bDao {
	@Autowired
	private SolrDao solrDao;

	public PaginatedResponse<List<B2b>> getFilteredDocuments(Selector selector){
		SolrQuery solrQuery = createSolrQuery(selector);
		System.out.println(solrQuery.toString());
		QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
		List<B2b> response = queryResponse.getBeans(B2b.class);
		        
		return getPaginatedResponse(response, queryResponse);
	}
	
	private SolrQuery createSolrQuery(Selector selector){
		SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.B2B);
		if (selector.getSort() == null) {
            selector.setSort(new LinkedHashSet<SortBy>());
        }

        selector.getSort().addAll(getDefaultSort());
        
        SolrQueryBuilder<B2b> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, B2b.class);
        System.out.println("HERE!" + solrQueryBuilder);
		solrQueryBuilder.buildQuery(selector, null);
		
		return solrQuery;
	}
	
	private Set<SortBy> getDefaultSort() {
        Set<SortBy> sortBySet = new LinkedHashSet<SortBy>();
        SortBy sortBy = new SortBy();
        sortBy.setField("projectId");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);
                
        return sortBySet;
    }
	
	public PaginatedResponse<List<B2b>> getPaginatedResponse(List<B2b> response, QueryResponse queryResponse){
    	List<B2b> data = new ArrayList<>();
		for(int i=0; i<response.size(); i++)
		{
			data.add(response.get(i));
		}
		
		PaginatedResponse<List<B2b>> solrRes = new PaginatedResponse<List<B2b>>();
        solrRes.setTotalCount(queryResponse.getResults().getNumFound());
        solrRes.setResults(data);
        
        return solrRes;
    }
}