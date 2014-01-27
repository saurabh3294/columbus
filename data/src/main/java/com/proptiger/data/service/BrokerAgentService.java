package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.seller.Agent;
import com.proptiger.data.model.seller.ProjectAssignmentRule;
import com.proptiger.data.model.seller.RuleAgentMapping;
import com.proptiger.data.model.seller.RuleLocalityMapping;
import com.proptiger.data.model.seller.RuleProjectMapping;
import com.proptiger.data.repo.seller.AgentDao;
import com.proptiger.data.repo.seller.ProjectAssignmentRuleDao;
import com.proptiger.data.repo.seller.RuleAgentMappingDao;
import com.proptiger.data.repo.seller.RuleLocalityMappingDao;
import com.proptiger.data.repo.seller.RuleProjectMappingDao;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * Service class to get agent related details
 * @author Rajeev Pandey
 *
 */
@Service
public class BrokerAgentService {

	private static final int DEFAULT_LOCALITY_IMAGE_COUNT = 3;

	@Autowired
	private AgentDao agentDao;
	
	@Autowired
	private RuleAgentMappingDao ruleAgentMappingDao;
	
	@Autowired
	private RuleProjectMappingDao ruleProjectMappingDao;
	
	@Autowired
	private ProjectAssignmentRuleDao projectAssignmentRuleDao;
	
	@Autowired
	private RuleLocalityMappingDao ruleLocalityMappingDao;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private LocalityService localityService;
	
	/**
	 * Get agent by agent id
	 * @param agentId
	 * @return
	 */
	@Cacheable(value=Constants.Cache.CACHE, key="#agentId")
	public Agent getAgent(Integer agentId){
		Agent agent = agentDao.findOne(agentId);
		if(agent == null){
			throw new ResourceNotAvailableException(ResourceType.AGENT, ResourceTypeAction.GET);
		}
		populateOtherDetails(agent);
		return agent;
	}

	/**
	 * Populating other details of agent like localities serviced
	 * @param agent
	 */
	private void populateOtherDetails(Agent agent) {
		List<Integer> ruleIdList = new ArrayList<Integer>();
		/*
		 * Getting all rules for agent
		 */
		List<RuleAgentMapping> agentRules = ruleAgentMappingDao
				.findByAgentId(agent.getId());

		if (agentRules == null || agentRules.size() == 0) {
			// no rule for agent, so find rules of broker
			List<ProjectAssignmentRule> brokerRules = projectAssignmentRuleDao
					.findByBrokerId(agent.getBroker().getId());
			if (brokerRules != null) {
				for (ProjectAssignmentRule rule : brokerRules) {
					ruleIdList.add(rule.getId());
				}
			}
		} else {
			for (RuleAgentMapping rule : agentRules) {
				ruleIdList.add(rule.getRuleId());
			}
		}
		List<Integer> localityIds = new ArrayList<Integer>();
		if(ruleIdList.size() > 0){
			/*
			 * ruleIdList populated either for agent specific rules or for broker
			 * rules. Now find if rules are mapped with project or locality, and get
			 * all unique locality ids
			 */
			Iterable<RuleProjectMapping> projectRules = ruleProjectMappingDao
					.findByRuleIdIn(ruleIdList);
			if (projectRules == null) {
				// no project rules so find locality rules
				List<RuleLocalityMapping> localityRules = ruleLocalityMappingDao
						.findByRuleIdIn(ruleIdList);
				if (localityRules != null) {
					for (RuleLocalityMapping rule : localityRules) {
						if (localityIds.contains(rule.getRuleId())) {
							localityIds.add(rule.getRuleId());
						}
					}
				}
			} else {
				Set<Integer> projectIds = new HashSet<Integer>();
				for (RuleProjectMapping rule : projectRules) {
					projectIds.add(rule.getProjectId());
				}
				List<Project> projectList = projectService
						.getProjectsByIds(projectIds);
				if (projectList != null) {
					for (Project project : projectList) {
						if (!localityIds.contains(project.getLocalityId())) {
							localityIds.add(project.getLocalityId());
						}
					}
				}
			}
		}
		List<Locality> localities = new ArrayList<Locality>();
		for(Integer localityId: localityIds){
			localities.add(localityService.getLocalityInfo(localityId, DEFAULT_LOCALITY_IMAGE_COUNT));
		}
		agent.setLocalitiesServiced(localities);
	}
	
	/**
	 * Get agents for given project id
	 * @param projectId
	 * @return
	 */
	@Cacheable(value=Constants.Cache.CACHE, key="#projectId")
	public List<Agent> getAgentsForProject(Integer projectId){
		List<RuleProjectMapping> projectRules = ruleProjectMappingDao.findByProjectId(projectId);
		List<Integer> ruleIds = new ArrayList<Integer>();
		if(projectRules == null || projectRules.size() == 0){
			
		}
		else{
			for(RuleProjectMapping rule: projectRules){
				ruleIds.add(rule.getRuleId());
			}
		}
		/*
		 * Now get rule id and agent mapping to get all agents
		 */
		List<Integer> agentIds = ruleAgentMappingDao.findAgentsIdsWhereRuleIdsIn(ruleIds);
		List<Agent> agentList = new ArrayList<Agent>();
		for(Integer agentId: agentIds){
			Agent agent = getAgent(agentId);
			agentList.add(agent);
		}
		return agentList;
	}
}
