/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import com.proptiger.data.repo.GraphDao;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 *
 * @author mukand
 */
@Service
public class GraphService {
    private GraphDao graphDao = new GraphDao();
    
    public Object getProjectDistrubtionOnStatus(Map<String, String> params){
        Map<String, Map<String, String>> projectCounts = new HashMap<String, Map<String, String>>();
        Map<String, String[]> projectStatusMapping = new HashMap<String, String[]>();
        //projectStatusMapping.put("under construction", {"under construction"});
        //projectStatusMapping.put("ready for possession", {"ready for possession", "occupied"});
        //projectStatusMapping.put("launch and upcoming", {"pre launch", "not launched", "launch"});

        Object projectBed = graphDao.getProjectDistrubtionOnStatusOnBed(params);
        Object projectMaxBed = graphDao.getProjectDistrubtionOnStatusOnMaxBed(params);
        
        return projectBed;
    }
    
    public Object getEnquiryDistributionOnLocality(Map<String, String> params){
        
        return graphDao.getEnquiryDistributionOnLocality(params);
    }
    
    public Object getProjectDistributionOnPrice(Map<String, Map<String, String>> params){
        
        return graphDao.getProjectDistributionOnPrice(params);
    }
            
}
