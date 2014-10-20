package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.enums.Status;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ListingPrice;
import com.proptiger.data.pojo.FIQLSelector;

/**
 * @author Rajeev Pandey
 * 
 */
public interface ListingDao extends JpaRepository<Listing, Integer>, ListingCustomDao {

    List<Listing> findBySellerIdAndStatus(Integer sellerId, Status status);

    Listing findBySellerIdAndIdAndStatus(Integer sellerId, Integer listingId, Status status);

    @Query("select l from Listing l left join fetch l.currentListingPrice join fetch l.property prop join fetch prop.project as p where l.id=?1 and l.sellerId=?2 and p.version=?3 and l.status=?4")
    Listing findListing(Integer listingId, Integer userId, DataVersion dataVersion, Status status);

    @Query("SELECT LPr FROM ListingPrice LPr JOIN fetch LPr.listing L where LPr.id IN ?1")
    public List<ListingPrice> getListingPrice(List<Integer> listingPriceId);

    @Query(" SELECT MAX(LP.id) as maxId FROM Listing L JOIN L.listingPrices AS LP WHERE L.propertyId IN ?1 AND LP.version = 'Website' GROUP BY L.propertyId")
    public List<Integer> getListingPriceIds(List<Integer> propertyId);

    @Query("select count(l) from Listing l left join l.projectSupply left join l.currentListingPrice join l.property prop join prop.project as p join p.projectStatusMaster join p.builder join  p.locality pl join pl.suburb pls join pls.city where l.sellerId=?1 and p.version=?2  and l.status=?3")
    public List<Long> findListingsCount(Integer userId, DataVersion dataVersion, Status status);

    @Query("select L from Listing L join fetch L.property where L.id = ?1")
    Listing findById(Integer listingId);
}
