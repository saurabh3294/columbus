/**
 * 
 */
package com.proptiger.data.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.SolrResult;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.ProjectDao;


/**
 * @author mandeep
 *
 */
@Service
public class LocalityService {
    @Autowired
    private LocalityDao localityDao;
    
    @Autowired
    private ProjectDao projectDao;
    
    public List<Locality> getLocalities(Selector selector) {
        return Lists.newArrayList(localityDao.getLocalities(selector));
    }
    
    public List<Locality> getLocalityListing(int cityId){
    	List<Locality> localities = localityDao.findByCityIdAndIsActiveAndDeletedFlagOrderByPriorityAsc(cityId, true, true, null);
    	setProjectStatusCountAndProjectCountOnLocality(localities, cityId);
    	return localities;
    }
    
    public void setProjectStatusCountAndProjectCountOnLocality(List<Locality> localities, int cityId){
    	Map<String,Map<String, Integer>> solrProjectStatusCountAndProjectCount = projectDao.getProjectStatusCountAndProjectOnLocalityByCity(cityId);
    	Map<Integer, Map<String, Integer>> localityProjectStatusCount = getProjectStatusCountOnLocalityByCity(cityId, 
    																										solrProjectStatusCountAndProjectCount.get("LOCALITY_ID_PROJECT_STATUS"));
    	Map<String, Integer> projectCountOnLocality = solrProjectStatusCountAndProjectCount.get("LOCALITY_ID");
    	long totalProjectCountsOnCity = projectDao.getProjectCountCity(cityId);
    	
    	int size = localities.size();
    	Locality locality;
    	Integer projectCount;
    	for(int i=0; i<size; i++)
    	{
    		locality = localities.get(i);
    		locality.setProjectStatusCount( localityProjectStatusCount.get(locality.getLocalityId()) );
    		projectCount = projectCountOnLocality.get( locality.getLocalityId()+"" );
    		if( projectCount != null )
    			locality.setProjectCount( projectCount.intValue() );
    		
    		locality.getSuburb().getCity().setProjectsCount(totalProjectCountsOnCity);
    		//localityProjectStatusCount.remove(locality.getLocalityId());
    	}

    }
    
    public Map<Integer, Map<String, Integer>> getProjectStatusCountOnLocalityByCity(int cityId, Map<String, Integer> solrProjectStatusCount) {
    	Map<Integer, Map<String, Integer>> localityProjectStatusCount = new HashMap<Integer, Map<String,Integer>>();
    	String[] split;
    	Integer localityId;
    	Map<String, Integer> projectStatusCount=null; 
    	for	(Map.Entry<String, Integer> entry : solrProjectStatusCount.entrySet()){
    		//localityId:projectStatus
    		split = entry.getKey().split(":");
    		localityId = Integer.parseInt(split[0]);
    		projectStatusCount = localityProjectStatusCount.get(localityId);
    		if(projectStatusCount == null)
    		{
    			projectStatusCount = new HashMap<String, Integer>();
    		}
    		projectStatusCount.put(split[1], entry.getValue());
    		localityProjectStatusCount.put(localityId, projectStatusCount);
    	
    	}
    	return localityProjectStatusCount;
   }
    
   public Double getMaxRadiusForLocalityOnProject(int localityId){
	   Locality locality = localityDao.findByLocalityId(localityId);
	   List<SolrResult> projectSolrResults = projectDao.getProjectsByGEODistanceByLocality(localityId, locality.getLatitude()
			   , locality.getLongitude(), 1);
	   
	   if(projectSolrResults.size() > 0)
		   return projectSolrResults.get(0).getProject().getLocality().getDerivedMaxRadius();
	   return null;
   }
}
