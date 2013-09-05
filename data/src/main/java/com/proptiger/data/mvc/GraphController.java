/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proptiger.data.service.GraphService;
import java.lang.reflect.Type;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private GraphService graphService = new GraphService();
    
    @RequestMapping("/project-distribution-status-bedroom")
    public @ResponseBody Object getProjectDistrubtionOnStatus(@RequestParam String params){
           Type type = new TypeToken<Map<String, String>>() {}.getType();
           Map<String, String> paramObject = gson.fromJson(params, type);
           
           return graphService.getProjectDistrubtionOnStatus(paramObject);
    }
    
}
