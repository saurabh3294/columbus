package com.proptiger.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.external.GooglePlace;
import com.proptiger.core.util.Constants;
import com.proptiger.data.repo.GooglePlacesAPIDao;

@Service
public class GooglePlacesAPIService {

    @Autowired
    GooglePlacesAPIDao         googlePlacesAPIDao;

    @Value("${google.places.api.place.enabled}")
    private Boolean            isGooglePlaceAPIEnabled;

    private static Logger      logger            = LoggerFactory.getLogger(GooglePlacesAPIService.class);

    public static final String TypeaheadIdPrefix = "TYPEAHEAD-GP-";

    @Cacheable(value = Constants.CacheName.CACHE)
    public GooglePlace getPlaceDetails(String placeId) {
        if (!isGooglePlaceAPIEnabled) {
            logger.error("Google Places API is not enabled.");
            return null;
        }

        long timeStart = System.currentTimeMillis();
        GooglePlace googlePlace = googlePlacesAPIDao.getPlaceDetails(placeId);
        long timeTaken = System.currentTimeMillis() - timeStart;
        logger.info("Google Place API call : Time Taken = " + timeTaken + " ms");
        return googlePlace;
    }

}
