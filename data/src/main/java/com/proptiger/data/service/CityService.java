package com.proptiger.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proptiger.data.model.City;
import com.proptiger.data.model.LocalityAmenity;
import com.proptiger.data.model.Project;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.CityDao;
import com.proptiger.data.service.pojo.PaginatedResponse;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * Service class to get city data
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class CityService {
    @Autowired
    private CityDao                cityDao;

    @Autowired
    private LocalityService        localityService;
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private LocalityAmenityService localityAmenityService;

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
        city.setAvgBHKPrice(localityService.getAvgPricePerUnitAreaBHKWise("cityId", cityId, city.getDominantUnitType()));
        return city;
    }

    public City getCity(Integer cityId){
        City city = cityDao.getCity(cityId);
        if (city == null) {
           throw new ResourceNotAvailableException(ResourceType.CITY, ResourceTypeAction.GET);
        }
        return city;
    }
    
    /**
     * Updating total projects in city
     * @param city
     */
    private void updateProjectCountAndStatusCount(City city) {
        Selector selector = new Gson().fromJson(
                "{\"filters\":{\"and\":[{\"equal\":{\"cityId\":" + city.getId() + "}}]}, \"paging\":{\"start\":0,\"rows\":0}}",
                Selector.class);
        PaginatedResponse<List<Project>> response = projectService.getProjects(selector);
        if(response != null){
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
            List<LocalityAmenity> amenities = localityAmenityService.getCityAmenities(
                    city.getId(),
                    Constants.AmenityName.AIRPORT);
            city.setAmenities(amenities);
        }
    }
}
