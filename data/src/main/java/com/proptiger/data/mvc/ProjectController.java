/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Project;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.ProjectService;

/**
 *
 * @author mukand
 */
@Controller
@RequestMapping(value="v1/entity/project")
public class ProjectController extends BaseController {
    @Autowired
    private ProjectService projectService;
    
    @RequestMapping
    public @ResponseBody Object getProjects(@RequestParam(required=false, value = "selector") String selector) throws Exception {
    	Selector propRequestParam = super.parseJsonToObject(selector, Selector.class);
    	if(propRequestParam == null){
    		propRequestParam = new Selector();
    	}
        List<Project> projects = projectService.getProjects(propRequestParam);
        Set<String> fieldsString = propRequestParam.getFields();

        return super.filterFields(projects, fieldsString);
    }
}
