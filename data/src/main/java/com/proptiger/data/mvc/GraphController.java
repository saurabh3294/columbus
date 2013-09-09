/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proptiger.data.service.GraphService;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author mukand
 */
@Controller
@RequestMapping(value="v1/entity/graph")
public class GraphController {
    private Gson gson = new Gson();
    @Autowired
    private GraphService graphService;
    
    @RequestMapping(value="/project-distribution-status-bedroom", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, HashMap<String, Integer>> getProjectDistrubtionOnStatus(@RequestParam(value="params") String params){
           Type type = new TypeToken<Map<String, String>>() {}.getType();
           Map<String, String> paramObject = gson.fromJson(params, type);
                              
           HashMap<String, HashMap<String, Integer>> solrList = graphService.getProjectDistrubtionOnStatus(paramObject);
                      
           return solrList;
    }
    
    @RequestMapping( value="/enquiry_distribution_locality", method= RequestMethod.GET)
    @ResponseBody
    public Object getEnquiryDistributionOnLocality(@RequestParam String params){
           Type type = new TypeToken<Map<String, String>>() {}.getType();
           Map<String, String> paramObject = gson.fromJson(params, type);
           
           return graphService.getEnquiryDistributionOnLocality(paramObject);
    }
    
    @RequestMapping(value="/project_distribution_price", method= RequestMethod.GET)
    @ResponseBody
    public Object getProjectDistributionOnPrice(@RequestParam String params){
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        Map<String, Map<String, String>> paramObject = gson.fromJson(params, type);
        
        return graphService.getProjectDistributionOnPrice(paramObject);
    }
}
