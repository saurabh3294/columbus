package com.proptiger.data.repo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.proptiger.data.enums.DocumentType;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;

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

    public List<Builder> getBuildersByIds(List<Integer> builderIds) {
        if (builderIds == null || builderIds.isEmpty())
            return new ArrayList<>();

        String builderStr = StringUtils.join(builderIds.toArray(), ",");

        Selector selector = new Gson().fromJson("{\"filters\":{\"and\":[{\"equal\":{\"id\":[" + builderStr
                + "]}}]}, \"paging\":{\"start\":0,\"rows\":"
                + builderIds.size()
                + "}}", Selector.class);

        SolrQuery solrQuery = createSolrQuery(selector);
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
        List<Builder> builders = queryResponse.getBeans(Builder.class);

        return builders;
    }

    public SolrQuery createSolrQuery(Selector selector) {
        SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.BUILDER);
        solrQuery.setQuery("*:*");

        if (selector == null) {
            selector = new Selector();
        }

        SolrQueryBuilder<Builder> queryBuilder = new SolrQueryBuilder<Builder>(solrQuery, Builder.class);

        if (selector.getSort() == null) {
            selector.setSort(new LinkedHashSet<SortBy>());
        }

        selector.getSort().addAll(getDefaultSort());
        queryBuilder.buildQuery(selector, null);

        return solrQuery;
    }

    public Set<SortBy> getDefaultSort() {
        return new Gson()
                .fromJson(
                        "{\"sort\":[{\"field\":\"priority\",\"sortOrder\":\"ASC\"},{\"field\":\"name\",\"sortOrder\":\"ASC\"}]}",
                        Selector.class).getSort();
    }

}
