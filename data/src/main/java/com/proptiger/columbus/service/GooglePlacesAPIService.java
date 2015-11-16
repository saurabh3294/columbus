package com.proptiger.columbus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.columbus.model.GooglePlace;
import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.repo.GooglePlacesAPIDao;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.util.Constants;

@Service
public class GooglePlacesAPIService {

    @Autowired
    GooglePlacesAPIDao                        googlePlacesAPIDao;

    @Value("${google.places.api.place.enabled}")
    private Boolean                           isGooglePlaceAPIEnabled;

    private static Logger                     logger                   = LoggerFactory
                                                                               .getLogger(GooglePlacesAPIService.class);

    public static final String                TypeaheadTypeGooglePlace = "GP";

    private final String                      googlePlaceIndicatorText = "Projects Near ";

    private final String                      googlePlaceCountryString = "India";

    private static final Map<String, Boolean> stateMap                 = new HashMap<>();

    private final String                      googlePlaceNameSeparator = ", ";

    static {
        stateMap.put("andaman and nicobar islands", true);
        stateMap.put("andhra pradesh", true);
        stateMap.put("arunachal pradesh", true);
        stateMap.put("assam", true);
        stateMap.put("bihar", true);
        stateMap.put("chhattisgarh", true);
        stateMap.put("goa", true);
        stateMap.put("gujarat", true);
        stateMap.put("haryana", true);
        stateMap.put("himachal pradesh", true);
        stateMap.put("jammu and kashmir", true);
        stateMap.put("jharkhand", true);
        stateMap.put("karnataka", true);
        stateMap.put("kerala", true);
        stateMap.put("madhya pradesh", true);
        stateMap.put("maharashtra", true);
        stateMap.put("manipur", true);
        stateMap.put("meghalaya", true);
        stateMap.put("mizoram", true);
        stateMap.put("nagaland", true);
        stateMap.put("orissa", true);
        stateMap.put("punjab", true);
        stateMap.put("rajasthan", true);
        stateMap.put("sikkim", true);
        stateMap.put("tamil nadu", true);
        stateMap.put("telangana", true);
        stateMap.put("tripura", true);
        stateMap.put("uttar pradesh", true);
        stateMap.put("uttarakhand", true);
        stateMap.put("west bengal", true);
        stateMap.put("delhi", true);
    }

    @Cacheable(value = Constants.CacheName.COLUMBUS_GOOGLE, unless = "#result.isEmpty()")
    public List<Typeahead> getPlacePredictions(String query, int rows, double[] geoCenter, int radius) {

        List<Typeahead> results = new ArrayList<Typeahead>();

        if (!isGooglePlaceAPIEnabled) {
            logger.error("Google Places API is not enabled.");
            return results;
        }

        long timeStart = System.currentTimeMillis();
        List<GooglePlace> googlePlaceList = googlePlacesAPIDao.getMatchingPlaces(query, rows, geoCenter, radius, true);
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
        typeahead.setId(String.format(
                TypeaheadConstants.TYPEAHEAD_ID_PATTERN,
                TypeaheadTypeGooglePlace,
                googlePlace.getPlaceId()));
        typeahead.setType(TypeaheadTypeGooglePlace);
        typeahead.setGooglePlaceId(googlePlace.getPlaceId());
        typeahead.setLabel(googlePlace.getPlaceName());
        typeahead.setDisplayText(getDisplayTextFromGooglePlaceName(googlePlace));
        typeahead.setLatitude(googlePlace.getLatitude());
        typeahead.setLongitude(googlePlace.getLongitude());
        typeahead.setGooglePlace(true);
        return typeahead;
    }

    private String getDisplayTextFromGooglePlaceName(GooglePlace googlePlace) {
        String[] placeNameParts = googlePlace.getDescription().split(googlePlaceNameSeparator);

        int size = placeNameParts.length;

        if (size > 1 && placeNameParts[size - 1].equals(googlePlaceCountryString)) {
            size--;
            if (size > 1 && stateMap.containsKey(placeNameParts[size - 1].toLowerCase())) {
                size--;
            }
        }
        return googlePlaceIndicatorText + StringUtils.join(placeNameParts, googlePlaceNameSeparator, 0, size);
    }
}