package com.proptiger.data.repo.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.portfolio.PortfolioListing;

/**
 * @author Rajeev Pandey
 * 
 */
public interface PortfolioListingDao extends JpaRepository<PortfolioListing, Integer> {
    public List<PortfolioListing> findByUserIdOrderByListingIdDesc(Integer userId);

    public PortfolioListing findByUserIdAndListingId(Integer userId, Integer listingId);

    public PortfolioListing findByUserIdAndName(Integer userId, String name);

}
