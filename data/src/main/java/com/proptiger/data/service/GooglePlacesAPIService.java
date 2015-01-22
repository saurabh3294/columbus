package com.proptiger.data.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.external.GooglePlace;
import com.proptiger.core.util.Constants;
import com.proptiger.data.repo.GooglePlacesAPIDao;

@Service
public class GooglePlacesAPIService {

    @Autowired
    private GooglePlacesAPIDao googlePlacesAPIDao;

    @Autowired
    private CityService        cityService;

    private static Logger      logger            = LoggerFactory.getLogger(GooglePlacesAPIService.class);

    public static final String TypeaheadIdPrefix = "TYPEAHEAD-GP-";

    @Cacheable(value = Constants.CacheName.CACHE)
    public GooglePlace getPlaceDetails(String placeId) {

        long timeStart = System.currentTimeMillis();
        GooglePlace googlePlace = googlePlacesAPIDao.getPlaceDetails(placeId);
        long timeTaken = System.currentTimeMillis() - timeStart;
        logger.info("Google Place API call : Time Taken = " + timeTaken + " ms");

        populateCityInfo(googlePlace);

        return googlePlace;
    }

    private void populateCityInfo(GooglePlace gp) {
        String cityName = gp.getCityName();
        Map<String, Integer> mapCityNameToId = cityService.getCityNameToIdMap();
        if (mapCityNameToId.containsKey(cityName)) {
            gp.setCityid(mapCityNameToId.get(cityName));
        }
        else {
            logger.error("Could not find cityname in database = " + cityName);
        }
    }

}
