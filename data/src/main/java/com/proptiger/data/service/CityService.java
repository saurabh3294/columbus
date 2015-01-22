package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.core.exception.BadRequestException;
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
    public List<City> getCityList(Selector selector, boolean useFieldSelector) {
        List<City> cities = cityDao.getCities(selector);
        Set<String> fields = (selector == null || selector.getFields() == null) ? new HashSet<String>() : selector
                .getFields();

        if (!useFieldSelector || fields.contains("amenties")) {
            updateAirportInfo(cities);
        }
        return cities;
    }

    /**
     * This method will return the city object based on city id.
     * 
     * @param cityId
     * @return City.
     */
    public City getCityInfo(int cityId, Selector selector, boolean useFieldSelector) {
        City city = cityDao.getCity(cityId);
        if (city == null) {
            return null;
        }
        Set<String> fields = selector.getFields() == null ? new HashSet<String>() : selector.getFields();

        /*
         * setting the airport data on selector or fieldSelector false.
         */
        if (useFieldSelector == false || fields.contains("amenities")) {
            updateAirportInfo(city);
        }
        /*
         * Setting project and project Status count.
         */
        if (useFieldSelector == false || fields.contains("projectCount") || fields.contains("projectStatusCount")) {
            updateProjectCountAndStatusCount(city);
        }
        /*
         * Setting the avgBHKPricePerUnitArea only when demanded or
         * fieldSelector false.
         */
        if (useFieldSelector == false || fields.contains("avgBHKPricePerUnitArea")) {
            city.setAvgBHKPricePerUnitArea(localityService.getAvgPricePerUnitAreaBHKWise(
                    "cityId",
                    cityId,
                    city.getDominantUnitType()));
        }
        /*
         * Setting the image only when asked in selector or fieldSelector false.
         */
        if (useFieldSelector == false || fields.contains("images")) {
            city.setImages(imageService.getImages(DomainObject.city, null, cityId));
        }
        /*
         * setting amenity Type count.
         */
        if (useFieldSelector == false || fields.contains("amenityTypeCount")) {
            updateAmenitiesAndAmenityTypeCount(city);
        }
        return city;
    }
    
    @Cacheable(Constants.CacheName.CACHE)
    public Map<String, Integer> getCityNameToIdMap(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        List<City> cityList = cityDao.findAll();
        for(City city : cityList){
            map.put(city.getLabel(), city.getId());
        }
        return map;
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

    @Cacheable(Constants.CacheName.CACHE)
    public City getCityByName(String cityName) {
        String js = "{\"filters\":{\"and\":[{\"equal\":{\"label\":" + cityName + "}}]}}";
        Gson gson = new Gson();
        Selector selector = gson.fromJson(js, Selector.class);
        List<City> cities = getCityList(selector, false);
        if (cities == null || cities.isEmpty()) {
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
        if (cities == null || cities.isEmpty()) {
            return;
        }
        List<Integer> cityIds = new ArrayList<Integer>();
        for (City city : cities) {
            cityIds.add(city.getId());
        }
        String cityIdsStr = StringUtils.join(cityIds.toArray(), ",");
        
        Paging paging = new Paging(0, 500);
        String jsonSelector = "{\"filters\":{\"and\":[{\"equal\":{\"cityId\":[" + cityIdsStr
                + "],\"amenityType\":\"" + Constants.AmenityName.AIRPORT +"\""
                + "}}]}, \"paging\":{\"start\":"
                + paging.getStart()
                + ",\"rows\":"
                + paging.getRows()
                + "}}";
        
        Selector selector = new Gson().fromJson(jsonSelector, Selector.class);
        List<LandMark> amenities = localityAmenityService.getLandMarksOnSelector(selector);
        
        Map<Integer, List<LandMark>> cityAmenitiesMap = new HashMap<Integer, List<LandMark>>();
        for(LandMark amenity : amenities) {
            List<LandMark> amenitylist = cityAmenitiesMap.get(new Integer(amenity.getCityId()));
            if (amenitylist == null) {
                amenitylist = new ArrayList<LandMark>();
                cityAmenitiesMap.put(new Integer(amenity.getCityId()), amenitylist);
            }
            amenitylist.add(amenity);
        }
        
        for(City city: cities) {
            city.setAmenities(cityAmenitiesMap.get(city.getId()));
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
    @Transactional
    public City updateCity(City city) {
        if(city.getDescription() != null && !city.getDescription().isEmpty()){
            City cityActual=cityDao.findOne(city.getId());
            cityActual.setDescription(city.getDescription());
            cityActual = cityDao.save(cityActual);
            return cityActual;
        }
        throw new BadRequestException("Invalid city description");
    }
}
