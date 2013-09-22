/**
 * 
 */
package com.proptiger.app.mvc;

import com.google.gson.Gson;
import com.proptiger.data.model.Builder;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.ProjectDB;
import com.proptiger.data.model.ProjectSpecification;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.BuilderService;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.pojo.SolrServiceResponse;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * @author mandeep
 *
 */
@Controller
public class ProjectDetailController extends BaseController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private BuilderService builderService;
    
    @RequestMapping(value="app/v1/project-detail")
    public @ResponseBody ProAPIResponse getProjectDetails(@RequestParam(required=false) String selector,@RequestParam int projectId) throws Exception {
        Selector projectDetailSelector = super.parseJsonToObject(selector, Selector.class);
        if(projectDetailSelector == null){
            projectDetailSelector = new Selector();
        }

        SolrServiceResponse<List<Project>> projects = projectService.getProjects(projectDetailSelector);
        propertyService.getProperties(projectDetailSelector);
        Set<String> fieldsString = projectDetailSelector.getFields();
        
        ProjectSpecification projectSpecification = projectService.getProjectSpecifications(projectId);
        Builder builderDetails = builderService.getBuilderDetailsByProjectId(projectId);
        ProjectDB projectInfo = projectService.getProjectDetails(projectId);
        
        Gson gson = new Gson();
        System.out.println(" SPECS \n"+gson.toJson(projectSpecification));
        System.out.println(" BUILDER \n"+gson.toJson(builderDetails));
        System.out.println(" PROJECT INFO \n"+gson.toJson(projectInfo));
        
        
        Map<String, Object> parseSpecification = parseSpecificationObject(projectSpecification);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("specification", parseSpecification);
        response.put("projectDescription", projectInfo.getProjectDescription());
        response.put("builderDescription", builderDetails.getDescription());
        response.put("properties", super.filterFields(projects, fieldsString));
        
        //return new ProAPISuccessResponse(super.filterFields(projects, fieldsString));
        return new ProAPISuccessResponse(response);
    }
    
    private Map<String, Object> parseSpecificationObject(ProjectSpecification projectSpecification){
        String specsGroups[] = {"doors", "electricalFittings", "fittingsAndFixtures", "flooring", "id", "others", "walls",  "windows"};
        
        Field fields[] = projectSpecification.getClass().getDeclaredFields();
        
        Map<String, Integer> fieldsMap = new TreeMap<>();
        
        for(int i=0; i<fields.length; i++)
            fieldsMap.put(fields[i].getName(), i);
        
        Map<String, Object> parseMap = new LinkedHashMap<String, Object>();
        Map<String, Object> splitKeys;
        
        int i=0;
        String key;
        String keySuffix;
        boolean found = false;
        int index;
        Object value = null;
        for(Map.Entry<String, Integer> entry: fieldsMap.entrySet())
        {
            key = entry.getKey();
            found = false;
            while(!found && i<specsGroups.length)
            {
                found = key.startsWith(specsGroups[i++]);
            }
            if(!found)
                break;
            
            i--;
            keySuffix = key.substring(specsGroups[i].length());
            if( !parseMap.containsKey(specsGroups[i]) )
                splitKeys = new LinkedHashMap<String, Object>();
            else
                splitKeys = (Map<String, Object>)parseMap.get(specsGroups[i]);
            
            index = entry.getValue();
            try{
                fields[index].setAccessible(true);
                value = fields[index].get(projectSpecification);
            }catch(Exception e){
                value = null;
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            if(keySuffix.length() <= 0)
                parseMap.put(specsGroups[i], value);
            else
            {
                splitKeys.put(keySuffix, value);
                parseMap.put(specsGroups[i], splitKeys);
            }
        }
        
        return parseMap;
    }
    
}
