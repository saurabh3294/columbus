package com.proptiger.data.event.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.meta.DisableCaching;
import com.proptiger.core.model.event.dto.EventRequestDto;
import com.proptiger.core.model.event.subscriber.Subscriber.SubscriberName;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.notification.service.SubscriberConfigService;
import com.proptiger.data.util.Serializer;

@RequestMapping("")
@Controller
@DisableCaching
public class EventSubscriberController {

	@Autowired
	private EventGeneratedService eventGeneratedService;

	@Autowired
	private SubscriberConfigService subscriberConfigService;

	@RequestMapping(value = "data/v1/events", method = RequestMethod.GET)
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

	@RequestMapping(value = "/data/v1/subscriber/{subscriberName}/update-last-event", method = RequestMethod.POST)
	@ResponseBody
	public APIResponse setLastEventGeneratedIdBySubscriber(
			@PathVariable SubscriberName subscriberName,
			@RequestParam Integer eventId) {
		subscriberConfigService.setLastEventGeneratedIdBySubscriberName(
				eventId, subscriberName);
		return new APIResponse();
	}
}
