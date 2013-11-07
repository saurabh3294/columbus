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
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.PropertyDao;
import com.proptiger.data.repo.SolrDao;
import com.proptiger.data.util.PropertyComparer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    
    @Autowired
    private ProjectDao projectDao;
    
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
    		return projectDao.sortingSimilarProjects(similarProjectIds, latitude, longitude, assignedPriority);
    		//return projectDao.getProjectsOnIds( similarProjectIds );
    	return null;
    }
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
        System.out.println("VIEWED PROPERTY");
        printPropertyData(viewPropertyData);
        
        List<List<SolrResult>> searchPropertiesData = getSimilarPropertiesData(viewPropertyData, limit, params);
        List<SolrResult> orderedSearchProperties = sortProperties(searchPropertiesData, viewPropertyData);
        boolean propertyNearBy = isPropertySearchedNearBy(viewPropertyData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("propertyData", orderedSearchProperties);
        response.put("isPropertyNearBy", propertyNearBy);
        return response;
    }
    
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
        System.out.println("started.");
        //if(!isSimilarPropertySearchValid(area, price, latitude, longitude, longitude, unitType, projectStatusGroup))
        if( !isSimilarPropertySearchValid(viewPropertyData, projectStatusGroup)[0])
            return null;
        
        if(checkDoubleObject(price))
            price = 0.0D;
        if(checkDoubleObject(area))
            area = 0.0D;
        if(checkDoubleObject(budget))
        	budget = 0.0D;
        
        double minArea, maxArea, minPrice, maxPrice;
        // TODO to handle the cancelled and 
        
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
            
            System.out.println("MIN AREA "+minArea+" MAX AREA "+maxArea+" MIN PRICE "+minPrice+" MAX PRICE "+maxPrice);
            
            tempSearchProperties = propertyDao.getSimilarProperties(params[i][0], latitude, longitude, 
                    minArea, maxArea, minPrice, maxPrice, unitType, projectStatusGroup, limit, projectIdBedroom, budget, projectId);
            searchPropertiesData.add(tempSearchProperties);
            totalProperties += tempSearchProperties.size();
            
            insertProjectIdBedrooms(tempSearchProperties, projectIdBedroom);
            System.out.println("i"+i+" : "+tempSearchProperties.size());
            
        }
        assignPriorityToProperty(searchPropertiesData, viewPropertyData);
        System.out.println(" FINAL : "+searchPropertiesData.size());
        //Double minArea = area*(100-params)
        
        return searchPropertiesData;
    }
    
    private void assignPriorityToProperty(List<List<SolrResult>> searchPropertiesData, SolrResult viewedPropertyData){
        SolrResult solrResult;
        List<SolrResult> lowPriorityProperty = new ArrayList<>();
        
        List<Object> projectStatusGroup = new ArrayList<>();
        projectStatusGroup.add("dummy");
        System.out.println(" PROPERTY BEING SEARCHED.");
        
        boolean[] dataStatus;
        int i=0;
        Iterator<SolrResult> it = null;
        Iterator<List<SolrResult>> listIt = searchPropertiesData.iterator();
        SolrResult tempResult;
        List<SolrResult> tempData;
        while(listIt.hasNext())
        {
        	tempData = listIt.next();
        	System.out.println("SORT PRIORITY "+i+" number of projects. "+tempData.size());
        	it = tempData.iterator();
            while(it.hasNext())
            {
            	tempResult = it.next();
            	printPropertyData(tempResult);
                dataStatus = isSimilarPropertySearchValid(tempResult, projectStatusGroup);
                // area and location data both is false but either of them is present
                // then priority will be changed to last.
                if(!dataStatus[2] && dataStatus[1])
                {
                	System.out.println("property being moved");
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
        	System.out.println(" END SORT PRIORITY "+i+" number of projects. "+tempData.size());

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
        
        Double area = property.getSize();
        Double price = property.getPricePerUnitArea();
        Double latitude = property.getProcessedLatitue();
        Double longitude = property.getProcessedLongitude();
        Integer localityId = project.getLocalityId();
        String unitType = property.getUnitType();
                
        System.out.println("UNIT TYPE" + unitType+" area "+area+" price "+price+" latitude "+latitude+" longitude "+longitude+" localityId "+localityId);
        System.out.println(" PROJECT STATUS "+projectStatusGroup.toString());
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
    
    private List<SolrResult> sortProperties(List<List<SolrResult>> searchPropertiesData, final SolrResult viewedProperty){
    	   	
    	List<SolrResult> finalPropertyResults = new ArrayList<>();
        PropertyComparer propertyComparer = new PropertyComparer(3, 
                viewedProperty.getProperty().getBedrooms(), viewedProperty.getProperty().getBudget());
        
        if(searchPropertiesData == null)
            return null;
        
        for(List<SolrResult> solrResults:searchPropertiesData)
        {
        	System.out.println("BEFORE SORTING");
        	printList(solrResults);
            Collections.sort(solrResults, propertyComparer);
            System.out.println("AFTER SORTING");
            printList(solrResults);
            finalPropertyResults.addAll(finalPropertyResults.size(), solrResults);
        }
        
        return finalPropertyResults;
    }
    
    private void printList(List<SolrResult> list){
    	for(SolrResult solrResult:list)
    		printPropertyData(solrResult);
    }
    
    private void printPropertyData(SolrResult solrResult){
    	Property property = solrResult.getProperty();
    	Project project = solrResult.getProject();
    	
    	Map<String, Object> data = new LinkedHashMap<>();
    	data.put("project Name", project.getName());
    	data.put("property id", property.getPropertyId());
    	data.put("project_id", project.getProjectId());
    	data.put("price", property.getPricePerUnitArea());
    	data.put("area", property.getSize());
    	data.put("latitude", property.getProcessedLatitue());
    	data.put("longitude", property.getProcessedLongitude());
    	data.put("display_order", project.getAssignedPriority());
    	data.put("project_status", project.getProjectStatus());
    	data.put("unit type", property.getUnitType());
    	data.put("is Resale", project.isIsResale());
    	data.put("bedrooms", property.getBedrooms());
    	data.put("localityId", project.getLocalityId());
    	data.put("budget", property.getBudget());
    	data.put("project description", project.getDescription());
    	
    	Gson gson = new Gson();
    	System.out.println(gson.toJson(data));
    	
    }
    
    private boolean isPropertiesInSameLocality(SolrResult solrResult1, SolrResult solrResult2){
    	return solrResult1.getProject().getLocalityId() == solrResult2.getProject().getLocalityId();
    }
    
    private void insertProjectIdBedrooms(List<SolrResult> propertiesData, List<Object> projectIdBedroomList){
    	
    	for(SolrResult temp:propertiesData){
    		projectIdBedroomList.add(temp.getProperty().getProjectIdBedroom());
    	}
    }
    
    private boolean isPropertySearchedNearBy(SolrResult solrResult){
    	
    	return !checkDoubleObject(solrResult.getProject().getLatitude()) &&  
    			!checkDoubleObject(solrResult.getProject().getLongitude());
    }
}
