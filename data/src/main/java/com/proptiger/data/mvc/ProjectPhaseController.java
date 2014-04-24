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
public class ProjectPhaseController extends BaseController {
    @Autowired
    private ProjectPhaseService projectPhaseService;

    @RequestMapping("data/v1/entity/project/{projectId}/phase")
    @ResponseBody
    public ProAPIResponse getPhaseDetailsForProject(
            @PathVariable Integer projectId,
            @RequestParam(required = false) DataVersion version,
            @ModelAttribute FIQLSelector selector) {
        if (version == null) {
            version = DataVersion.Website;
        }
        return new ProAPISuccessResponse(super.filterFields(
                projectPhaseService.getPhaseDetailsForProject(projectId, version),
                selector.getFieldsSet()));
    }

    @RequestMapping("data/v1/entity/project/{projectId}/phase/{phaseId}")
    @ResponseBody
    public ProAPIResponse getPhaseDetail(@PathVariable Integer projectId, @PathVariable Integer phaseId, @RequestParam(
            required = false) DataVersion version, @ModelAttribute FIQLSelector selector) {
        if (version == null) {
            version = DataVersion.Website;
        }
        return new ProAPISuccessResponse(super.filterFields(
                projectPhaseService.getPhaseDetail(projectId, phaseId, version),
                selector.getFieldsSet()));
    }
}
