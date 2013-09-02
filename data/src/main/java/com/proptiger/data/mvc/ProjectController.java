/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import com.proptiger.data.model.Project;
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
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    
    @RequestMapping
    public @ResponseBody List<Project> getProjects(ProjectFilter projectFilter) throws SolrServerException{
        return projectService.getProjects(projectFilter);
    }
}
