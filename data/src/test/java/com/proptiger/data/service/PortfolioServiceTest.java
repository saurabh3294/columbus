package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.core.enums.ListingStatus;
import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.service.AbstractTest;
import com.proptiger.data.service.user.portfolio.PortfolioService;

public class PortfolioServiceTest extends AbstractTest{
    
    @Autowired
    private PortfolioService portfolioService;
    
    @Test
    public void testLatestEntryDateOfProjectPriceTrend() {
        
        List<Integer> users = new ArrayList<Integer>();
        users.add(111694);
        
        List<ListingStatus> listingStatus = new ArrayList<ListingStatus>();
        listingStatus.add(ListingStatus.ACTIVE);    // Active Listings hold Price values
        
        for (Integer userId : users) {
        List<PortfolioListing> portfolio = portfolioService.getAllPortfolioListings(userId , listingStatus);
        
        for (PortfolioListing listing : portfolio) {
            if(listing.getTotalPrice() == listing.getCurrentPrice()) {
                Assert.assertEquals(false, true);
            }
        }
        }
    }
    
    

}
