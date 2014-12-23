package com.proptiger.data.event.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.event.model.dto.EventRequestDto;
import com.proptiger.data.event.service.EventGeneratedService;

@RequestMapping("data/v1/events")
@Controller
public class EventSubscriberController {

	@Autowired
	private EventGeneratedService eventGeneratedService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public APIResponse getEventGeneratedBySubscriber(
			@Valid @RequestBody EventRequestDto requestParam) {
		return new APIResponse(
				eventGeneratedService
						.getLatestVerifiedEventGeneratedsBySubscriber(
								requestParam.getSubscriberName(),
								requestParam.getEventTypeList(),
								requestParam.getPageable()));
	}
}
