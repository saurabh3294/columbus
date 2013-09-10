/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import com.google.gson.Gson;
import com.proptiger.data.repo.PropertyDao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author mukand
 */
@Service
public class GraphService {
    @Autowired
    private PropertyDao propertyDao;
        
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
    
    public Object getEnquiryDistributionOnLocality(Map<String, String> params){
        
        //return graphDao.getEnquiryDistributionOnLocality(params);
        return new Object();
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
        double currentRange = range;
        double oldRange = 0;
        String rangeKey = "0";
        response.put(rangeKey,0);
        double maxPrice = 0;
        String key = solrDataIt.next();
        double currentPrice;
        int value;
        Integer oldValue;
        boolean customRangeFlag=true;
            
        while( solrDataIt.hasNext()  )
        {
            currentPrice = Double.parseDouble(key);
            maxPrice = maxPrice<currentPrice? currentPrice: maxPrice;
            value = solrData.get(key);
            if(count == 0 && priceIt.hasNext())
            {
                priceRangeKey = priceIt.next();
                range = Integer.parseInt(priceRangeKey);
                count = customPriceRange.get(priceRangeKey).intValue();
                oldRange = currentRange;
                currentRange += range;
                response.put(""+oldRange, 0);
                
            }
            if(currentPrice <= currentRange || (!priceIt.hasNext() && count==0) )
            {
                if(!priceIt.hasNext() && count == 0 && customRangeFlag)
                {
                    oldRange += range;
                    customRangeFlag = false;
                }
                
                oldValue = response.get(""+oldRange);
                oldValue = (oldValue == null) ?0 : oldValue;
                response.put(""+oldRange, oldValue+value);
                
                key = solrDataIt.next();
            }
            else
            {
                count--;
                if(count>0)
                {
                    oldRange = currentRange;
                    currentRange += range;
                    response.put(""+oldRange, 0);
                    //$projectCountsRange[$oldRange] = 0;
                }
            } 
        }
        
        Gson gson = new Gson();
        System.out.println(gson.toJson(response));
        /*int currentPrice;
        int count;
        int totalCount = 0;
        int oldPriceRange = 0;
        Integer newPriceRange = 0;
        int rangeCount = 0;
        String key;
        int priceRangeTotal = 0;
        while(solrDataIt.hasNext())
        {
            key = solrDataIt.next();
            currentPrice = Integer.parseInt(key);
            totalCount += solrData.get(key);
            while(priceIt.hasNext() && rangeCount <= 0)
            {
                newPriceRange = Integer.parseInt(priceIt.next());
                rangeCount = customPriceRange.get(newPriceRange.toString());
                priceRangeTotal += newPriceRange;
                
                while(currentPrice> priceRangeTotal && rangeCount>=0){
                    response.put(oldPriceRange+"-"+priceRangeTotal, 0);
                    oldPriceRange = priceRangeTotal;
                    priceRangeTotal += newPriceRange;
                    rangeCount--;
                }
                //if(currentPrice <= priceRangeTotal)
            }
        }*/
        
        //Gson gson = new Gson();
        System.out.println(gson.toJson(solrData));
        return solrData;
    }
            
}
