package com.proptiger.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.core.exception.ResourceNotAvailableException;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.model.cms.LandMark;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.proptiger.Image;
import com.proptiger.core.pojo.Paging;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.repo.CityDao;

/**
 * Service class to get city data
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class CityService {
    @Autowired
    private CityDao         cityDao;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private ProjectService  projectService;

    @Autowired
    private ImageService    imageService;

    @Autowired
    private LandMarkService localityAmenityService;
    
    @Autowired
    private ImageEnricher   imageEnricher;

    /**
     * Get list of city details
     * 
     * @param selector
     * @return List<City>
     */
    @Cacheable(Constants.CacheName.CACHE)
    public List<City> getCityList(Selector selector) {
        List<City> cities = cityDao.getCities(selector);
        updateAirportInfo(cities);
        return cities;
    }

    /**
     * This method will return the city object based on city id.
     * 
     * @param cityId
     * @return City.
     */
    public City getCityInfo(int cityId) {
        City city = cityDao.getCity(cityId);
        if (city == null) {
            return null;
        }
        updateAirportInfo(city);
        updateProjectCountAndStatusCount(city);
        city.setAvgBHKPricePerUnitArea(localityService.getAvgPricePerUnitAreaBHKWise(
                "cityId",
                cityId,
                city.getDominantUnitType()));
        city.setImages(imageService.getImages(DomainObject.city, null, cityId));
        updateAmenitiesAndAmenityTypeCount(city);
        return city;
    }

    private void updateAmenitiesAndAmenityTypeCount(City city) {
        if (city == null) {
            return;
        }

        Selector selector = new Gson().fromJson("{\"filters\":{\"and\":[{\"equal\":{\"cityId\":" + city.getId()
                + "}}]}, \"paging\":{\"start\":0,\"rows\":0}}", Selector.class);
        // Currently only Amenity Type count is required
        /*
         * List<LandMark> amenities =
         * localityAmenityService.getAmenityListByGroupSelector(selector); if
         * (amenities == null || amenities.isEmpty()) { return; }
         * city.setCompleteAmenities(amenities);
         */
        city.setAmenityTypeCount(localityAmenityService.getAmenityTypeCount(selector));
    }

    public City getCity(Integer cityId) {
        City city = cityDao.getCity(cityId);
        if (city == null) {
            throw new ResourceNotAvailableException(ResourceType.CITY, ResourceTypeAction.GET);
        }
        return city;
    }

    public City getCityByName(String cityName) {
        String js = "{\"filters\":{\"and\":[{\"equal\":{\"label\":" + cityName + "}}]}}";
        Gson gson = new Gson();
        Selector selector = gson.fromJson(js, Selector.class);
        List<City> cities = getCityList(selector);
        if(cities == null || cities.isEmpty()){
            return null;
        }

        return cities.get(0);
    }

    /**
     * Updating total projects in city
     * 
     * @param city
     */
    private void updateProjectCountAndStatusCount(City city) {
        Selector selector = new Gson().fromJson("{\"filters\":{\"and\":[{\"equal\":{\"cityId\":" + city.getId()
                + "}}]}, \"paging\":{\"start\":0,\"rows\":0}}", Selector.class);
        PaginatedResponse<List<Project>> response = projectService.getProjects(selector);
        if (response != null) {
            city.setProjectCount(response.getTotalCount());
        }
        Map<String, Long> projectStatusCount = projectService.getProjectStatusCount(selector);
        city.setProjectStatusCount(projectStatusCount);
    }

    /**
     * Updating airport informtaion in cities list
     * 
     * @param cities
     */
    private void updateAirportInfo(List<City> cities) {
        if (cities != null) {
            for (City city : cities) {
                updateAirportInfo(city);
            }
        }
    }

    /**
     * Updating airport
     * 
     * @param city
     */
    private void updateAirportInfo(City city) {
        if (city != null) {
            List<LandMark> amenities = localityAmenityService.getLandMarksByCity(
                    city.getId(),
                    Constants.AmenityName.AIRPORT,
                    new Paging(0, 10));
            city.setAmenities(amenities);
        }
    }

    public PaginatedResponse<List<Image>> getCityLandMarkImages(int cityId) {
        City city = cityDao.getCity(cityId);
        if (city == null) {
            return new PaginatedResponse<List<Image>>();
        }
        List<LandMark> amenity = localityAmenityService.getLandMarksByCity(cityId, null, new Paging(0, 2000));
        return imageEnricher.getCityAmenityImages(amenity);
    }
}
