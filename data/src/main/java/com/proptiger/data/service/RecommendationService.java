/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.util.PropertyComparer;


/**
 *
 * @author mukand
 */
@Service
public class RecommendationService {
    @Autowired
    private PropertyDao propertyDao;
    
    @Autowired
    private ProjectDao projectDao;
    
    /**
     * This method will get all properties of a project. Then get the similar properties of each 
     * property of the project. Now taking all project ids of all similar properties found and
     * sorting them and return n number of similar projects based on limit. 
     * @param projectId
     * @param limit number of similar projects.
     * @return List<SolrResult> similar projects.
     */
    public List<SolrResult> getSimilarProjects(int projectId, int limit){
    	List<SolrResult> properties = propertyDao.getPropertiesOnProjectId(projectId);
    	
    	long propertyId;
    	int similarProjectId;
    	int assignedPriority = 0;
    	Double latitude=null, longitude=null;
    	Set<Integer> similarProjectIds = new HashSet<>();
    	List<SolrResult> similarProperties;
    	Map<String, Object> data;
    	Property property;
    	for(SolrResult projectProperty:properties)
    	{
    		property = projectProperty.getProperty();
    		
    		propertyId = property.getPropertyId();
    		latitude = property.getProcessedLatitue();
    		longitude = property.getProcessedLongitude();
    		
    		assignedPriority = projectProperty.getProject().getAssignedPriority();
    		
    		data = getSimilarProperties(propertyId, limit);
    		if(data == null)
    			continue;
    		
    		similarProperties = (List<SolrResult>)data.get("propertyData") ;
    		
    		for(SolrResult solrResult:similarProperties)
    		{
    			similarProjectId = solrResult.getProject().getProjectId();
    			similarProjectIds.add(similarProjectId);
    			System.out.println(" SIMILAR PROJECT ID."+similarProjectId );
    		}
    		similarProperties.clear();
    		data.clear();
    		
    	}
    	
    	if(similarProjectIds.size() > 0)
    		return projectDao.sortingSimilarProjects(similarProjectIds, latitude, longitude, assignedPriority, limit);
    		//return projectDao.getProjectsOnIds( similarProjectIds );
    	return null;
    }
    
    /**
     * This method will get the similar properties of a property Id. It will sort the similar properties.
     * If the viewed property has non valid lat/long and no localityId then isPropertyNearBy flag will
     * be false.
     * @param propertyId viewed property
     * @param limit number of similar properties to return.
     * @return Map<String, Object> It contains:
     *         1: propertyData: List<SolrResult> similar properties found.
     *         2: isPropertyNearBy: boolean whether similar properties found are in nearbyrange.
     */
    public Map<String, Object> getSimilarProperties(long propertyId, int limit){
        // distance, budget%, area%, sort Priority
        int[][] params = new int[][]{
            {5, 15, 15, 1},
            {5, 30, 15, 2},
            {10, 15, 15, 3},
            {10, 30, 15, 4}};
        
        SolrResult viewPropertyData = propertyDao.getProperty(propertyId);
        if(viewPropertyData == null)
            return null;
        
        List<List<SolrResult>> searchPropertiesData = getSimilarPropertiesData(viewPropertyData, limit, params);
        List<SolrResult> orderedSearchProperties = sortProperties(searchPropertiesData, viewPropertyData);
        boolean propertyNearBy = isPropertySearchedNearBy(viewPropertyData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("propertyData", orderedSearchProperties);
        response.put("isPropertyNearBy", propertyNearBy);
        return response;
    }
    
    /**
     * This method will take the viewedProperty Object and return the number of similar properties found.
     * Based on the params, it will iterate through all params until it finds the limit number of similar
     * properties. These params will define the filtering criteria for finding the similar property. Then 
     * assign the priority to each property based on lat/long, price, size, status. 
     * @param viewPropertyData
     * @param limit
     * @param params
     * @return List<List<SolrResult>>
     */
    private List<List<SolrResult>> getSimilarPropertiesData(SolrResult viewPropertyData, int limit, int[][] params){
        Property property = viewPropertyData.getProperty();
        Project project = viewPropertyData.getProject();
        
        Double area = property.getSize();
        Double price = property.getPricePerUnitArea();
        Double latitude = property.getProcessedLatitue();
        Double longitude = property.getProcessedLongitude();
        String unitType = property.getUnitType();
        Double budget = property.getBudget();
        String projectStatus = project.getProjectStatus().toLowerCase();
        Integer localityId = project.getLocalityId();
        int projectId = project.getProjectId();
                
        List<Object> projectStatusGroup = (List)getProjectStatusGroups(projectStatus);
                
        /*
         * If the viewed property is not valid then function will return.
         */
        if( !isSimilarPropertySearchValid(viewPropertyData, projectStatusGroup)[0])
            return null;
        
        if(checkDoubleObject(price))
            price = 0.0D;
        if(checkDoubleObject(area))
            area = 0.0D;
        if(checkDoubleObject(budget))
        	budget = 0.0D;
        
        double minArea, maxArea, minPrice, maxPrice;
        
        List<List<SolrResult>> searchPropertiesData= null;
        List<SolrResult> tempSearchProperties = null;
        searchPropertiesData = new LinkedList<>();
        int totalProperties = 0;
        List<Object> projectIdBedroom = new ArrayList<>();
        projectIdBedroom.add( viewPropertyData.getProperty().getProjectIdBedroom() );
        
        for(int i=0; i<params.length &&totalProperties< limit; i++){
            minArea = (100-params[i][2])*area/100;
            maxArea = (100+params[i][2])*area/100;
            minPrice = (100-params[i][1])*budget/100;
            maxPrice = (100+params[i][1])*budget/100;
            
            tempSearchProperties = propertyDao.getSimilarProperties(params[i][0], latitude, longitude, 
                    minArea, maxArea, minPrice, maxPrice, unitType, projectStatusGroup, limit, projectIdBedroom, budget, projectId);
            searchPropertiesData.add(tempSearchProperties);
            totalProperties += tempSearchProperties.size();
            
            insertProjectIdBedrooms(tempSearchProperties, projectIdBedroom);
        }
        assignPriorityToProperty(searchPropertiesData, viewPropertyData);

        return searchPropertiesData;
    }
    
    
    /**
     * This method will assign priority by moving the properties from high priority list to low priority list. The list 
     * with index lower has high priority. 
     * @param searchPropertiesData
     * @param viewedPropertyData
     */
    private void assignPriorityToProperty(List<List<SolrResult>> searchPropertiesData, SolrResult viewedPropertyData){
        SolrResult solrResult;
        List<SolrResult> lowPriorityProperty = new ArrayList<>();
        
        List<Object> projectStatusGroup = new ArrayList<>();
        projectStatusGroup.add("dummy");
                
        boolean[] dataStatus;
        int i=0;
        Iterator<SolrResult> it = null;
        Iterator<List<SolrResult>> listIt = searchPropertiesData.iterator();
        SolrResult tempResult;
        List<SolrResult> tempData;
        while(listIt.hasNext())
        {
        	tempData = listIt.next();
        	it = tempData.iterator();
            while(it.hasNext())
            {
            	tempResult = it.next();
            	dataStatus = isSimilarPropertySearchValid(tempResult, projectStatusGroup);
                // area and location data both is false but either of them is present
                // then priority will be changed to last.
                if(!dataStatus[2] && dataStatus[1])
                {
                    lowPriorityProperty.add(tempResult);
                    it.remove();
                }
                // if both budget, lat long is not present and property do not belong to same locality
                // then that property will be removed.
                else if( !dataStatus[1] && !isPropertiesInSameLocality(tempResult, viewedPropertyData) )
                	it.remove();
            }
            // if all are removed. That list should be removed.
            if(tempData.size() == 0)
            	listIt.remove();
        }
        if(lowPriorityProperty.size() > 0)
            searchPropertiesData.add(lowPriorityProperty);
    }
    
    /**
     * This method will take the project status of the property and return the groups where
     * it belongs.
     * @param projectStatus
     * @return List<String> list of all project status of the group.
     */
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
    
    /**
     * This method will return the project status grouping for the similar properties.
     * @return List<Map<String, Integer>>
     */
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
    
    /**
     * This method will check where object is null or not.
     * @param object
     * @return boolean true if object is null.
     */
    private boolean checkDoubleObject(Double object){
        return object == null || object.isNaN();
    }
    
    /**
     * This method will check whether string is null or empty.
     * @param str
     * @return boolean if string is null or empty.
     */
    private boolean checkStringObject(String str){
        return str == null || str.length()<1;
    }
    
    /**
     * This method will check the data of property to get the validity of similar property.
     * It will check the following conditions:
     * 1: whether area is null or not. Price is null or not.
     * 2: where it has valid lat/long or localityId
     * @param propertyData
     * @param projectStatusGroup
     * @return boolean array
     *         0 => similar properties Data valid
     *         1 => area or location data is present.
     *         2 => area and location data both is present.
     */
    private boolean[] isSimilarPropertySearchValid(SolrResult propertyData, List<Object> projectStatusGroup)
    {
        Property property = propertyData.getProperty();
        Project project = propertyData.getProject();
        
        Double area = property.getSize();
        Double price = property.getPricePerUnitArea();
        Double latitude = property.getProcessedLatitue();
        Double longitude = property.getProcessedLongitude();
        Integer localityId = project.getLocalityId();
        String unitType = property.getUnitType();
                
        boolean isValid = true;
        if(checkStringObject(unitType) || projectStatusGroup.size()<1)
            isValid = false;
        
        Boolean locationStatus = ( !checkDoubleObject(latitude)&&!checkDoubleObject(longitude) ) || checkDoubleObject(localityId.doubleValue());
        Boolean areaStatus = !checkDoubleObject(price) && !checkDoubleObject(area);
        
        // 0> similar properties Data valid
        // 1> area or location data is present.
        // 2> area and location data both is present.
        boolean[] response = new boolean[3];
        response[0] = isValid&&(locationStatus||areaStatus);
        response[1] = locationStatus||areaStatus;
        response[2] = locationStatus&&areaStatus;
        return response;
    }
    
    
    /**
     * This method will sort the similar properties and return sorted results.
     * @param searchPropertiesData
     * @param viewedProperty
     * @return List<SolrResult> sorted similar properties.
     */
    private List<SolrResult> sortProperties(List<List<SolrResult>> searchPropertiesData, final SolrResult viewedProperty){
    	   	
    	List<SolrResult> finalPropertyResults = new ArrayList<>();
        PropertyComparer propertyComparer = new PropertyComparer(3, 
                viewedProperty.getProperty().getBedrooms(), viewedProperty.getProperty().getBudget());
        
        if(searchPropertiesData == null)
            return null;
        
        for(List<SolrResult> solrResults:searchPropertiesData)
        {
        	Collections.sort(solrResults, propertyComparer);
            finalPropertyResults.addAll(finalPropertyResults.size(), solrResults);
        }
        
        return finalPropertyResults;
    }
            
    
    /**
     * This method will check whether two properties belong to same locality.
     * @param solrResult1
     * @param solrResult2
     * @return boolean true if property belongs to same locality.
     */
    private boolean isPropertiesInSameLocality(SolrResult solrResult1, SolrResult solrResult2){
    	return solrResult1.getProject().getLocalityId() == solrResult2.getProject().getLocalityId();
    }
    
    /**
     * This method will insert the projectIdbedrooms in the list.
     * @param propertiesData
     * @param projectIdBedroomList
     */
    private void insertProjectIdBedrooms(List<SolrResult> propertiesData, List<Object> projectIdBedroomList){
    	
    	for(SolrResult temp:propertiesData){
    		projectIdBedroomList.add(temp.getProperty().getProjectIdBedroom());
    	}
    }
    
    /**
     * This method will determine whether similar properties found was in the property
     * near by area or not.
     * @param solrResult
     * @return boolean true if similar property is nearby.
     */
    private boolean isPropertySearchedNearBy(SolrResult solrResult){
    	
    	return !checkDoubleObject(solrResult.getProject().getLatitude()) &&  
    			!checkDoubleObject(solrResult.getProject().getLongitude());
    }
}
