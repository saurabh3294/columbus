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
import com.google.gson.Gson;
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
    
   /* public void setProjectStatusCountOnLocality(List<Locality> localities, int cityId){
    	Map<Integer, Map<String, Integer>> localityProjectStatusCount = getProjectStatusCountOnLocalityByCity(cityId);
    	
    	int size = localities.size();
    	Locality locality;
    	for(int i=0; i<size; i++)
    	{
    		locality = localities.get(i);
    		locality.setProjectStatusCount( localityProjectStatusCount.get(locality.getLocalityId()) );
    		//localityProjectStatusCount.remove(locality.getLocalityId());
    	}

    }*/
    
    public Map<Integer, Map<String, Integer>> getProjectStatusCountOnLocalityByCity(int cityId) {
    	return new HashMap<>();
    	/*Map<String, Integer> solrProjectStatusCount = projectDao.getProjectStatusCountOnLocalityByCity(cityId);
    	
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
    	return localityProjectStatusCount;*/
   }
    
}
