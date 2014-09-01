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

    public List<PortfolioListing> findByUserIdAndSourceTypeInAndListingStatusInOrderByListingIdDesc(
            Integer userId,
            List<Source> sourceType,
            List<ListingStatus> listingStatus,
            Pageable limitOffsetPageRequest);

    public PortfolioListing findByListingIdAndListingStatusIn(Integer listingId, List<ListingStatus> listingStatus);

    public PortfolioListing findByUserIdAndNameAndProjectIdAndListingStatusInAndSourceTypeIn(
            Integer userId,
            String name,
            Integer projectId,
            List<ListingStatus> listingStatus,
            List<Source> sourceType);

    public List<PortfolioListing> findByTypeIdAndListingStatusAndSourceTypeIn(
            Integer typeId,
            ListingStatus listingStatus,
            List<Source> sourceType);

}
