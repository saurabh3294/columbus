package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.Builder;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.enums.DocumentType;
import com.proptiger.data.model.filter.Operator;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.repo.BuilderDao;
import com.proptiger.data.repo.SolrDao;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * 
 * @author mukand
 * @author Rajeev Pandey
 */
@Service
public class BuilderService {
    private static Logger   logger = LoggerFactory.getLogger(BuilderService.class);
    @Autowired
    private BuilderDao      builderDao;

    @Autowired
    private SolrDao         solrDao;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ImageEnricher   imageEnricher;

    @Autowired
    private ProjectService projectService;

    public Builder getBuilderDetailsByProjectId(int projectId) {
        Builder builder = builderDao.findByProjectId(projectId);
        imageEnricher.setBuilderImages(builder);

        return builder;
    }

    /**
     * This methods get builder info with some derived information about total
     * projects of this builder and total ongoing projects etc.
     * 
     * @param builderId
     * @return
     */
    @Cacheable(value = Constants.CacheName.BUILDER, key = "#builderId+\":\"+#selector")
    public Builder getBuilderInfo(Integer builderId, Selector selector) {
        Builder builder = builderDao.getBuilderById(builderId);
        if (builder == null) {
            logger.error("Builder id {} not found", builderId);
            throw new ResourceNotAvailableException(ResourceType.BUILDER, ResourceTypeAction.GET);
        }

        Selector tempSelector = createSelectorForTotalProjectOfBuilder(builderId, selector);
        Map<String, Long> projectStatusCountMap = projectService.getProjectStatusCount(tempSelector);
        builder.setProjectStatusCount(projectStatusCountMap);

        return builder;
    }

    /**
     * Creating selector object to fetch projects for builder
     * 
     * @param builderId
     * @param projectStatusNotIn
     * @param selectorPassed
     * @return
     */
    private Selector createSelectorForTotalProjectOfBuilder(Integer builderId, Selector selectorPassed) {
        Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String, Map<String, Object>>>>();
        List<Map<String, Map<String, Object>>> list = new ArrayList<>();
        Map<String, Map<String, Object>> searchType = new HashMap<>();
        Map<String, Object> equalFilterCriteria = new HashMap<>();
        if (selectorPassed != null && selectorPassed.getFilters() != null) {
            filter = selectorPassed.getFilters();
            if (filter.get(Operator.and.name()) != null) {
                list = filter.get(Operator.and.name());
                if (list != null && !list.isEmpty()) {
                    searchType = list.get(0);
                }
            }
        }
        Selector selector = new Selector();

        if (searchType.get(Operator.equal.name()) != null) {
            equalFilterCriteria = searchType.get(Operator.equal.name());
        }
        equalFilterCriteria.put("builderId", builderId);
        searchType.put(Operator.equal.name(), equalFilterCriteria);

        list.add(searchType);
        filter.put(Operator.and.name(), list);
        selector.setFilters(filter);
        selector.setPaging(new Paging(0, 100));
        LinkedHashSet<SortBy> sortingSet = new LinkedHashSet<>();
        SortBy sortBy = new SortBy();
        sortBy.setField("assignedPriority");
        sortBy.setSortOrder(SortOrder.DESC);
        sortingSet.add(sortBy);
        selector.setSort(sortingSet);
        return selector;
    }

    /**
     * Get top builders, use the builders in that suburb/localities only.
     * However if priority of all builders is same then use the popular projects
     * and extract the unique builder of popular projects
     * 
     * @param builderSelector
     * @return
     */
    public List<Builder> getTopBuilders(Selector builderSelector) {
        SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.PROJECT);
        solrQuery.add("group", "true");
        solrQuery.add("group.field", "BUILDER_ID");
        solrQuery.add("group.ngroups", "true");
        solrQuery.add("group.main", "true");

        SolrQueryBuilder<SolrResult> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, SolrResult.class);
        solrQueryBuilder.buildQuery(builderSelector, null);
        QueryResponse queryResponse = solrDao.executeQuery(solrQuery);

        List<Builder> topBuilders = queryResponse.getBeans(Builder.class);

        List<Integer> builderIds = getBuilderIds(topBuilders);
        List<Builder> builders = builderDao.getBuildersByIds(builderIds);
        imageEnricher.setBuildersImages(builders);
        return builders;
    }

    /**
     * Get top builders in a locality
     * 
     * @param localityId
     * @return
     */
    public List<Builder> getTopBuildersForLocality(Integer localityId) {
        Selector selector = new Gson().fromJson("{\"filters\":{\"and\":[{\"equal\":{\"localityId\":" + localityId
                + "}}]}}", Selector.class);
        return getTopBuilders(selector);
    }

    @Cacheable(value = Constants.CacheName.BUILDER, key = "#builderId")
    public Builder getBuilderById(int builderId) {
        return builderDao.getBuilderById(builderId);
    }

    /**
     * This method will return the list of builder Ids from List of Builders.
     * 
     * @param builders
     * @return
     */
    private List<Integer> getBuilderIds(List<Builder> builders) {
        if (builders == null)
            return new ArrayList<>();

        List<Integer> builderIds = new ArrayList<>();
        for (Builder builder : builders) {
            builderIds.add(builder.getId());
        }

        return builderIds;
    }

}
