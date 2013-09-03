/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.filter.FieldsQueryBuilder;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.PropertyRequestParams;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.model.filter.SortBy;
import com.proptiger.data.model.filter.SortOrder;
import com.proptiger.data.model.filter.SortQueryBuilder;

/**
 *
 * @author mukand
 */
@Repository
public class ProjectDao {
     private HttpSolrServer httpSolrServer = new HttpSolrServer("http://www.proptiger.com:8983/solr/");

    public List<Project> getProjects(PropertyRequestParams projectFilter) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.addFilterQuery("DOCUMENT_TYPE:PROJECT");
        solrQuery.setRows(10);

        SolrQueryBuilder queryBuilder = new SolrQueryBuilder(solrQuery);
        FilterQueryBuilder.applyFilter(queryBuilder, projectFilter.getFilters(), Project.class);
        SortQueryBuilder.applySort(queryBuilder, projectFilter.getSort(), Project.class);
        FieldsQueryBuilder.applyFields(queryBuilder, projectFilter.getFields(), Project.class);

        try {
            QueryResponse queryResponse = httpSolrServer.query(solrQuery);
            return queryResponse.getBeans(Project.class);
//            List<SolrResult> solrResults = queryResponse.getBeans(SolrResult.class);
//            List<Property> properties = new ArrayList<Property>();
//            for (SolrResult solrResult : solrResults) {
//                properties.add(solrResult.getProperty());
//            }
//            return properties;
        } catch (SolrServerException e) {
            throw new RuntimeException("Could not run query", e);
        }
    }
    
    public static void main(String[] args) {
    	PropertyRequestParams projectFilter = new PropertyRequestParams();
        projectFilter.setFilters("{\"and\":[{\"range\":{\"bedrooms\":{\"from\":\"2\",\"to\":\"3\"}}},{\"equal\":{\"bathrooms\":[2]}}]}");
       Set<String> fields = new HashSet<String>();
       fields.add("price_per_unit_area");
       fields.add("bedrooms");
       fields.add("unit_name");
       fields.add("unit_type");
        projectFilter.setFields(fields);
        
        Set<SortBy> sort = new HashSet<SortBy>();
        SortBy sortBy1 = new SortBy();
        sortBy1.setField("price_per_unit_area");
        sortBy1.setSortOrder(SortOrder.ASC);
        
        SortBy sortBy2 = new SortBy();
        sortBy2.setField("bedrooms");
        sortBy2.setSortOrder(SortOrder.DESC);
        sort.add(sortBy1);
        sort.add(sortBy2);
        projectFilter.setSort(sort);
        ObjectMapper mapper = new ObjectMapper();
        
        try {
			System.out.println(mapper.writeValueAsString(projectFilter));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        new ProjectDao().getProjects(projectFilter);
    }
}
