package com.proptiger.data.repo.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.portfolio.PortfolioListing;

/**
 * @author Rajeev Pandey
 * 
 */
public interface PortfolioListingDao extends JpaRepository<PortfolioListing, Integer> {
    
    public List<PortfolioListing> findByUserIdAndDeletedFlagOrderByListingIdDesc(Integer userId, Boolean deletedFlag);
    
    public PortfolioListing findByUserIdAndListingIdAndDeletedFlag(Integer userId, Integer listingId, Boolean deletedFlag);

    public PortfolioListing findByUserIdAndNameAndDeletedFlag(Integer userId, String name, Boolean deletedFlag);
    
    public PortfolioListing findByListingIdAndDeletedFlag(Integer listingId, Boolean deletedFlag);

}
