package com.proptiger.data.repo.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.portfolio.Dashboard;

/**
 * Dashboard repository class to provide CRUD operations for Dashboard resource
 * @author Rajeev Pandey
 *
 */
public interface DashboardDao extends JpaRepository<Dashboard, Integer>{

	public List<Dashboard> findByUserId(Integer userId);
	
	public List<Dashboard> findByIdAndUserId(Integer dashboardId, Integer userId);
	
	public Dashboard findByNameAndUserId(String name, Integer userId);
	
}
