package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.enums.DataVersion;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.ProjectPhaseService;

@Controller
@RequestMapping
public class ProjectPhaseController {
    @Autowired
    private ProjectPhaseService projectPhaseService;
    
    @RequestMapping("data/v1/entity/project/{projectId}/phase")
    @ResponseBody
    public ProAPIResponse getPhaseDetails(
           @PathVariable Integer projectId,
           @RequestParam(required=false) DataVersion version
           ){
        if(version == null){
            version = DataVersion.Cms;
        }
        return new ProAPISuccessResponse(projectPhaseService.getPhaseDetailsForProject(projectId, version));
    }
}
