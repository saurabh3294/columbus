package com.proptiger.columbus.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.columbus.model.GooglePlace;
import com.proptiger.columbus.model.Typeahead;
import com.proptiger.columbus.repo.GooglePlacesAPIDao;

@Service
public class GooglePlacesAPIService {

    @Autowired
    GooglePlacesAPIDao         googlePlacesAPIDao;

    public static final String TypeaheadIdPrefix = "TYPEAHEAD-GP-";

    public List<Typeahead> getPlacePredictions(String query, int rows) {
        List<Typeahead> results = new ArrayList<Typeahead>();
        List<GooglePlace> googlePlaceList = googlePlacesAPIDao.getMatchingPlaces(query, rows);
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
    public GooglePlace getPlaceDetails(String placeId) {
        GooglePlace googlePlace = googlePlacesAPIDao.getPlaceDetails(placeId);
        return googlePlace;
    }

    private Typeahead getTypeaheadFromGooglePlace(GooglePlace googlePlace) {
        if (googlePlace == null) {
            return null;
        }

        Typeahead typeahead = new Typeahead();
        typeahead.setId(TypeaheadIdPrefix + googlePlace.getPlaceId());
        typeahead.setGooglePlaceId(googlePlace.getPlaceId());
        typeahead.setLabel(googlePlace.getPlaceName());
        typeahead.setDisplayText(googlePlace.getDescription());
        typeahead.setLatitude(googlePlace.getLatitude());
        typeahead.setLongitude(googlePlace.getLongitude());
        typeahead.setIsGooglePlace(true);
        return typeahead;
    }

}
