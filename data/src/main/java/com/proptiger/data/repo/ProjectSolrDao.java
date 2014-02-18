/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.enums.DocumentType;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.service.pojo.PaginatedResponse;
import com.proptiger.data.util.SolrResponseReader;

/**
 * 
 * @author mukand
 */
@Repository
public class ProjectSolrDao {
    private static Logger      logger             = LoggerFactory.getLogger(ProjectSolrDao.class);
    @Autowired
    private SolrDao            solrDao;

    private SolrResponseReader solrResponseReader = new SolrResponseReader();

    /*
     * @Autowired private FilterQueryBuilder filterQueryBuilder;
     */

    public PaginatedResponse<List<Project>> getProjects(Selector selector) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.addFilterQuery("DOCUMENT_TYPE:PROJECT");
        solrQuery.setRows(selector.getPaging().getRows());
        solrQuery.setStart(selector.getPaging().getStart());

        SolrQueryBuilder<SolrResult> queryBuilder = new SolrQueryBuilder<SolrResult>(solrQuery, SolrResult.class);

        queryBuilder.buildQuery(selector, null);
        System.out.println(solrQuery.toString());
        logger.debug("Solr query for get projects {}", solrQuery.toString());
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> solrResults = queryResponse.getBeans(SolrResult.class);
        List<Project> projectList = new ArrayList<Project>();
        for (SolrResult solrResult : solrResults) {
            projectList.add(solrResult.getProject());
        }
        PaginatedResponse<List<Project>> solrRes = new PaginatedResponse<List<Project>>();
        solrRes.setTotalCount(queryResponse.getResults().getNumFound());
        solrRes.setResults(projectList);
        return solrRes;
    }

    // TODO to integrate with existing getProject functions.
    public PaginatedResponse<List<Project>> getNewProjectsByLaunchDate(String cityName, Selector selector) {
        SolrQuery solrQuery = new SolrQuery();

        if (cityName == null || cityName.length() <= 0)
            solrQuery.setQuery("*:*");
        else
            solrQuery.setQuery("CITY:" + cityName);

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(Calendar.getInstance().getTime());
        String fq = "DOCUMENT_TYPE:PROJECT AND VALID_LAUNCH_DATE:[* TO " + timeStamp
                + "] "
                + "AND -PROJECT_STATUS:cancelled AND -PROJECT_STATUS:\"on hold\"";
        solrQuery.setFilterQueries(fq);

        solrQuery.setRows(selector.getPaging().getRows());
        solrQuery.setSort("VALID_LAUNCH_DATE", SolrQuery.ORDER.desc);

        SolrQueryBuilder<Project> queryBuilder = new SolrQueryBuilder<Project>(solrQuery, Project.class);
        // FieldsQueryBuilder.applyFields(queryBuilder, selector);
        queryBuilder.buildQuery(selector, null);

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> totalSolrResults = queryResponse.getBeans(SolrResult.class);

        List<Project> solrResults = new ArrayList<>();
        for (SolrResult solr : totalSolrResults)
            solrResults.add(solr.getProject());

        PaginatedResponse<List<Project>> solrRes = new PaginatedResponse<List<Project>>();
        solrRes.setTotalCount(queryResponse.getResults().getNumFound());
        solrRes.setResults(solrResults);

        return solrRes;

    }

    public List<SolrResult> getProjectsOnIds(Set<Integer> projectIds) {
        SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery("*:*");
        solrQuery.addFilterQuery("DOCUMENT_TYPE:PROJECT");

        List<Object> projectIdList = new ArrayList<>();
        projectIdList.addAll(projectIds);

        SolrQueryBuilder<Project> queryBuilder = new SolrQueryBuilder<>(solrQuery, Project.class);
        queryBuilder.addEqualsFilter("projectId", projectIdList);
        logger.debug("Solr query for get projects by ids {}", solrQuery.toString());
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);

        return queryResponse.getBeans(SolrResult.class);
    }

    public List<SolrResult> sortingSimilarProjects(
            Set<Integer> projectIds,
            Double latitude,
            Double longitude,
            int projectImportance,
            int rows) {

        List<Object> projectIdList = new ArrayList<>();
        projectIdList.addAll(projectIds);

        SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery("*:*");
        SolrQueryBuilder<Project> queryBuilder = new SolrQueryBuilder<>(solrQuery, Project.class);
        queryBuilder.addEqualsFilter("projectId", projectIdList);

        solrQuery.addFilterQuery("DOCUMENT_TYPE:PROJECT");

        if (latitude != null && longitude != null)
            solrQuery.addSort("geodist(GEO," + latitude + "," + longitude + ")", ORDER.asc);

        solrQuery.addSort("abs(sub(" + projectImportance + ",DISPLAY_ORDER))", ORDER.asc);
        solrQuery.setRows(rows);
        logger.debug("Solr query for get similar projects {}", solrQuery.toString());
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);

        return queryResponse.getBeans(SolrResult.class);
    }

    public PaginatedResponse<List<Project>> getUpcomingNewProjects(String cityName, Selector selector) {
        SolrQuery solrQuery = new SolrQuery();

        if (cityName == null || cityName.length() <= 0)
            solrQuery.setQuery("*:*");
        else
            solrQuery.setQuery("CITY:" + cityName);

        String fq = "DOCUMENT_TYPE:PROJECT AND (PROJECT_STATUS:\"pre launch\" OR PROJECT_STATUS:\"not launched\")";
        solrQuery.setFilterQueries(fq);

        solrQuery.setRows(selector.getPaging().getRows());
        solrQuery.addSort("DISPLAY_ORDER", SolrQuery.ORDER.asc);
        solrQuery.addSort("PROJECT_PRIORITY", SolrQuery.ORDER.asc);
        solrQuery.addSort("PROJECT_ID", SolrQuery.ORDER.asc);
        solrQuery.addSort("BEDROOMS", SolrQuery.ORDER.asc);
        solrQuery.addSort("SIZE", SolrQuery.ORDER.asc);
        logger.debug("Solr query for get upcomming new projects {}", solrQuery.toString());
        SolrQueryBuilder<Project> queryBuilder = new SolrQueryBuilder<Project>(solrQuery, Project.class);
        queryBuilder.buildQuery(selector, null);

        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<SolrResult> totalSolrResults = queryResponse.getBeans(SolrResult.class);

        List<Project> solrResults = new ArrayList<>();
        for (SolrResult solr : totalSolrResults)
            solrResults.add(solr.getProject());

        PaginatedResponse<List<Project>> solrRes = new PaginatedResponse<List<Project>>();
        solrRes.setTotalCount(queryResponse.getResults().getNumFound());
        solrRes.setResults(solrResults);

        return solrRes;
    }

    public List<SolrResult> getProjectsByGEODistanceByLocality(
            int localityId,
            double latitude,
            double longitude,
            int rows) {
        SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery("LOCALITY_ID:" + localityId);
        solrQuery.setFilterQueries("DOCUMENT_TYPE:PROJECT AND HAS_GEO:1");
        solrQuery.setRows(rows);

        SolrQueryBuilder<Project> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, Project.class);
        solrQueryBuilder.addGeoFilter("geo", 0, latitude, longitude);
        solrQuery.setSort("geodist()", ORDER.desc);
        solrQuery.add("fl", "* __RADIUS__:geodist()");
        logger.info("Solr query for get projects by GEO {}", solrQuery.toString());
        return solrDao.executeQuery(solrQuery).getBeans(SolrResult.class);

    }

    public static void main(String[] args) {
        Selector projectFilter = new Selector();
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
        // projectFilter.setSort(sort);
        ObjectMapper mapper = new ObjectMapper();

        try {
            System.out.println(mapper.writeValueAsString(projectFilter));
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // new ProjectSolrDao().getProjects(projectFilter);
    }

    /**
     * This method get project count by project status for provided filter.
     * 
     * @param selector
     * @return
     */
    public Map<String, Long> getProjectStatusCount(Selector selector) {
        Map<String, Long> projectStatusCount = new HashMap<String, Long>();
        SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.PROJECT);

        solrQuery.add("facet", "true");
        solrQuery.add("facet.field", "PROJECT_STATUS");
        SolrQueryBuilder<Project> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, Project.class);
        solrQueryBuilder.buildQuery(selector, null);
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        FacetField facetField = queryResponse.getFacetField("PROJECT_STATUS");
        if (facetField != null) {
            for (Count count : facetField.getValues()) {
                projectStatusCount.put(count.getName(), count.getCount());
            }
        }
        return projectStatusCount;
    }
}
