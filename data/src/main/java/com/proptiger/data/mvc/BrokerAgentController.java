package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.seller.Agent;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.BrokerAgentService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
public class BrokerAgentController extends BaseController{
	
	@Autowired
	private BrokerAgentService brokerAgentService;
	
	@RequestMapping(value = "data/v1/entity/broker-agent/{agentId}")
	@ResponseBody
	public ProAPIResponse getAgent(@PathVariable Integer agentId){
		Agent agent = brokerAgentService.getAgent(agentId);
		return new ProAPISuccessCountResponse(super.filterFields(agent, null), 1);
	}
	
	@RequestMapping(value = "data/v1/entity/project/{projectId}/agent")
	@ResponseBody
	public ProAPIResponse getAgenetsForProject(@PathVariable Integer projectId){
		List<Agent> agents = brokerAgentService.getAgentsForProject(projectId);
		return new ProAPISuccessCountResponse(super.filterFields(agents, null), agents.size());
	}
}
