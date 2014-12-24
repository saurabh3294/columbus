package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.enums.Status;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.model.cms.ListingPrice;

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

    @Query("select count(*) from Listing  where sellerId=?1 and status=?2")
    public List<Long> findCountBySellerIdAndStatus(Integer sellerId, Status status);

    @Query("select count(l) from Listing l join l.property prop join prop.project as p where l.sellerId=?1 and p.version=?2 and l.status=?3 and p.projectId in (?4)")
    public List<Long> findCountBySellerIdAndVersionAndStatusAndProjectIdIn(
            Integer sellerId,
            DataVersion projectDataVersion,
            Status status,
            List<Integer> projectIds);

    @Query("select L from Listing L join fetch L.property where L.id = ?1")
    Listing findById(Integer listingId);

    @Query("select l from Listing l left join fetch l.projectSupply left join fetch l.currentListingPrice join fetch l.property prop join fetch prop.project as p join fetch p.projectStatusMaster join fetch p.builder join fetch p.locality pl join fetch pl.suburb pls join fetch pls.city where l.sellerId = ?1 and p.version = ?2 and l.status = ?3 order by l.id desc")
    public List<Listing> findBySellerIdAndVersionAndStatusWithCity(
            Integer sellerId,
            DataVersion projectDataVersion,
            Status status,
            Pageable pageable);

    @Query("select l from Listing l left join fetch l.projectSupply left join fetch l.currentListingPrice join fetch l.property prop join fetch prop.project as p join fetch p.projectStatusMaster join fetch p.builder join fetch p.locality pl join fetch pl.suburb pls join fetch pls.city where l.sellerId = ?1 and p.version = ?2 and l.status = ?3 and p.projectId in (?4) order by l.id desc")
    public List<Listing> findBySellerIdAndVersionAndStatusAndProjectIdInWithCity(
            Integer sellerId,
            DataVersion projectDataVersion,
            Status status,
            List<Integer> projectIds,
            Pageable pageable);
}