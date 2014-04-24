package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.data.model.LandMark;
import com.proptiger.data.model.LandMarkResult;
import com.proptiger.data.model.enums.DocumentType;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;

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

}
