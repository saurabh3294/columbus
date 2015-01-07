package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.proptiger.core.enums.DataVersion;
import com.proptiger.core.enums.Status;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.model.cms.Listing;
import com.proptiger.core.model.cms.ListingPrice;
import com.proptiger.data.repo.ListingPriceDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class ListingPriceService {

    @Autowired
    private ListingPriceDao listingPriceDao;

    public ListingPrice createOrUpdateListingPrice(ListingPrice listingPrice, Listing listing) {
        preCreateValidation(listingPrice, listing);
        ListingPrice created = listingPriceDao.saveAndFlush(listingPrice);
        return created;
    }

    /**
     * Either price or price per unit area should not be null and other price is
     * optional
     * 
     * @param listingPrice
     * @param listing
     */
    private void preCreateValidation(ListingPrice listingPrice, Listing listing) {
        //ignoring villa related prices
        listingPrice.setConstructionCostPerUnitArea(0);
        listingPrice.setPlotCostPerUnitArea(0);
        boolean isValidPrice = false;
        if (listingPrice.getPricePerUnitArea() != null && listingPrice.getPricePerUnitArea() > 0) {
            isValidPrice = true;
            listingPrice.setPrice(0);
        }
        if (!isValidPrice && listingPrice.getPrice() != null && listingPrice.getPrice() > 0) {
            isValidPrice = true;
            listingPrice.setPricePerUnitArea(0);
        }
        if (listingPrice.getOtherCharges() != null && listingPrice.getOtherCharges() < 0) {
            throw new BadRequestException("Invalid other charges");
        }
        if(!isValidPrice){
            throw new BadRequestException("Invalid price or pricePerUnitArea");
        }
        listingPrice.setListingId(listing.getId());
        listingPrice.setVersion(DataVersion.Website);
        listingPrice.setStatus(Status.Active);
        listingPrice.setUpdatedBy(listing.getUpdatedBy());
    }

    public List<ListingPrice> getListingPrices(List<Integer> listingPriceIds) {
        List<ListingPrice> listingPrices = new ArrayList<>();
        if (listingPriceIds != null && listingPriceIds.size() > 0) {
            Iterable<ListingPrice> prices = listingPriceDao.findAll(listingPriceIds);
            listingPrices = Lists.newArrayList(prices);
        }
        return listingPrices;
    }

    public List<ListingPrice> getListingPricesOfListings(List<Listing> listings) {
        List<Integer> listingPriceIds = new ArrayList<>(listings.size());
        for (Listing l : listings) {
            if (l.getCurrentPriceId() != null) {
                listingPriceIds.add(l.getCurrentPriceId());
            }
        }
        return getListingPrices(listingPriceIds);
    }
}
