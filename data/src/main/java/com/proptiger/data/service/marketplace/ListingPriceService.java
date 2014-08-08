package com.proptiger.data.service.marketplace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.enums.Status;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ListingPrice;
import com.proptiger.data.repo.ListingPriceDao;
import com.proptiger.exception.BadRequestException;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class ListingPriceService {

    @Autowired
    private ListingPriceDao listingPriceDao;
    
    public ListingPrice createListingPrice(ListingPrice listingPrice, Listing listing){
        preCreateValidation(listingPrice, listing);
        ListingPrice created = listingPriceDao.saveAndFlush(listingPrice);
        return created;
    }

    private void preCreateValidation(ListingPrice listingPrice, Listing listing) {
        //TODO apply validation over four price related fields
        if(listingPrice.getPricePerUnitArea() != null && listingPrice.getPricePerUnitArea() <= 0){
            throw new BadRequestException("Invalid price per unit area");
        }
        if(listingPrice.getOtherCharges() != null && listingPrice.getOtherCharges() < 0){
            throw new BadRequestException("Invalid other charges");
        }
        if(listingPrice.getPlotCostPerUnitArea() != null){
            if(listingPrice.getPlotCostPerUnitArea() < 0){
            throw new BadRequestException("Invalid plot cost per unit area");
            }
        }
        else{
            listingPrice.setPlotCostPerUnitArea(0);
        }
        if(listingPrice.getConstructionCostPerUnitArea() != null){
            if(listingPrice.getConstructionCostPerUnitArea() < 0){
            throw new BadRequestException("Invalid construction cost per unit area");
            }
        }
        else{
            listingPrice.setConstructionCostPerUnitArea(0);
        }
        if(listingPrice.getPrice() != null && listingPrice.getPrice() < 0){
            throw new BadRequestException("Invalid price");
        }
        listingPrice.setListingId(listing.getId());
        listingPrice.setVersion(DataVersion.Website);
        listingPrice.setStatus(Status.Active);
        listingPrice.setUpdatedBy(listing.getUpdatedBy());
    }

    public List<ListingPrice> getListingPrices(List<Integer> listingPriceIds) {
        List<ListingPrice> listingPrices = new ArrayList<>();
        if(listingPriceIds != null && listingPriceIds.size() > 0){
            Iterable<ListingPrice> prices = listingPriceDao.findAll(listingPriceIds);
            listingPrices = Lists.newArrayList(prices);
        }
        return listingPrices;
    }
}
