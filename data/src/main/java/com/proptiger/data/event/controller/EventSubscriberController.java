package com.proptiger.data.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.model.event.dto.EventRequestDto;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.util.Serializer;

@RequestMapping("data/v1/events")
@Controller
@DisableCaching
public class EventSubscriberController {

	@Autowired
	private EventGeneratedService eventGeneratedService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public APIResponse getEventGeneratedBySubscriber(
			@RequestParam String requestParam) {
		
		EventRequestDto eventRequestDto = Serializer.fromJson(requestParam,
				EventRequestDto.class);

		return new APIResponse(
				eventGeneratedService
						.getLatestVerifiedEventGeneratedsBySubscriber(
								eventRequestDto.getSubscriberName(),
								eventRequestDto.getEventTypeList(),
								eventRequestDto.getPageable()));
	}
}
