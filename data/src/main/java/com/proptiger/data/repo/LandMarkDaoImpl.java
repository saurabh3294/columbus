package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.enums.DocumentType;
import com.proptiger.core.enums.SortOrder;
import com.proptiger.core.model.cms.LandMark;
import com.proptiger.core.pojo.Paging;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.pojo.SortBy;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.data.model.LandMarkResult;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.util.SolrResponseReader;

public class LandMarkDaoImpl {
    private static Logger logger = LoggerFactory.getLogger(ProjectSolrDao.class);
    @Autowired
    private SolrDao       solrDao;

    public List<LandMark> getLocalityAmenitiesOnSelector(Selector selector) {
        SolrQuery solrQuery = createSolrQuery(selector);
        System.out.println(solrQuery.toString());
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<LandMarkResult> localityAmenityResults = queryResponse.getBeans(LandMarkResult.class);

        List<LandMark> localityAmenitiesList = new ArrayList<LandMark>();

        if (localityAmenityResults != null) {
            for (LandMarkResult localityAmenityResult : localityAmenityResults) {
                localityAmenitiesList.add(localityAmenityResult.getLocalityAmenity());
            }
        }
        
        return localityAmenitiesList;
    }

    public Map<String, Integer> getAmenitiesTypeCount(Selector selector) {
        SolrQuery solrQuery = createSolrQuery(selector);
        solrQuery.add("facet", "true");
        solrQuery.add("facet.field", "LANDMARK_DISPLAY_TYPE");
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        Map<String, Map<String, Integer>> facetMap = SolrResponseReader.getFacetResults(queryResponse.getResponse());
        return facetMap.get("LANDMARK_DISPLAY_TYPE");
    }
    
    /*
     * Return Amenity List based on selector provided with
     * max group count of 23 (Currently supported amenity types count) 
     * and each group count will have max 10 
     * amenities to reduce the api data overhead.
     */
    public List<LandMark> getAmenityListByGroupSelector(Selector selector) {
        selector.setPaging(null);
        SolrQuery solrQuery = createSolrQuery(selector);
        solrQuery.add("group", "true");
        solrQuery.add("group.ngroups", "true");
        solrQuery.add("group.limit", "10");//Returning max 10 amenity of each amenity type
        solrQuery.add("rows", "23"); //To count of amenity type is 23, Hence max rows of group
        solrQuery.add("group.field", "LANDMARK_TYPE");
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<LandMark> amenitiesList = new ArrayList<LandMark>();
        for(GroupCommand groupCommand : queryResponse.getGroupResponse().getValues()) {
            for (Group group : groupCommand.getValues()) {
                List<LandMarkResult> landmarkResults = convertLandMarkResult(group.getResult());
                for(LandMarkResult landmarkResult : landmarkResults) {
                    amenitiesList.add(landmarkResult.getLocalityAmenity());
                }
            }
        }
        return amenitiesList;
    }
    
    private SolrQuery createSolrQuery(Selector selector) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.addFilterQuery(DocumentType.getDocumentTypeFilter(DocumentType.LANDMARK));

        if (selector != null) {
            Paging paging = selector.getPaging();
            if (paging != null) {
                solrQuery.setRows(paging.getRows());
                solrQuery.setStart(paging.getStart());
            }

            SolrQueryBuilder<LandMarkResult> queryBuilder = new SolrQueryBuilder<LandMarkResult>(
                    solrQuery,
                    LandMarkResult.class);

            if (selector.getSort() == null) {
                selector.setSort(new LinkedHashSet<SortBy>());
            }

            selector.getSort().addAll(getDefaultSort());
            queryBuilder.buildQuery(selector, null);
        }

        return solrQuery;
    }

    private Set<SortBy> getDefaultSort() {
        Set<SortBy> sortBySet = new LinkedHashSet<SortBy>();
        SortBy sortBy = new SortBy();
        sortBy.setField("priority");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);

        return sortBySet;
    }

    private List<LandMarkResult> convertLandMarkResult(SolrDocumentList result) {
        return new DocumentObjectBinder().getBeans(LandMarkResult.class, result);
    }
}
