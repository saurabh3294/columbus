package com.proptiger.data.repo.user.portfolio;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.enums.portfolio.ListingStatus;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.model.user.portfolio.PortfolioListing.Source;

/**
 * @author Rajeev Pandey
 * 
 */
public interface PortfolioListingDao extends JpaRepository<PortfolioListing, Integer> {

    public List<PortfolioListing> findByUserIdAndDeletedFlagAndSourceTypeInAndListingStatusInOrderByListingIdDesc(
            Integer userId,
            Boolean deletedFlag,
            List<Source> sourceType,
            List<ListingStatus> listingStatus, Pageable limitOffsetPageRequest);

    public PortfolioListing findByListingIdAndDeletedFlag(Integer listingId, Boolean deletedFlag);

    public PortfolioListing findByUserIdAndNameAndProjectIdAndDeletedFlagAndSourceTypeIn(
            Integer userId,
            String name,
            Integer projectId,
            Boolean deletedFlag,
            List<Source> sourceType);

}
