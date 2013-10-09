package com.proptiger.data.repo.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.portfolio.PortfolioProperty;

/**
 * @author Rajeev Pandey
 *
 */
public interface PortfolioPropertyDao extends JpaRepository<PortfolioProperty, Integer>{
	public List<PortfolioProperty> findByUserId(Integer userId);
	public PortfolioProperty findByUserIdAndId(Integer userId, Integer propertyId);
	public PortfolioProperty findByUserIdAndName(Integer userId, String name);
	
}
