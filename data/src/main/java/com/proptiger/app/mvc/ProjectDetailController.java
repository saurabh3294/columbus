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
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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

        List<Project> projects = null; //projectService.getProjects(projectDetailSelector);
        propertyService.getProperties(projectDetailSelector);
        Set<String> fieldsString = projectDetailSelector.getFields();
        
        ProjectSpecification projectSpecification = projectService.getProjectSpecifications(projectId);
        Builder builderDetails = builderService.getBuilderDetailsByProjectId(projectId);
        ProjectDB projectInfo = projectService.getProjectDetails(projectId);
        
        Gson gson = new Gson();
        System.out.println(" SPECS \n"+gson.toJson(projectSpecification));
        System.out.println(" BUILDER \n"+gson.toJson(builderDetails));
        System.out.println(" PROJECT INFO \n"+gson.toJson(projectInfo));
        
        return new ProAPISuccessResponse(super.filterFields(projects, fieldsString));
    }
    
    private Map<String, Object> parseSpecificationObject(ProjectSpecification projectSpecification){
        String specsGroups[] = {"FLOORING", "WALLS", "DOORS", "FITTINGS_AND_FIXTURES", 
            "WINDOWS", "ELECTRICAL_FITTINGS", "OTHERS"};
        Field fields[] = projectSpecification.getClass().getDeclaredFields();
        //fields[0].
        return new HashMap<>();
    }
    
}
