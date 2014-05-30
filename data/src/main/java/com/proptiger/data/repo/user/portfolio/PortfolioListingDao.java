package com.proptiger.data.repo.user.portfolio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.model.user.portfolio.PortfolioListing.Source;

/**
 * @author Rajeev Pandey
 * 
 */
public interface PortfolioListingDao extends
		JpaRepository<PortfolioListing, Integer> {

	public List<PortfolioListing> findByUserIdAndDeletedFlagAndSourceTypeInOrderByListingIdDesc(
			Integer userId, Boolean deletedFlag, List<Source> sourceType);

	public PortfolioListing findByListingIdAndDeletedFlag(Integer listingId,
			Boolean deletedFlag);

	public PortfolioListing findByUserIdAndNameAndDeletedFlagAndSourceTypeIn(
			Integer userId, String name, Boolean deletedFlag,
			List<Source> sourceType);

}
