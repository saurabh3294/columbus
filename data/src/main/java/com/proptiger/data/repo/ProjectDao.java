/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.filter.FieldsQueryBuilder;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.ProjectFilter;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.model.filter.SortQueryBuilder;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Repository;

/**
 *
 * @author mukand
 */
@Repository
public class ProjectDao {
     private HttpSolrServer httpSolrServer = new HttpSolrServer("http://www.proptiger.com:8983/solr/");

    public List<Project> getProjects(ProjectFilter projectFilter) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.add("facet", "true");
        solrQuery.add("facet.field", "CITY");
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
        ProjectFilter projectFilter = new ProjectFilter();
        projectFilter.setFilters("{\"and\":[{\"range\":{\"bedrooms\":{\"from\":\"2\",\"to\":\"3\"}}},{\"equal\":{\"bathrooms\":[2]}}]}");
        projectFilter.setFields("price_per_unit_area,bedrooms,unit_name,unit_type");
        projectFilter.setSort("[{\"price_per_unit_area\" : \"asc\"}, {\"bedrooms\" : \"desc\"}]");
        new ProjectDao().getProjects(projectFilter);
    }
}
