package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.seller.Agent;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.service.BrokerAgentService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/broker-agent")
@DisableCaching
public class BrokerAgentController extends BaseController{
	
	@Autowired
	private BrokerAgentService sellerService;
	
	@RequestMapping(value = "{agentId}")
	@ResponseBody
	public ProAPIResponse getAgent(@PathVariable Integer agentId){
		Agent agent = sellerService.getAgent(agentId);
		return new ProAPISuccessCountResponse(super.filterFieldsWithTree(agent, null), 1);
	}
}
