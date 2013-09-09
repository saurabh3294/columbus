/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import com.proptiger.data.repo.PropertyDao;
import java.util.HashMap;
import java.util.Map;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
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
    
    public NamedList<Object> getProjectDistrubtionOnStatus(Map<String, String> params){
        Map<String, Map<String, String>> projectCounts = new HashMap<String, Map<String, String>>();
        Map<String, String[]> projectStatusMapping = new HashMap<String, String[]>();
        //projectStatusMapping.put("under construction", {"under construction"});
        //projectStatusMapping.put("ready for possession", {"ready for possession", "occupied"});
        //projectStatusMapping.put("launch and upcoming", {"pre launch", "not launched", "launch"});
        NamedList<Object> projectBed = propertyDao.getProjectDistrubtionOnStatusOnBed(params);
        //SolrDocumentList projectMaxBed = propertyDao.getProjectDistrubtionOnStatusOnMaxBed(params);
        return projectBed;
    }
    
    public Object getEnquiryDistributionOnLocality(Map<String, String> params){
        
        //return graphDao.getEnquiryDistributionOnLocality(params);
        return new Object();
    }
    
    public Object getProjectDistributionOnPrice(Map<String, Map<String, String>> params){
        
        return propertyDao.getProjectDistributionOnPrice(params);
    }
            
}
