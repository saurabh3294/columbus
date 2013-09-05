/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import com.proptiger.data.repo.GraphDao;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mukand
 */
public class GraphService {
    private GraphDao graphDao = new GraphDao();
    
    public Object getProjectDistrubtionOnStatus(Map<String, String> params){
        Map<String, String[]> projectStatusMapping = new HashMap<String, String[]>();
        //projectStatusMapping.put("under construction", {"under construction"});
        //projectStatusMapping.put("ready for possession", {"ready for possession", "occupied"});
        //projectStatusMapping.put("launch and upcoming", {"pre launch", "not launched", "launch"});

        Object projectBed = graphDao.getProjectDistrubtionOnStatusOnBed(params);
        Object projectMaxBed = graphDao.getProjectDistrubtionOnStatusOnMaxBed(params);
        
        return projectBed;
    }
            
}
