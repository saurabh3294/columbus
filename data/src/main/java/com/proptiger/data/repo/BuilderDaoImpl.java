package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.enums.DocumentType;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;

/**
 * @author mukand
 * @author Rajeev Pandey
 * 
 */
@Repository
public class BuilderDaoImpl {

    private static Logger logger = LoggerFactory.getLogger(BuilderDaoImpl.class);

    @Autowired
    private SolrDao       solrDao;

    public Builder getBuilderById(int builderId) {
        SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.BUILDER);
        solrQuery.addFilterQuery("BUILDER_ID:" + builderId);

        logger.debug("Solr query for builder by id {}", solrQuery.toString());
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<Builder> builders = queryResponse.getBeans(Builder.class);

        if (builders.size() > 0)
            return builders.get(0);

        return null;
    }

    /**
     * Get popular builders
     * 
     * @param selector
     * @return
     */
    public List<Builder> getPopularBuilders(Selector selector) {
        SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.BUILDER);
        return null;
    }
    
    public List<Builder> getBuildersByIds(List<Object> builderIds){
        if(builderIds == null || builderIds.isEmpty())
            return new ArrayList<>();
        
        String builderStr = "";
        for(Object id:builderIds){
            builderStr += ","+id;
        }
        builderStr = builderStr.substring(1);
        Selector selector = new Gson().fromJson(
                "{\"filters\":{\"and\":[{\"equal\":{\"id\":[" + builderStr 
                        + "]}}]}}", Selector.class);
        
        SolrQuery solrQuery = createSolrQuery(selector);
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<Builder> builders = queryResponse.getBeans(Builder.class);
                        
        return builders;
    }
    
    public SolrQuery createSolrQuery(Selector selector) {
        SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.BUILDER);
        solrQuery.setQuery("*:*");
        
        if (selector != null) {
            Paging paging = selector.getPaging();
            if (paging != null) {
                solrQuery.setRows(paging.getRows());
                solrQuery.setStart(paging.getStart());
            }

            SolrQueryBuilder<Builder> queryBuilder = new SolrQueryBuilder<Builder>(solrQuery, Builder.class);

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

        sortBy = new SortBy();
        sortBy.setField("name");
        sortBy.setSortOrder(SortOrder.ASC);
        sortBySet.add(sortBy);

        return sortBySet;
    }
}
