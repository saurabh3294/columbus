package com.proptiger.data.event.model.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;

import com.proptiger.data.notification.model.Subscriber.SubscriberName;

public class EventRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1131787305805612529L;

	@NotNull(message = "Subscriber should be present.")
	private SubscriberName subscriberName;
	private List<String> eventTypeList;
	private Pageable pageable;

	public SubscriberName getSubscriberName() {
		return subscriberName;
	}

	public void setSubscriberName(SubscriberName subscriberName) {
		this.subscriberName = subscriberName;
	}

	public List<String> getEventTypeList() {
		return eventTypeList;
	}

	public void setEventTypeList(List<String> eventTypeList) {
		this.eventTypeList = eventTypeList;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
}
