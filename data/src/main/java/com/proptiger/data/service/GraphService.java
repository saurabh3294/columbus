/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.NearLocalities;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.repo.CMSDao;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.NearLocalitiesDao;
import com.proptiger.data.repo.PropertyDao;

/**
 * 
 * @author mukand
 */
@Service
public class GraphService {
    @Autowired
    private PropertyDao       propertyDao;

    @Resource
    private LocalityDao       localityDao;

    @Autowired
    private CMSDao            cmsDao;

    @Autowired
    private NearLocalitiesDao nearLocalitiesDao;

    @Autowired
    private LocalityService   localityService;

    /**
     * This method will return the number of projects for a project status group
     * and bedrooms wise for a locality or suburb or city.
     * 
     * @param params
     *            of type Map<String, String> . The map contain three
     *            parameters: 1: location_type: city or suburb or locality 2:
     *            location_id: id of the corresponding location type 3:
     *            optional: bedroom upper limit(default 3). meaning the bedrooms
     *            above this will be grouped.
     * @return Map<String, Map<Integer, Integer>> String is the project status.
     *         next integer is the bedroom and next integer is the project
     *         count.
     */
    public Map<String, Map<Integer, Integer>> getProjectDistrubtionOnStatus(Map<String, String> params) {
        int bedroom_limit = Integer.parseInt(params.get("bedroom_upper_limit"));

        // TODO to move configuration file
        Map<String, String> projectStatusMapping = new HashMap<String, String>();
        projectStatusMapping.put("under construction", "under construction");
        projectStatusMapping.put("pre launch", "launch and upcoming");
        projectStatusMapping.put("not launched", "launch and upcoming");
        projectStatusMapping.put("launch", "launch and upcoming");
        projectStatusMapping.put("ready for possession", "ready for possession");
        projectStatusMapping.put("occupied", "ready for possession");

        Map<String, Map<Integer, Integer>> response = new HashMap<String, Map<Integer, Integer>>();
        Map<Integer, Integer> data;
        Map<String, Map<String, Integer>> projectBed = propertyDao.getProjectDistrubtionOnStatusOnBed(params);
        Map<String, Map<String, Integer>> projectMaxBed = propertyDao.getProjectDistrubtionOnStatusOnMaxBed(params);

        // adding the projects with bedroom limit from 1 to Bedroom_limit
        Map<String, Integer> it = projectBed.get("PROJECT_STATUS_BEDROOM");
        Iterator<String> keys = it.keySet().iterator();
        String splits[], key, hashKey;
        Integer value = null, bed = null;

        while (keys.hasNext()) {
            hashKey = keys.next();
            // 0 => project status, 1=> number of beds
            splits = hashKey.split(",");
            splits[0] = splits[0].toLowerCase();

            bed = Integer.parseInt(splits[1]);
            if (bed > bedroom_limit)
                bed = bedroom_limit + 1;

            key = projectStatusMapping.get(splits[0]);
            if (key != null) {
                data = response.get(key);
                if (data != null) {
                    value = data.get(bed);
                    if (value != null)
                        value += it.get(hashKey);
                    else
                        value = it.get(hashKey);
                }
                else {
                    value = it.get(hashKey);
                    data = new HashMap<Integer, Integer>();

                }
                data.put(bed, value);
                response.put(key, data);
            }
        }

        // adding projects where bedroom greater than bedrooms.
        it = projectMaxBed.get("PROJECT_STATUS");
        keys = it.keySet().iterator();
        bed = bedroom_limit + 1;
        while (keys.hasNext()) {
            hashKey = keys.next();
            key = projectStatusMapping.get(hashKey);
            if (key != null) {
                data = response.get(key);
                value = data.get(bed);
                data.put(bed, value + it.get(hashKey));
                response.put(key, data);
            }
        }

        return response;
    }

    /**
     * This method will return the enquiry distribution of the localities in a
     * city or suburb. The enquiry distribution will pick the localities with
     * maximum enquiries. In case of location_type locality, the top enquiry
     * localities will be picked including the current locality only if the
     * enquiry percentage is > 1.
     * 
     * @param params
     *            of type Map<String, String> . The map contain three
     *            parameters: 1: location_type: city or suburb or locality 2:
     *            location_id: id of the corresponding location type 3:
     *            number_of_localities: The number of localities to return in
     *            response for enquiry distribution. 4: last_number_of_months:
     *            number of months of data to be returned.
     * @return Map<String, Double> String is the locality name and Double is the
     *         percentage of enquiry distribution.
     */
    public Map<String, Double> getEnquiryDistributionOnLocality(Map<String, Object> params) {

        Double locationId = (Double) params.get("location_id");
        String locationType = (String) params.get("location_type");
        Double numberOfLocalities = (Double) params.get("number_of_localities");
        Double lastNumberOfMonths = (Double) params.get("last_number_of_months");
        Double cityId = (Double) params.get("city_id");

        Date date = new DateTime().minusMonths(lastNumberOfMonths.intValue()).toDate();
        Long locationTypeMap, parentLocationId = locationId.longValue();

        locationType = locationType.toLowerCase();
        switch (locationType) {
            case "locality":
                locationTypeMap = 1L;
                parentLocationId = cityId.longValue();
                break;
            case "suburb":
                locationTypeMap = 2L;
                break;
            case "city":
                locationTypeMap = 3L;
                break;
            default:
                locationTypeMap = 3L;
        }

        Long totalEnquiry = localityDao.findTotalEnquiryCountOnCityOrSubOrLoc(
                date,
                locationTypeMap,
                parentLocationId.intValue());
        List<Object[]> localitiesData = localityDao.findEnquiryCountOnCityOrSubOrLoc(
                date,
                locationTypeMap,
                parentLocationId.intValue());
        Object[] currentLocalityData = null;
        if (locationTypeMap == 1L) {
            currentLocalityData = localityDao.findEnquiryCountOnLoc(date, locationId.intValue());
        }

        Map<String, Double> response = new LinkedHashMap<String, Double>();
        Object[] data;
        double sum = 0;
        double percentage;
        boolean flag = true;
        for (int i = 0; i < localitiesData.size() && i < numberOfLocalities; i++) {
            data = (Object[]) localitiesData.get(i);
            if (flag && currentLocalityData != null
                    && (Long) currentLocalityData[2] > (Long) data[2]
                    && !response.containsKey(currentLocalityData[1])) {
                flag = false;
                percentage = (100 * (Long) currentLocalityData[2]) / totalEnquiry.doubleValue();
                if (percentage > 1) {
                    sum += percentage;
                    response.put((String) currentLocalityData[1], percentage);
                }

            }
            if (response.size() < numberOfLocalities) {
                percentage = (100 * (Long) data[2]) / totalEnquiry.doubleValue();
                response.put((String) data[1], percentage);
                if (currentLocalityData == null || currentLocalityData[1] != data[1]
                        || !response.containsKey(currentLocalityData[1]))
                    sum += percentage;
            }
        }

        response.put("Other Localities", 100 - sum);

        return response;
    }

    /**
     * This method will get the prices of all projects for a locality or suburb
     * or city. Then it will group the projects based on their price. The price
     * range will be based on the custom_price_range params.
     * 
     * @param params
     *            of type Map<String, String> . The map contain three
     *            parameters: 1: location_type: city or suburb or locality 2:
     *            location_id: id of the corresponding location type 3:
     *            custom_price_range(Map<String, Double>): The range of price by
     *            which the projects will be grouped. Here String will the range
     *            of price. Double will be number of times this range will be
     *            repeated.
     * 
     * @return Map<String, Integer> Here String will contain the price range and
     *         the Integer will contain the project count in this range.
     */
    public Map<String, Integer> getProjectDistributionOnPrice(Map<String, Object> params) {

        Map<String, Integer> solrData = propertyDao.getProjectDistributionOnPrice(params).get("PRICE_PER_UNIT_AREA");
        if (solrData.isEmpty())
            return null;

        Map<String, Double> customPriceRange = (Map<String, Double>) params.get("custom_price_range");
        Map<String, Integer> response = new LinkedHashMap<String, Integer>();

        Iterator<String> priceIt = customPriceRange.keySet().iterator();
        Iterator<String> solrDataIt = solrData.keySet().iterator();

        String priceRangeKey = priceIt.next();
        int range = Integer.parseInt(priceRangeKey);
        int count = customPriceRange.get(priceRangeKey).intValue();
        int currentRange = range;
        int oldRange = 0;
        String rangeKey = "0-" + range;
        response.put(rangeKey, 0);
        int maxPrice = 0;
        String key = solrDataIt.next();
        int currentPrice;
        int value;
        Integer oldValue;
        boolean customRangeFlag = true;
        boolean solrDataFlag = true;

        while (solrDataFlag) {
            currentPrice = new Double(key).intValue();
            maxPrice = maxPrice < currentPrice ? currentPrice : maxPrice;
            value = solrData.get(key);
            if (count == 0 && priceIt.hasNext()) {
                priceRangeKey = priceIt.next();
                range = Integer.parseInt(priceRangeKey);
                count = customPriceRange.get(priceRangeKey).intValue();
                oldRange = currentRange;
                currentRange += range;
                response.put(oldRange + "-" + currentRange, 0);

            }
            if (currentPrice <= currentRange || (!priceIt.hasNext() && count == 0)) {
                if (!priceIt.hasNext() && count == 0 && customRangeFlag) {
                    oldRange += range;
                    customRangeFlag = false;
                }

                oldValue = response.get(oldRange + "-" + currentRange);
                oldValue = (oldValue == null) ? 0 : oldValue;
                response.put(oldRange + "-" + currentRange, oldValue + value);

                try {
                    key = solrDataIt.next();
                }
                catch (NoSuchElementException e) {
                    solrDataFlag = false;
                }
            }
            else {
                count--;
                if (count > 0) {
                    oldRange = currentRange;
                    currentRange += range;
                    response.put(oldRange + "-" + currentRange, 0);
                }
            }
        }
        if (currentRange == oldRange) {
            key = currentRange + "-" + currentRange;
            count = response.get(key);
            response.remove(key);
            response.put(currentRange + "+", count);
        }

        return response;
    }

    public Object getPriceTrendsGraphs(Map<String, Object> paramObject) {
        String locationType = (String) paramObject.get("location_type");
        
        Integer locationId = Integer.parseInt((String)paramObject.get("location_id"));
        
        
        List<String> unitType = (List<String>) paramObject.get("unit_type");
        locationType = locationType.toLowerCase();
        Integer lastNumberOfMonths = (Integer) paramObject.get("last_number_of_months");

        // START getting the Top Rated locality in a city or suburb.
        int topRatedLocalityId = localityService.getTopRatedLocalityInCityOrSuburb(locationType, locationId.intValue());
        if (topRatedLocalityId == -1)
            return null;
        // END getting top rated Locality

        Map<String, Object> response = new HashMap<String, Object>();

        response.put(
                "price_trends",
                getPropertyPriceTrends("locality", topRatedLocalityId, unitType, lastNumberOfMonths));
        response.put(
                "price_trends_comparison_localites",
                getPriceTrendComparisionLocalities("locality", topRatedLocalityId, unitType, lastNumberOfMonths));
        // getting the Property Price Trends for Top Rated Locality.

        return response;
    }

    /**
     * This method will return the Property Price Trends for top rated locality
     * on city or suburb or locality. It will retrieve the data from the CMS
     * api.
     * 
     * @param paramObject
     *            The map contain three parameters: 1: location_type: city or
     *            suburb or locality 2: location_id: id of the corresponding
     *            location type 3: unit_type: unit type of the property.
     * 
     * @return Object.
     */
    public Object getPropertyPriceTrends(
            String locationType,
            int locationId,
            List<String> unitType,
            int lastNumberOfMonths) {
        return cmsDao.getPropertyPriceTrends(locationType, locationId, unitType, lastNumberOfMonths);
    }

    /**
     * This method will select the top locality in a city or suburb. Then it
     * will get near localities of the top locality. Then it will fetch data for
     * localities selected from cms.
     * 
     * @param paramObject
     *            1: location_type: city or suburb or locality 2: location_id:
     *            id of the corresponding location type 3: unit_type: unit type
     *            of the property.
     * @return Object
     */
    public Object getPriceTrendComparisionLocalities(
            String locationType,
            int locationId,
            List<String> unitType,
            int lastNumberOfMonths) {
        // START getting near by localities of Top Locality
        Pageable pageable = new LimitOffsetPageRequest(0, 5);
        List<NearLocalities> nearLocalitiesList = nearLocalitiesDao.findByMainLocalityOrderByDistanceAsc(
                locationId,
                pageable);
        // END getting near by localities of Top Locality

        if (nearLocalitiesList == null || nearLocalitiesList.size() < 2)
            return null;

        String topRatedLocalityName = nearLocalitiesList.get(0).getLocality().getLabel();

        // START Getting Data from CMS
        Map<Object, Object> response = new LinkedHashMap<>();
        Map<String, Object> cmsOutput = null;

        // getting cms data of near localities of Top Rated Locality.
        for (NearLocalities nearLocality : nearLocalitiesList) {
            cmsOutput = cmsDao.getPropertyPriceTrends(
                    "locality",
                    nearLocality.getNearLocality(),
                    unitType,
                    lastNumberOfMonths);
            if (cmsOutput != null) {
                response.put(nearLocality.getLocality().getLabel(), cmsOutput);
            }
        }

        // END Getting Data from CMS

        // setting top Rated Locality in a seperate Key
        response.put("topRatedLocality", topRatedLocalityName);
        // minimum 3 localities cms data is required to plot graph.
        // top Rated Locality CMS should not be null
        if (response.size() < 3 || response.get(topRatedLocalityName) == null)
            return null;

        return response;
    }
}
