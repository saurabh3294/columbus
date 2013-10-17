package com.proptiger.data.repo.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.portfolio.PortfolioListing;

/**
 * @author Rajeev Pandey
 *
 */
public interface PortfolioListingDao extends JpaRepository<PortfolioListing, Integer>{
	public List<PortfolioListing> findByUserId(Integer userId);
	public PortfolioListing findByUserIdAndId(Integer userId, Integer propertyId);
	public PortfolioListing findByUserIdAndName(Integer userId, String name);
	
}