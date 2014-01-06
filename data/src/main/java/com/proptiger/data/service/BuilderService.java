package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Builder;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.model.enums.DocumentType;
import com.proptiger.data.model.enums.ProjectStatus;
import com.proptiger.data.model.filter.Operator;
import com.proptiger.data.model.filter.SolrQueryBuilder;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.repo.BuilderDao;
import com.proptiger.data.repo.SolrDao;
import com.proptiger.data.service.pojo.SolrServiceResponse;
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
    @Autowired
    private BuilderDao builderDao;
    
    @Autowired
    private SolrDao solrDao;
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private ImageEnricher imageEnricher;

    public Builder getBuilderDetailsByProjectId(int projectId) {
        Builder builder = builderDao.findByProjectId(projectId);
        imageEnricher.setBuilderImages("logo", builder);

        return builder;
    }
    
    /**
     * This methods get builder info with some derived information about total projects of this builder and 
     * total ongoing projects etc.
     * @param builderId
     * @return
     */
    public Builder getBuilderInfo(Integer builderId, Selector selector){
    	int projectsToShow = 3;
    	Builder builder = builderDao.getBuilderById(builderId);
    	if(builder == null){
    		throw new ResourceNotAvailableException(ResourceType.BUILDER, ResourceTypeAction.GET);
    	}
    	List<String> projectStatusNotIn = new ArrayList<>();
    	projectStatusNotIn.add(ProjectStatus.ON_HOLD.getStatus());
    	projectStatusNotIn.add(ProjectStatus.CANCELLED.getStatus());
    	projectStatusNotIn.add(ProjectStatus.NOT_LAUNCHED.getStatus());
    	/*
    	 * creating selector to find total projects of builder
    	 */
    	Selector totalProjectSelector = createSelectorForTotalProjectOfBuilder(builderId, projectStatusNotIn, selector);
    	SolrServiceResponse<List<Project>> totalProjects = propertyService.getPropertiesGroupedToProjects(totalProjectSelector);
    	
    	builder.setTotalProjects(((Long)totalProjects.getTotalResultCount()).intValue());
    	/*
    	 * Updating selector to find total ongoing projects of builder
    	 */
    	projectStatusNotIn.add(ProjectStatus.OCCUPIED.getStatus());
    	projectStatusNotIn.add(ProjectStatus.READY_FOR_POSSESSION.getStatus());
    	Selector selectorForOnGoingProject = createSelectorForTotalProjectOfBuilder(builderId, projectStatusNotIn, selector);
    	SolrServiceResponse<List<Project>> ongoingProjects = propertyService.getPropertiesGroupedToProjects(selectorForOnGoingProject);
    	builder.setTotalOngoingProjects(((Long)ongoingProjects.getTotalResultCount()).intValue());

    	Iterator<Project> totalProjectItr = totalProjects.getResult().iterator();
    	Iterator<Project> ongoingProjectItr = ongoingProjects.getResult().iterator();
    	int counter = 0;
    	List<Project> projectsToReturn = new ArrayList<>();
    	while(ongoingProjectItr.hasNext() && counter++ < projectsToShow){
    		Project project = ongoingProjectItr.next();
    		imageEnricher.setProjectImages("main", project, null);
     		project.setImages( project.getImages().subList(0, 1) );
    		projectsToReturn.add(project);
    	}
    	
    	while(totalProjectItr.hasNext() && counter++ < projectsToShow){
    		Project project = totalProjectItr.next();
    		imageEnricher.setProjectImages(null, project, null);
     		project.setImages( project.getImages().subList(0, 1) );
    		projectsToReturn.add(project);
    	}
    	
    	builder.setProjects(projectsToReturn);
    	
    	return builder;
    }

	/**
	 * Creating selector object to fetch projects for builder
	 * @param builderId
	 * @param projectStatusNotIn
	 * @param selectorPassed
	 * @return
	 */
	private Selector createSelectorForTotalProjectOfBuilder(Integer builderId, List<String> projectStatusNotIn, Selector selectorPassed) {
		Map<String, List<Map<String, Map<String, Object>>>> filter = new HashMap<String, List<Map<String,Map<String,Object>>>>();
    	List<Map<String, Map<String, Object>>> list = new ArrayList<>();
    	Map<String, Map<String, Object>> searchType = new HashMap<>();
    	Map<String, Object> equalFilterCriteria = new HashMap<>();
		if(selectorPassed != null && selectorPassed.getFilters() != null){
			filter = selectorPassed.getFilters(); 
			if(filter.get(Operator.and.name()) != null){
				list = filter.get(Operator.and.name());
				if(list != null && !list.isEmpty()){
					searchType = list.get(0);
				}
			}
		}
		Selector selector = new Selector();
    	
    	if(searchType.get(Operator.equal.name()) != null){
    		equalFilterCriteria = searchType.get(Operator.equal.name());
    	}
    	equalFilterCriteria.put("builderId", builderId);
    	searchType.put(Operator.equal.name(), equalFilterCriteria);
    	
    	Map<String, Object> notEqualCriteria = new HashMap<>();
    	if(searchType.get(Operator.notEqual.name()) != null){
    		notEqualCriteria = searchType.get(Operator.notEqual.name());
    	}
    	notEqualCriteria.put("projectStatus", projectStatusNotIn);
    	searchType.put(Operator.equal.name(), notEqualCriteria);
    	list.add(searchType);
    	filter.put(Operator.and.name(), list);
    	selector.setFilters(filter);
    	selector.setPaging(new Paging(0, 100));
    	LinkedHashSet<SortBy> sortingSet = new LinkedHashSet<>();
    	SortBy sortBy = new SortBy();
    	sortBy.setField("assignedPriority");
    	sortBy.setSortOrder(SortOrder.DESC);
		sortingSet.add(sortBy );
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
	public List<Builder> getTopBuilders(
			Selector builderSelector) {
		SolrQuery solrQuery = SolrDao.createSolrQuery(DocumentType.PROJECT);
    	solrQuery.add("group", "true");
    	solrQuery.add("group.field", "BUILDER_ID");
    	solrQuery.add("group.ngroups", "true");
    	solrQuery.add("group.main", "true");
    	
    	SolrQueryBuilder<SolrResult> solrQueryBuilder = new SolrQueryBuilder<>(solrQuery, SolrResult.class);
		solrQueryBuilder.buildQuery(builderSelector, null);
		QueryResponse queryResponse = solrDao.executeQuery(solrQuery);
		List<SolrResult> response = queryResponse.getBeans(SolrResult.class);
		List<Builder> topBuilders = new ArrayList<>();
		for (SolrResult result: response) {
			topBuilders.add(result.getProject().getBuilder());
		}
		sortByPriorityAsc(topBuilders);
		return topBuilders;
	}
	
	
	/**
	 * The method is used to sort the builders based on their priority.
	 * @param topBuilders
	 */
	private void sortByPriorityAsc(List<Builder> topBuilders) {
		Collections.sort(topBuilders, new Comparator<Builder>() {
			@Override
			public int compare(Builder b1, Builder b2) {
				return (b1.getPriority() - b2.getPriority());
			}
		});
	}
}
