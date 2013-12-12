/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Locality;
import com.proptiger.data.model.NearLocalities;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.SortOrder;
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
    private PropertyDao propertyDao;

    @Resource
    private LocalityDao localityDao;
    
    @Autowired
    private CMSDao cmsDao;
    
    @Autowired
    private NearLocalitiesDao nearLocalitiesDao;
            
    public Map<String, Map<Integer, Integer>> getProjectDistrubtionOnStatus(Map<String, String> params){
        int bedroom_limit = Integer.parseInt( params.get("bedroom_upper_limit") );
        
        // TODO to move configuration  file
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
        Integer value=null, bed=null;
        
        while(keys.hasNext())
        {
            hashKey = keys.next();
            // 0 => project status, 1=> number of beds
            splits = hashKey.split(",");
            splits[0] = splits[0].toLowerCase();
            
            bed = Integer.parseInt(splits[1]);
            if(bed>bedroom_limit)
                bed = bedroom_limit+1;
            
            key = projectStatusMapping.get(splits[0]);
            if(key != null)
            {
                data = response.get(key);
                if(data != null)
                {
                    value = data.get(bed);
                    if(value != null)
                        value += it.get(hashKey);
                    else
                        value = it.get(hashKey);
                }
                else
                {
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
        bed = bedroom_limit+1;
        while(keys.hasNext())
        {
            hashKey = keys.next();
            key = projectStatusMapping.get(hashKey);
            if(key != null)
            {
                data = response.get(key);
                value = data.get(bed);
                data.put(bed, value+it.get(hashKey));
                response.put(key, data);
            }
        }
        
        return response;
    }
    
    public Map<String, Double> getEnquiryDistributionOnLocality(Map<String, Object> params){
        
        Double locationId = (Double)params.get("location_id");
        String locationType = (String)params.get("location_type");
        Double numberOfLocalities = (Double)params.get("number_of_localities");
        Double lastNumberOfMonths = (Double)params.get("last_number_of_months");
        Double cityId = (Double)params.get("city_id");
        
        Long timediff = lastNumberOfMonths.longValue()*30*24*60*60;
        Long locationTypeMap, parentLocationId = locationId.longValue();
        
        locationType = locationType.toLowerCase();
        switch(locationType)
        {
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
        
        Long totalEnquiry = localityDao.findTotalEnquiryCountOnCityOrSubOrLoc(timediff, locationTypeMap, parentLocationId.intValue());
        List<Object[]> localitiesData = localityDao.findEnquiryCountOnCityOrSubOrLoc(timediff, locationTypeMap, parentLocationId.intValue());
        Object[] currentLocalityData = null;
        if(locationTypeMap == 1L)
        {
            currentLocalityData = localityDao.findEnquiryCountOnLoc(timediff, locationId.intValue());
        }
        
        Map<String, Double> response = new LinkedHashMap<String, Double>();
        Object[] data;
        double sum=0;
        double percentage;
        boolean flag = true;
        for(int i=0; i<localitiesData.size()&& i<numberOfLocalities; i++)
        {
            data = (Object[])localitiesData.get(i);
            if(flag && currentLocalityData != null && (Long)currentLocalityData[2]>(Long)data[2] && !response.containsKey(currentLocalityData[1]))
            {
                flag = false;
                percentage = (100*(Long)currentLocalityData[2])/totalEnquiry.doubleValue();
                if(percentage > 1)
                {
                    sum += percentage;
                    response.put((String)currentLocalityData[1], percentage);
                }
                
            }
            if(response.size() < numberOfLocalities )
            {
                percentage = (100*(Long)data[2])/totalEnquiry.doubleValue();
                response.put((String)data[1], percentage);
                if(currentLocalityData == null || currentLocalityData[1]!=data[1] || !response.containsKey(currentLocalityData[1]) )
                    sum += percentage;
            }
        }
        
        response.put("Other Localities", 100-sum);
               
        return response;
    }
    
    public Map<String, Integer> getProjectDistributionOnPrice(Map<String, Object> params){
        
        Map<String, Integer> solrData = propertyDao.getProjectDistributionOnPrice(params).get("PRICE_PER_UNIT_AREA");
        Map<String, Double> customPriceRange = (Map<String, Double>)params.get("custom_price_range");
        Map<String, Integer> response = new LinkedHashMap<String, Integer>();
        
        Iterator<String> priceIt = customPriceRange.keySet().iterator();
        Iterator<String> solrDataIt = solrData.keySet().iterator();
        
        String priceRangeKey = priceIt.next();
        int range = Integer.parseInt( priceRangeKey );
        int count = customPriceRange.get(priceRangeKey).intValue();
        int currentRange = range;
        int oldRange = 0;
        String rangeKey = "0-"+range;
        response.put(rangeKey,0);
        int maxPrice = 0;
        String key = solrDataIt.next();
        int currentPrice;
        int value;
        Integer oldValue;
        boolean customRangeFlag=true;
        boolean solrDataFlag = true;
            
        while(solrDataFlag)
        {
            currentPrice = new Double(key).intValue();
            maxPrice = maxPrice<currentPrice? currentPrice: maxPrice;
            value = solrData.get(key);
            if(count == 0 && priceIt.hasNext())
            {
                priceRangeKey = priceIt.next();
                range = Integer.parseInt(priceRangeKey);
                count = customPriceRange.get(priceRangeKey).intValue();
                oldRange = currentRange;
                currentRange += range;
                response.put(oldRange+"-"+currentRange, 0);
                
            }
            if(currentPrice <= currentRange || (!priceIt.hasNext() && count==0) )
            {
                if(!priceIt.hasNext() && count == 0 && customRangeFlag)
                {
                    oldRange += range;
                    customRangeFlag = false;
                }
                
                oldValue = response.get(oldRange+"-"+currentRange);
                oldValue = (oldValue == null) ?0 : oldValue;
                response.put(oldRange+"-"+currentRange, oldValue+value);
                
                try{
                    key = solrDataIt.next();
                }catch(NoSuchElementException e){
                    solrDataFlag = false;
                }
            }
            else
            {
                count--;
                if(count>0)
                {
                    oldRange = currentRange;
                    currentRange += range;
                    response.put(oldRange+"-"+currentRange, 0);
                }
            } 
        }
        if(currentRange == oldRange)
        {
            key = currentRange+"-"+currentRange;
            count = response.get(key);
            response.remove(key);
            response.put(currentRange+"+", count);
        }
        
        return response;
    }
    
    public Object getPropertyPriceTrends(Map<String, Object> paramObject){
        String locationType = (String)paramObject.get("location_type");
        Double locationId = (Double)paramObject.get("location_id");
        List<String> unitType = (List<String>)paramObject.get("unit_type");
        locationType = locationType.toLowerCase();
                
        return cmsDao.getPropertyPriceTrends(locationType, locationId.intValue(), unitType);
    }
    
    public Object getPriceTrendComparisionLocalities(Map<String, Object> paramObject){
        String locationType = (String)paramObject.get("location_type");
        Double locationId = (Double)paramObject.get("location_id");
        List<String> unitType = (List<String>)paramObject.get("unit_type");
        locationType = locationType.toLowerCase();
        // START getting the Top Rated locality in a city or suburb.
        Paging paging = new Paging(0, 1);
        int topRatedLocalityId;
        List<Locality> locality = null;
        switch (locationType) {
            case "city":
                locality = localityDao.findByLocationOrderByPriority(locationId, "city", paging, SortOrder.ASC);//findByCityIdOrderByPriority(locationId.intValue(), paging, SortOrder.ASC);
                break;
            case "suburb":
                locality = localityDao.findByLocationOrderByPriority(locationId, "suburb", paging, SortOrder.ASC);//findBySuburbIdAndIsActiveAndDeletedFlagOrderByPriorityAsc(locationId.intValue(), true, true, paging);
                break;
        }
        
        if("locality".equals(locationType))
            topRatedLocalityId = locationId.intValue();
        else if(locality.size() > 0)
            topRatedLocalityId = locality.get(0).getLocalityId();
        else
        	return null;
        // END getting top rated Locality
        
        // START getting near by localities of Top Locality
        Pageable pageable = new PageRequest(0, 5);
        List<NearLocalities> nearLocalitiesList = nearLocalitiesDao.findByMainLocalityOrderByDistanceAsc(topRatedLocalityId, pageable);
        // END getting near by localities of Top Locality
        
        // START Getting Data from CMS
        Map<Object, Object> response = new LinkedHashMap<>();
        Object cmsOutput = null;
                        
           // getting cms data of near localities of Top Rated Locality.
        for(NearLocalities nearLocality : nearLocalitiesList)
        {
            cmsOutput = cmsDao.getPropertyPriceTrends("locality", nearLocality.getNearLocality(), unitType);
            if(cmsOutput != null)
                response.put(nearLocality.getNearLocality(), cmsOutput);
        }
        // END Getting Data from CMS
        
        // setting top Rated Locality in a seperate Key
        response.put("topRatedLocality", topRatedLocalityId);
        // minimum 3 localities cms data is required to plot graph.
        // top Rated Locality CMS should not be null
        if(response.size() < 3 || response.get(topRatedLocalityId) == null)
            return null;
        
        return response;
    }
}
