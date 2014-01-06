package com.proptiger.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.seller.Agent;
import com.proptiger.data.repo.AgentDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class BrokerAgentService {

	@Autowired
	private AgentDao agentDao;
	
	public Agent getAgent(Integer agentId){
		Agent agent = agentDao.findOne(agentId);
		return agent;
	}
}
