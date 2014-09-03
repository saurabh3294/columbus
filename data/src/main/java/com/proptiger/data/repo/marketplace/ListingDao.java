package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.enums.Status;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ListingPrice;

/**
 * @author Rajeev Pandey
 *
 */
public interface ListingDao extends JpaRepository<Listing, Integer>, ListingCustomDao {

    List<Listing> findBySellerIdAndStatus(Integer sellerId, Status status);

    Listing findBySellerIdAndIdAndStatus(Integer sellerId, Integer listingId, Status status);

    @Query("select l from Listing l join fetch l.currentListingPrice join fetch l.property prop join fetch prop.project as p where l.id=?1 and l.sellerId=?2 and p.version=?3 and l.status=?4")
    Listing findListing(Integer listingId, Integer userId, DataVersion dataVersion, Status status);
    
    @Query("SELECT LP FROM ListingPrice LP where LP.id IN (SELECT MAX(LP.id) FROM Listing L JOIN L.listingPrices AS LP WHERE L.propertyId = ?1) ")
    public ListingPrice getListingPrice(Integer propertyId);    

    @Query("select l from Listing l left join fetch l.currentListingPrice join fetch l.property prop join fetch prop.project as p where l.sellerId=?1 and p.version=?2  and l.status=?3")
    List<Listing> findListings(Integer userId, DataVersion dataVersion, Status status, Pageable pageable);
}
