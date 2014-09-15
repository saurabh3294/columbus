package com.proptiger.data.controller;

import static org.testng.AssertJUnit.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.proptiger.app.mvc.AppLocalityController;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.LocalityRatings.LocalityAverageRatingByCategory;
import com.proptiger.data.mvc.LocalityController;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.AbstractTest;

public class AppLocalityControllerTest extends AbstractTest{
    @Autowired
    private AppLocalityController appLocalityController;
    
    @Autowired
    private LocalityController    localityController;
    
    @Test
    public void testGetLocalityDetails() {
        //Order of api call should be maintained because internally caching has being done
        //for locality based on locality id. v2 api returns locality without any manipulation
        // so for v1, locality returned will be as it is returned from solr or db without any
        //manipulation and later all manipulation is done.
        APIResponse v2Response = appLocalityController.getLocalityDetailsV2(50186, null, 1);
        APIResponse v1Response = appLocalityController.getLocalityDetails(50186, null, 1);
        
        if (v1Response.getData() != null && v2Response.getData() != null) {
            Locality locality1 = (Locality) v1Response.getData();
            Locality locality2 = (Locality) v2Response.getData();
            verifyData(locality1, locality2);
        }
        else if (v1Response.getData() == null && v2Response.getData() == null) {
            assertEquals("Both response null", v1Response.getData(), v2Response.getData());
        }
        else {
            assertNotNull(v1Response.getData());
            assertNotNull(v2Response.getData());
        }
    }

    private void verifyData(Locality locality1, Locality locality2) {
        if (locality1.getSafetyScore() != null) {
            assertEquals("Safety Score ", 2 * locality1.getSafetyScore(), locality2.getSafetyScore());
        }
        
        if (locality1.getLivabilityScore() != null) {
           assertEquals("Livability Score ", 2 * locality1.getLivabilityScore(), locality2.getLivabilityScore());
        }
        
        if (locality1.getAverageRating() != null) {
           assertEquals("Average Rating ", 2 * locality1.getAverageRating(), locality2.getAverageRating());
        }
        
        if (locality1.getAvgRatingsByCategory() != null) {
            LocalityAverageRatingByCategory locAvgRatingsByCat1 = locality1.getAvgRatingsByCategory();
            LocalityAverageRatingByCategory locAvgRatingsByCat2 = locality2.getAvgRatingsByCategory();
            if (locAvgRatingsByCat1.getOverallRating() != null) {
                assertEquals("Overall Rating ", 2 * locAvgRatingsByCat1.getOverallRating(), locAvgRatingsByCat2.getOverallRating());
            }
            if (locAvgRatingsByCat1.getLocation() != null) {
                assertEquals("Location ", 2 * locAvgRatingsByCat1.getLocation(), locAvgRatingsByCat2.getLocation());
            }
            if (locAvgRatingsByCat1.getSafety() != null){
                assertEquals("Safety ", 2 * locAvgRatingsByCat1.getSafety(), locAvgRatingsByCat2.getSafety());
            }
            if (locAvgRatingsByCat1.getPubTrans() != null){
                assertEquals("PubTrans ", 2 * locAvgRatingsByCat1.getPubTrans(), locAvgRatingsByCat2.getPubTrans());
            }
            if (locAvgRatingsByCat1.getRestShop() != null){
                assertEquals("RestShop ", 2 * locAvgRatingsByCat1.getRestShop(), locAvgRatingsByCat2.getRestShop());
            }
            if (locAvgRatingsByCat1.getSchools() != null){
                assertEquals("Schools ", 2 * locAvgRatingsByCat1.getSchools(), locAvgRatingsByCat2.getSchools());
            }
            if (locAvgRatingsByCat1.getParks() != null){
                assertEquals("Parks ", 2 * locAvgRatingsByCat1.getParks(), locAvgRatingsByCat2.getParks());
            }
            if (locAvgRatingsByCat1.getTraffic() != null) {
                assertEquals("Traffic ", 2 * locAvgRatingsByCat1.getTraffic(), locAvgRatingsByCat2.getTraffic());
            }
            if (locAvgRatingsByCat1.getHospitals() != null){
                assertEquals("Hospitals ", 2 * locAvgRatingsByCat1.getHospitals(), locAvgRatingsByCat2.getHospitals());
            }
            if (locAvgRatingsByCat1.getCivic() != null){
                assertEquals("Civic ", 2 * locAvgRatingsByCat1.getCivic(), locAvgRatingsByCat2.getCivic());
            }
        }
    }
}
