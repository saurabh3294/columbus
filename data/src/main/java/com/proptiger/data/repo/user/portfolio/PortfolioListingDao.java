package com.proptiger.data.repo.user.portfolio;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.core.enums.ListingStatus;
import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.proptiger.PortfolioListing.Source;

/**
 * @author Rajeev Pandey
 * 
 */
public interface PortfolioListingDao extends JpaRepository<PortfolioListing, Integer> {

    @Query("SELECT PL FROM PortfolioListing PL LEFT JOIN fetch PL.otherPrices OP LEFT JOIN fetch PL.listingPaymentPlan LPP "
         + " WHERE PL.userId = ?1 AND PL.sourceType IN ?2 AND PL.listingStatus IN ?3 ORDER BY PL.listingId DESC ")
    public List<PortfolioListing> findByUserIdAndSourceTypeInAndListingStatusInOrderByListingIdDesc(
            Integer userId,
            List<Source> sourceType,
            List<ListingStatus> listingStatus,
            Pageable limitOffsetPageRequest);

    public PortfolioListing findByUserIdAndListingIdAndListingStatusIn(Integer userId, Integer listingId, List<ListingStatus> listingStatus);

    /**
     * This query should be used  by internal service and should not be exposed to users 
     * @param listingId
     * @param listingStatus
     * @return
     */
    public PortfolioListing findByListingIdAndListingStatusIn(Integer listingId, List<ListingStatus> listingStatus);

    
    public PortfolioListing findByUserIdAndNameAndProjectIdAndListingStatusInAndSourceTypeIn(
            Integer userId,
            String name,
            Integer projectId,
            List<ListingStatus> listingStatus,
            List<Source> sourceType);

    /**
     * This query should be used  by internal service and should not be exposed to users 
     * @param listingId
     * @param listingStatus
     * @return
     */
    public List<PortfolioListing> findByTypeIdAndListingStatusAndSourceTypeIn(
            Integer typeId,
            ListingStatus listingStatus,
            List<Source> sourceType);

}
