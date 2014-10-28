package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.internal.dto.SenderDetail;
import com.proptiger.data.service.ProjectService;

/**
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
public class MailController {

	@Autowired
	private ProjectService projectService;

	@ResponseBody
	@RequestMapping(value = "data/v1/entity/project/{projectId}/email", method = RequestMethod.POST)
	public APIResponse sendProjectDetailsMail(
			@PathVariable Integer projectId,
			@RequestBody SenderDetail senderDetail) {
		boolean status = projectService.sendProjectDetailsMail(projectId,
				senderDetail);
		return new APIResponse(status);
	}

}
