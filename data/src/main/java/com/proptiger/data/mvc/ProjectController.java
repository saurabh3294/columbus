/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import com.proptiger.data.model.Project;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.filter.ProjectFilter;
import com.proptiger.data.service.ProjectService;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public @ResponseBody Object getProjects(ProjectFilter projectFilter) {
        List<Project> projects = projectService.getProjects(projectFilter);
        String fieldsString = projectFilter.getFields();
        String[] fields = null;
        if (fieldsString != null && !fieldsString.isEmpty()) {
            fields = fieldsString.split(",");
        }

        return super.filterFields(projects, fields);
    }
}
