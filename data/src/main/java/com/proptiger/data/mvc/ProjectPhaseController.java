package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.ApplicationNameService;
import com.proptiger.data.service.B2BAttributeService;
import com.proptiger.data.service.ProjectPhaseService;

@Controller
@RequestMapping
public class ProjectPhaseController extends BaseController {
    @Autowired
    private ProjectPhaseService projectPhaseService;

    @Value("${b2b.price-inventory.max.month.dblabel}")
    String                b2bEndDateDbLabel;
    
    @Autowired
    B2BAttributeService b2bAttributeService;
    
    @RequestMapping("data/v1/entity/project/{projectId}/phase")
    @ResponseBody
    public APIResponse getPhaseDetailsForProject(
            @PathVariable Integer projectId,
            @RequestParam(required = false) DataVersion version,
            @ModelAttribute FIQLSelector selector) {
        if (version == null) {
            version = DataVersion.Website;
        }
        
        String endMonth = null;
        if(ApplicationNameService.isB2BApplicationRequest()){
            endMonth = b2bAttributeService.getAttributeByName(b2bEndDateDbLabel);
        }
        
        return new APIResponse(super.filterFields(
                projectPhaseService.getPhaseDetailsForProject(projectId, version, endMonth),
                selector.getFieldSet()));
    }

    @RequestMapping("data/v1/entity/project/{projectId}/phase/{phaseId}")
    @ResponseBody
    public APIResponse getPhaseDetail(@PathVariable Integer projectId, @PathVariable Integer phaseId, @RequestParam(
            required = false) DataVersion version, @ModelAttribute FIQLSelector selector) {
        if (version == null) {
            version = DataVersion.Website;
        }

        String endMonth = null;
        if(ApplicationNameService.isB2BApplicationRequest()){
            endMonth = b2bAttributeService.getAttributeByName(b2bEndDateDbLabel);
        }

        return new APIResponse(super.filterFields(
                projectPhaseService.getPhaseDetail(projectId, phaseId, version, endMonth),
                selector.getFieldSet()));
    }
}
