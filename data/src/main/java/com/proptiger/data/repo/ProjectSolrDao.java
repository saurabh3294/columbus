/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.filter.FieldsQueryBuilder;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.model.filter.SortQueryBuilder;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.service.pojo.SolrServiceResponse;
import com.proptiger.data.util.PropertyReader;

/**
 * 
 * @author mukand
 */
@Repository
public class ProjectSolrDao {

    @Autowired
    private SolrDao solrDao;
    @Autowired
    private FilterQueryBuilder filterQueryBuilder;

    public SolrServiceResponse<List<Project>> getProjects(Selector projectFilter) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.addFilterQuery("DOCUMENT_TYPE:PROJECT");
        solrQuery.setRows(projectFilter.getPaging().getRows());
        solrQuery.setStart(projectFilter.getPaging().getStart());

        SolrQueryBuilder<Project> queryBuilder = new SolrQueryBuilder<Project>(solrQuery, Project.class);
        
        filterQueryBuilder.applyFilter(queryBuilder, projectFilter.getFilters(), Project.class);
        SortQueryBuilder.applySort(queryBuilder, projectFilter.getSort(), Project.class);
        FieldsQueryBuilder.applyFields(queryBuilder, projectFilter.getFields(), Project.class);

        System.out.println(solrQuery);
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<Project> solrResults = queryResponse.getBeans(Project.class);

        SolrServiceResponse<List<Project>> solrRes = new SolrServiceResponse<List<Project>>();
        solrRes.setTotalResultCount(queryResponse.getResults().getNumFound());
        solrRes.setResult(solrResults);
        return solrRes;
    }

    // TODO to integrate with existing getProject functions.
    public SolrServiceResponse<List<Project>> getNewProjectsByLaunchDate(String cityName, Selector projectFilter) {
        SolrQuery solrQuery = new SolrQuery();

        if (cityName == null || cityName.length() <= 0)
            solrQuery.setQuery("*:*");
        else
            solrQuery.setQuery("CITY:" + cityName);

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(Calendar.getInstance().getTime());
        String fq = "DOCUMENT_TYPE:PROJECT AND VALID_LAUNCH_DATE:[* TO " + timeStamp + "] "
                + "AND -PROJECT_STATUS:cancelled AND -PROJECT_STATUS:\"on hold\"";
        solrQuery.setFilterQueries(fq);

        solrQuery.setRows(projectFilter.getPaging().getRows());
        solrQuery.setSort("VALID_LAUNCH_DATE", SolrQuery.ORDER.desc);

        SolrQueryBuilder<Project> queryBuilder = new SolrQueryBuilder<Project>(solrQuery, Project.class);
        FieldsQueryBuilder.applyFields(queryBuilder, projectFilter.getFields(), Project.class);

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<Project> solrResults = queryResponse.getBeans(Project.class);

        SolrServiceResponse<List<Project>> solrRes = new SolrServiceResponse<List<Project>>();
        solrRes.setTotalResultCount(queryResponse.getResults().getNumFound());
        solrRes.setResult(solrResults);
        return solrRes;

    }

    public static void main(String[] args) {
        Selector projectFilter = new Selector();
        projectFilter
                .setFilters("{\"and\":[{\"range\":{\"bedrooms\":{\"from\":\"2\",\"to\":\"3\"}}},{\"equal\":{\"bathrooms\":[2]}}]}");
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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // new ProjectSolrDao().getProjects(projectFilter);
    }
}
