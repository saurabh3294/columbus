/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.repo.SolrDao;
import com.proptiger.data.util.PropertyComparer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * @author mukand
 */
@Service
public class RecommendationService {
    @Autowired
    private PropertyDao propertyDao;
    
    public Object getSimilarProperties(long propertyId, int limit){
        // distance, budget%, area%, sort Priority
        int[][] params = new int[][]{
            {5, 15, 15, 1},
            {5, 30, 15, 2},
            {10, 15, 15, 3},
            {10, 30, 15, 4}};
        
        SolrResult viewPropertyData = propertyDao.getProperty(propertyId);
        if(viewPropertyData == null)
            return null;
        Gson gson = new Gson();
        System.out.println(gson.toJson(viewPropertyData));
        
        List<List<SolrResult>> searchPropertiesData = getSimilarPropertiesData(viewPropertyData, limit, params);
        List<SolrResult> response = sortProperties(searchPropertiesData, viewPropertyData);
        return new Object();
    }
    
    private List<List<SolrResult>> getSimilarPropertiesData(SolrResult viewPropertyData, int limit, int[][] params){
        Property property = viewPropertyData.getProperty();
        Project project = viewPropertyData.getProject();
        
        Double area = property.getSize();
        Double price = property.getPricePerUnitArea();
        Double latitude = property.getProcessedLatitue();
        Double longitude = property.getProcessedLongitude();
        String unitType = property.getUnitType();
        String projectStatus = project.getStatus().toLowerCase();
        Integer localityId = project.getLocalityId();
        
        List<Object> projectStatusGroup = (List)getProjectStatusGroups(projectStatus);
        System.out.println("started.");
        //if(!isSimilarPropertySearchValid(area, price, latitude, longitude, longitude, unitType, projectStatusGroup))
        if( !isSimilarPropertySearchValid(viewPropertyData, projectStatusGroup)[0])
            return null;
        
        if(checkDoubleObject(price))
            price = 0.0D;
        if(checkDoubleObject(area))
            area = 0.0D;
        
        double minArea, maxArea, minPrice, maxPrice;
        // TODO to handle the cancelled and 
        
        List<List<SolrResult>> searchPropertiesData= null;
        List<SolrResult> tempSearchProperties = null;
        searchPropertiesData = new LinkedList<>();
        
        for(int i=0; i<params.length &&searchPropertiesData.size()< limit; i++){
            minArea = (100-params[i][2])*area/100;
            maxArea = (100+params[i][2])*area/100;
            minPrice = (100-params[i][1])*price/100;
            maxPrice = (100+params[i][1])*price/100;
            
            tempSearchProperties = propertyDao.getSimilarProperties(params[i][0], latitude, longitude, 
                    minArea, maxArea, minPrice, maxPrice, unitType, projectStatusGroup, limit);
            searchPropertiesData.add(tempSearchProperties);
            System.out.println("i"+i+" : "+tempSearchProperties.size());
        }
        assignPriorityToProperty(searchPropertiesData);
        System.out.println(" FINAL : "+searchPropertiesData.size());
        //Double minArea = area*(100-params)
        
        return searchPropertiesData;
    }
    
    private void assignPriorityToProperty(List<List<SolrResult>> searchPropertiesData){
        SolrResult solrResult;
        List<SolrResult> lowPriorityProperty = new ArrayList<>();
        
        List<Object> projectStatusGroup = new ArrayList<>();
        projectStatusGroup.add("");
        
        boolean[] dataStatus;
        for(List<SolrResult> tempData:searchPropertiesData)
        {
            for(SolrResult tempResult:tempData)
            {
                dataStatus = isSimilarPropertySearchValid(tempResult, projectStatusGroup);
                // area and location data both is false but either of them is present
                // then priority will be changed to last.
                if(!dataStatus[2] && dataStatus[1])
                {
                    lowPriorityProperty.add(tempResult);
                    tempData.remove(tempResult);
                }
            }
        }
        if(lowPriorityProperty.size() > 0)
            searchPropertiesData.add(lowPriorityProperty);
    }
    
    private List<String> getProjectStatusGroups(String projectStatus){
        List<Map<String, Integer>> projectStatusGrouping = getProjectStatusGrouping();
        
        List<String> projectStatusGroup = new ArrayList<>();
        for(Map<String, Integer> map: projectStatusGrouping){
            if( map.containsKey(projectStatus) )
            {
                projectStatusGroup.addAll( map.keySet() );
                break;
            }
        }
        
        return projectStatusGroup;
    }
    
    private List<Map<String, Integer>> getProjectStatusGrouping(){
        List<Map<String, Integer>> projectStatus = new LinkedList<>();
        
        Map<String, Integer> group1 = new HashMap<>();
        group1.put("pre launch", 0);
        group1.put("not launched", 0);
        group1.put("launch", 0);
        group1.put("under construction", 0);
        
        Map<String, Integer> group2 = new HashMap<>();
        group2.put("ready for possession", 1);
        group2.put("occupied", 1);
        
        projectStatus.add(group1);
        projectStatus.add(group2);
        
        return projectStatus;
    }
    
    private boolean checkDoubleObject(Double object){
        return object == null || object.isNaN();
    }
    
    private boolean checkStringObject(String str){
        return str == null || str.length()<1;
    }
    
   /* private boolean isSimilarPropertySearchValid(Double area, Double price, Double latitude, Double longitude
            , Double localityId, String unitType, List<Object> projectStatusGroup){*/
    private boolean[] isSimilarPropertySearchValid(SolrResult propertyData, List<Object> projectStatusGroup)
    {
        Property property = propertyData.getProperty();
        Project project = propertyData.getProject();
        
        String unitType = project.getUnitTypes();
        Double area = property.getSize();
        Double price = property.getPricePerUnitArea();
        Double latitude = property.getProcessedLatitue();
        Double longitude = property.getProcessedLongitude();
        Integer localityId = project.getLocalityId();
                
        System.out.println("UNIT TYPE" + unitType+" area "+area+" price "+price+" latitude "+latitude+" longitude "+longitude+" localityId "+localityId);
        System.out.println(" PROJECT STATUS "+projectStatusGroup.toString());
        boolean isValid = true;
        if(checkStringObject(unitType) || projectStatusGroup.size()<1)
            isValid = false;
        
        Boolean locationStatus = ( !checkDoubleObject(latitude)&&!checkDoubleObject(longitude) ) || checkDoubleObject(localityId.doubleValue());
        Boolean areaStatus = !checkDoubleObject(price) || !checkDoubleObject(area);
        
        // 0> similar properties Data valid
        // 1> area or location data is present.
        // 2> area and location data both is present.
        boolean[] response = new boolean[3];
        response[0] = isValid&&(locationStatus||areaStatus);
        response[1] = locationStatus||areaStatus;
        response[2] = locationStatus&&areaStatus;
        return response;
    }
    
    private List<SolrResult> sortProperties(List<List<SolrResult>> searchPropertiesData, final SolrResult viewedProperty){
        List<SolrResult> finalPropertyResults = new ArrayList<>();
        PropertyComparer propertyComparer = new PropertyComparer(3, 
                viewedProperty.getProperty().getBedrooms());
        
        if(searchPropertiesData == null)
            return null;
        
        for(List<SolrResult> solrResults:searchPropertiesData)
        {
            Collections.sort(solrResults, propertyComparer);
            finalPropertyResults.addAll(finalPropertyResults.size(), solrResults);
        }
        
        return finalPropertyResults;
    }
}
