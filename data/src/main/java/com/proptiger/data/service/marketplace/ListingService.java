package com.proptiger.data.service.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ListingPrice;
import com.proptiger.data.repo.marketplace.ListingDao;

/**
 * @author Rajeev Pandey
 * 
 */
@Service
public class ListingService {

    @Autowired
    private ListingDao listingDao;
   
    public Listing getListingByListingId(Integer listingId) {
        return listingDao.findOne(listingId);
    }

    public ListingPrice getLatestListingPrice(Integer propertyId) {
        return listingDao.getListingPrice(propertyId);
    }
}
