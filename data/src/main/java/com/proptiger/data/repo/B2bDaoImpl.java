package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.InventoryPriceTrend;
import com.proptiger.data.model.SolrResult;
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

	public PaginatedResponse<List<InventoryPriceTrend>> getFilteredDocuments(Selector selector){
		SolrQuery solrQuery = createSolrQuery(selector);
		
		String test = getDominantSupply(selector);
		
//		solrQuery.add("group", "true");
//        solrQuery.add("group.ngroups", "true");
//        solrQuery.add("group.limit", "-1");
//        solrQuery.add("group.field", "BUILDER_ID");
//			System.out.println(solrQuery.toString());
		QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
		List<InventoryPriceTrend> response = queryResponse.getBeans(InventoryPriceTrend.class);
		        
		return getPaginatedResponse(response, queryResponse);
	}
	
	public String getDominantSupply(Selector selector){
		SolrQuery solrQuery = createSolrQuery(selector);
		
		solrQuery.add("group", "true");
        solrQuery.add("group.ngroups", "true");
        solrQuery.add("group.truncate", "true");
        solrQuery.add("group.limit", "-1");
        solrQuery.add("group.field", "BUILDER_ID");
        
        solrQuery.add("stats", "true");
        //solrQuery.add("group.ngroups", "true");
        //solrQuery.add("group.limit", "-1");
        solrQuery.add("stats.facet", "BUILDER_ID");
        solrQuery.add("stats.field", "AVERAGE_PRICE_PER_UNIT_AREA");
        
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        
        for (GroupCommand groupCommand : queryResponse.getGroupResponse().getValues()) {
        	for (Group group : groupCommand.getValues()) {
        		String groupValue = group.getGroupValue();
        		List<SolrDocument> solrResults = group.getResult();
        		System.out.println("GROUP VALUE = " + groupValue);
        		System.out.println("GROUP RESULT = " + solrResults);
        	}
        }
        
        System.out.println(solrQuery.toString());
        
		return "";
	}
	
	private SolrQuery createSolrQuery(Selector selector){
		SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.B2B);
		
		
		if (selector.getSort() == null) {
            selector.setSort(new LinkedHashSet<SortBy>());
        }

        selector.getSort().addAll(getDefaultSort());
        
        SolrQueryBuilder<InventoryPriceTrend> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, InventoryPriceTrend.class);
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
	
	public PaginatedResponse<List<InventoryPriceTrend>> getPaginatedResponse(List<InventoryPriceTrend> response, QueryResponse queryResponse){
    	List<InventoryPriceTrend> data = new ArrayList<>();
		for(int i=0; i<response.size(); i++)
		{
			data.add(response.get(i));
		}
		
		PaginatedResponse<List<InventoryPriceTrend>> solrRes = new PaginatedResponse<List<InventoryPriceTrend>>();
        solrRes.setTotalCount(queryResponse.getResults().getNumFound());
        solrRes.setResults(data);
        
        return solrRes;
    }
}