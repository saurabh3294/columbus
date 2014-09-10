package com.proptiger.data.service.marketplace;

import java.util.List;

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

    public List<ListingPrice> getLatestListingPrice(List<Integer> propertyId) {
        List<Integer> listingPriceId = listingDao.getListingPriceIds(propertyId);
        return listingDao.getListingPrice(listingPriceId);
    }
}
