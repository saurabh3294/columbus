/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.GraphService;

/**
 *
 * @author mukand
 */
@Controller
@RequestMapping(value="data/v1/entity/graph")
public class GraphController {
    private Gson gson = new Gson();
    @Autowired
    private GraphService graphService;
    
    @RequestMapping(value="/project-distribution-status-bedroom", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Map<String, Map<Integer, Integer>>> getProjectDistrubtionOnStatus(@RequestParam(value="params") String params){
    	   Type type = new TypeToken<Map<String, String>>() {}.getType();
           Map<String, String> paramObject = gson.fromJson(params, type);
           
           if( !paramObject.containsKey("bedroom_upper_limit") )
               paramObject.put("bedroom_upper_limit", "3");
           
           Map<String, Map<Integer, Integer>> solrList = graphService.getProjectDistrubtionOnStatus(paramObject);
           Map<String, Map<String, Map<Integer, Integer>>> response = new HashMap<String, Map<String, Map<Integer, Integer>>>();
           
           response.put("data", solrList);
           return response;
    }
    
    @RequestMapping( value="/enquiry_distribution_locality", method= RequestMethod.GET)
    @ResponseBody
    public Map<String, Map<String, Double>> getEnquiryDistributionOnLocality(@RequestParam(value="params") String params){
           Type type = new TypeToken<Map<String, Object>>() {}.getType();
           Map<String, Object> paramObject = gson.fromJson(params, type);
           
           Double paramDouble = (Double)paramObject.get("number_of_localities");
           Integer param = paramDouble.intValue();
           if( param == null || param <=0 )
            paramObject.put("number_of_localities", 6);
           
           paramDouble = (Double)paramObject.get("last_number_of_months");
           param = paramDouble.intValue();
           if( param == null || param <= 0)
               paramObject.put("last_number_of_months", 1);
           
           Map<String, Map<String, Double>> response = new LinkedHashMap<String, Map<String, Double>>();
           
           response.put("data", graphService.getEnquiryDistributionOnLocality(paramObject) );
           return response;
    }
    
    @RequestMapping(value="/project_distribution_price", method= RequestMethod.GET)
    @ResponseBody
    public Map<String, Map<String, Integer>> getProjectDistributionOnPrice(@RequestParam String params){
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> paramObject = gson.fromJson(params, type);
        
        Map<String, Double> defaultPriceRange = new LinkedTreeMap<String, Double>();
        defaultPriceRange.put("300", 1.0);
        defaultPriceRange.put("700", 1.0);
        defaultPriceRange.put("1000", 9.0);
        defaultPriceRange.put("3000", 2.0);
        defaultPriceRange.put("4000", 6.0);
        
        if( !paramObject.containsKey("custom_price_range") )
            paramObject.put("custom_price_range", defaultPriceRange);
        
        Map<String, Integer> data = graphService.getProjectDistributionOnPrice(paramObject);
        Map<String, Map<String, Integer>> response = new LinkedHashMap<String, Map<String, Integer>>();
        
        response.put("data", data);
        
        return response;
    }
    
    @RequestMapping(value="/property_price_trends", method= RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getPropertyPriceTrends(@RequestParam String params){
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> paramObject = gson.fromJson(params, type);
        System.out.println("testing");
        Map<String, Object> response = new LinkedHashMap();
        
        Object priceTrends = graphService.getPropertyPriceTrends(paramObject);
        Object priceTrendsComparisionLocalities = graphService.getPriceTrendComparisionLocalities(paramObject);
        
        response.put("price_trends", priceTrends);
        response.put("price_trends_comparison_localites", priceTrendsComparisionLocalities);
        
        return new ProAPISuccessResponse(response);
    }
}
