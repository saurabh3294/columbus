/**
 * 
 */
package com.proptiger.app.mvc;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Project;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.PropertyService;

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
    
    @RequestMapping(value="app/v1/project-detail")
    public @ResponseBody ProAPIResponse getProjectDetails(@RequestParam(required=false) String selector) throws Exception {
        Selector projectDetailSelector = super.parseJsonToObject(selector, Selector.class);
        if(projectDetailSelector == null){
            projectDetailSelector = new Selector();
        }

        List<Project> projects = null; //projectService.getProjects(projectDetailSelector);
        propertyService.getProperties(projectDetailSelector);
        Set<String> fieldsString = projectDetailSelector.getFields();

        return new ProAPISuccessResponse(super.filterFields(projects, fieldsString));
    }
}
