package com.proptiger.columbus.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.columbus.model.GooglePlace;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.Constants;
import com.proptiger.columbus.repo.GooglePlacesAPIDao;

@Service
public class GooglePlacesAPIService {

    @Autowired
    GooglePlacesAPIDao         googlePlacesAPIDao;

    @Value("${google.places.api.place.enabled}")
    private Boolean            isGooglePlaceAPIEnabled;

    private static Logger      logger                   = LoggerFactory.getLogger(GooglePlacesAPIService.class);

    public static final String TypeaheadIdPrefix        = "TYPEAHEAD-GP-";

    public static final String TypeaheadTypeGooglePlace = "GP";

    @Cacheable(value = Constants.CacheName.COLUMBUS_GOOGLE, unless = "#result.isEmpty()")
    public List<Typeahead> getPlacePredictions(String query, int rows) {

        List<Typeahead> results = new ArrayList<Typeahead>();

        if (!isGooglePlaceAPIEnabled) {
            logger.error("Google Places API is not enabled.");
            return results;
        }

        long timeStart = System.currentTimeMillis();
        List<GooglePlace> googlePlaceList = googlePlacesAPIDao.getMatchingPlaces(query, rows);
        long timeTaken = System.currentTimeMillis() - timeStart;
        logger.info("Google Place Predictions API call (" + query + ") : Time Taken = " + timeTaken + " ms");

        for (GooglePlace gp : googlePlaceList) {
            results.add(getTypeaheadFromGooglePlace(gp));
        }
        return results;
    }

    /**
     * @param placeId
     *            google place_id for the place
     * @return a typeahead object populated with required values.
     */
    @Cacheable(value = Constants.CacheName.COLUMBUS_GOOGLE, unless = "#result == null")
    public GooglePlace getPlaceDetails(String placeId) {
        if (!isGooglePlaceAPIEnabled) {
            logger.error("Google Places API is not enabled.");
            return null;
        }
        GooglePlace googlePlace = googlePlacesAPIDao.getPlaceDetails(placeId);
        return googlePlace;
    }

    private Typeahead getTypeaheadFromGooglePlace(GooglePlace googlePlace) {
        if (googlePlace == null) {
            return null;
        }

        Typeahead typeahead = new Typeahead();
        typeahead.setId(TypeaheadIdPrefix + googlePlace.getPlaceId());
        typeahead.setType(TypeaheadTypeGooglePlace);
        typeahead.setGooglePlaceId(googlePlace.getPlaceId());
        typeahead.setLabel(googlePlace.getPlaceName());
        typeahead.setDisplayText(googlePlace.getDescription());
        typeahead.setLatitude(googlePlace.getLatitude());
        typeahead.setLongitude(googlePlace.getLongitude());
        typeahead.setGooglePlace(true);
        return typeahead;
    }

}
