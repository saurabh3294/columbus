/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.amazonaws.services.ec2.model.DomainType;
import com.google.gson.Gson;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.LandMark;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Suburb;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.repo.LandMarkDao;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.UtilityClass;

/**
 * 
 * @author mukand
 */
@Service
public class LandMarkService {
    @Autowired
    private LandMarkDao localityAmenityDao;
    @Autowired
    private LocalityDao        localityDao;
    
    @Autowired
    private SuburbService suburbService;
    
    @Autowired
    private LocalityService localityService;

    /**
     * Get the locality amenities based on locality id and amenity name. If
     * amenity name not provided the it will return all amenities present in
     * locality
     * 
     * @param localityId
     * @param amenityName
     * @return
     */
    @Cacheable(value = Constants.CacheName.LOCALITY_AMENITY)
    public List<LandMark> getLocalityAmenities(int localityId, String amenityName) {
        Locality locality = localityService.getLocality(localityId);
        Paging paging = new Paging(0, 999);
        
        if (amenityName == null || amenityName.isEmpty()) {
            return getLandMarksForLocality(locality, null, paging);//localityAmenityDao.getAmenitiesByLocalityId(localityId);
        }
        else {
            return getLandMarksForLocality(locality, amenityName, paging);//localityAmenityDao.getAmenitiesByLocalityIdAndAmenity(localityId, amenityName);
        }
    }

    /**
     * Get amenities for city, if no amenity name provided then fetch all
     * amenities
     * 
     * @param cityId
     * @param amenityName
     * @return
     */
    public List<LandMark> getCityAmenities(Integer cityId, String amenityName) {
        List<LandMark> amenities = null;
        if (amenityName == null || amenityName.isEmpty()) {
            amenities = getCityAmenities(cityId, null);//localityAmenityDao.getAmenitiesByCityId(cityId);
        }
        else {
            amenities = getCityAmenities(cityId, amenityName);//localityAmenityDao.getAmenitiesByCityIdAndAmenityName(cityId, amenityName);
        }
        return amenities;
    }

    /**
     * This method will take the cityId or list of localities and select the
     * locality with highest priority. Then return the amenites of that
     * locality.
     * 
     * @param cityId
     * @param localityIds
     * @return List<LocalityAmenity>
     */
    public List<LandMark> getAmenitiesByHighPriorityLocalityId(Integer cityId, List<Integer> localityIds) {
        Paging paging = new Paging(0, 1);
        List<Locality> localityInfo = null;
        if (localityIds != null)
            localityInfo = localityDao.findByLocationOrderByPriority(localityIds, "locality", paging, SortOrder.ASC);
        else
            localityInfo = localityDao.findByLocationOrderByPriority(cityId, "city", paging, SortOrder.ASC);

        List<LandMark> data = getLandMarksForLocality(localityInfo.get(0), null, paging);//localityAmenityDao.getAmenitiesByLocalityId(localityId);

        return data;
    }

    /**
     * @param suburbId
     * @return
     */
    public List<LandMark> getSuburbAmenities(int suburbId) {
        List<Locality> localities = localityService.getLocalitiesOnCityOrSuburb(DomainObject.suburb, suburbId, new Paging(0, 1));
        if(localities == null || localities.isEmpty())
            return new ArrayList<LandMark>();
        
        return getLandMarksForLocality(localities.get(0), null, new Paging(0, 999));
        /*List<LandMarks> output = null;
        output = localityAmenityDao.getAmenitiesBySuburbId(suburbId);
        return output;*/
    }
    
    /**
     * 
     * @param locality
     * @param amenityType
     * @param paging
     * @return
     */
    public List<LandMark> getLandMarksForLocality(Locality locality, String amenityType, Paging paging){
        if(locality == null || locality.getLatitude() == null || locality.getLongitude() == null)
            return new ArrayList<LandMark>();
        double radius = UtilityClass.min(locality.getMaxRadius(), 3.0);
        return getLandMarkByGeoDistance(locality.getLatitude(), locality.getLongitude(), radius, paging, amenityType, null);
    }
    
    /**
     * 
     * @param project
     * @param amenityType
     * @param paging
     * @return
     */
    public List<LandMark> getLandMarksForProject(Project project, String amenityType, Paging paging){
        if(project == null || project.getLatitude() == null || project.getLongitude() == null)
            return new ArrayList<LandMark>();
        
        return getLandMarkByGeoDistance(project.getLatitude(), project.getLongitude(), 3, paging, amenityType, null);
    }
    
    /**
     * 
     * @param latitude
     * @param longitude
     * @param distance
     * @param paging
     * @param amenityType
     * @param cityId
     * @return
     */
    public List<LandMark> getLandMarkByGeoDistance(
            double latitude,
            double longitude,
            double distance,
            Paging paging,
            String amenityType,
            Integer cityId) {

        String equalFilters = ",\"hasGeo\":1";
        if (cityId != null) {
            equalFilters += ",\"cityId\":" + cityId;
        }
        if (amenityType != null) {
            equalFilters += ",\"amenityType\":" + amenityType;
        }
        if (equalFilters.length() > 1) {
            equalFilters = ",{\"equal\":{" + equalFilters.substring(1) + "}}";
        }

        String jsonSelector = "{\"filters\":{\"and\":[{\"geoDistance\":{\"geo\":{\"lat\":" + latitude
                + ",\"lon\":"
                + longitude
                + ",\"distance\":"
                + distance
                + "}}}"
                + equalFilters
                + "]}, \"paging\":{\"start\":"
                + paging.getStart()
                + ",\"rows\":"
                + paging.getRows()
                + "}}";
        System.out.println(jsonSelector);
        Selector selector = new Gson().fromJson(jsonSelector, Selector.class);

        return localityAmenityDao.getLocalityAmenitiesOnSelector(selector);
    }

    /**
     * 
     * @param cityId
     * @param amenityType
     * @param paging
     * @return
     */
    public List<LandMark> getLandMarksByCity(Integer cityId, String amenityType, Paging paging) {
        String amenityTypeStr = "";
        if (amenityType != null) {
            amenityTypeStr = ",\"amenityType\":" + amenityType;
        }
        String jsonSelector = "{\"filters\":{\"and\":[{\"cityId\":" + cityId
                + amenityTypeStr
                + "}]}, \"paging\":{\"start\":"
                + paging.getStart()
                + ",\"rows\":"
                + paging.getRows()
                + "}}";
        System.out.println(jsonSelector);
        Selector selector = new Gson().fromJson(jsonSelector, Selector.class);

        return localityAmenityDao.getLocalityAmenitiesOnSelector(selector);
    }
}
