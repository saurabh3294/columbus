package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.seller.Agent;

/**
 * @author Rajeev Pandey
 *
 */
public interface AgentDao extends JpaRepository<Agent, Integer> {

}
