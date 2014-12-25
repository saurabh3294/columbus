package com.proptiger.data.event.model;

import java.util.HashMap;
import java.util.Map;

import com.proptiger.core.enums.event.EventTypeEnum;
import com.proptiger.core.event.model.payload.DefaultEventTypePayload;
import com.proptiger.core.event.model.payload.EventTypePayload;
import com.proptiger.core.event.model.payload.MultiValueEventTypePayload;
import com.proptiger.core.event.model.payload.NewsEventTypePayload;
import com.proptiger.core.model.event.EventTypeConfig;
import com.proptiger.data.event.processor.DBEventProcessor;
import com.proptiger.data.event.processor.DefaultDBEventProcessor;
import com.proptiger.data.event.processor.LocalityNewsProcessor;
import com.proptiger.data.event.processor.PriceChangeProcessor;
import com.proptiger.data.event.processor.ProjectNewsProcessor;
import com.proptiger.data.event.processor.seo.SeoBuilderChangeProcessor;
import com.proptiger.data.event.processor.seo.SeoBuilderDeleteProcessor;
import com.proptiger.data.event.processor.seo.SeoLocalityChangeProcessor;
import com.proptiger.data.event.processor.seo.SeoLocalityDeleteProcessor;
import com.proptiger.data.event.processor.seo.SeoProjectChangeProcessor;
import com.proptiger.data.event.processor.seo.SeoProjectContentChangeProcessor;
import com.proptiger.data.event.processor.seo.SeoProjectDeleteProcessor;
import com.proptiger.data.event.processor.seo.SeoPropertyChangeProcessor;
import com.proptiger.data.event.processor.seo.SeoPropertyDeleteProcessor;
import com.proptiger.data.event.verification.DBEventVerification;
import com.proptiger.data.event.verification.DefaultDBEventVerification;
import com.proptiger.data.event.verification.PriceChangeVerification;
import com.proptiger.data.event.verification.seo.SeoPropertyAddVerification;

public class DefaultEventTypeConfig extends EventTypeConfig{

	DefaultEventTypeConfig(Class<? extends EventTypePayload> dataClassName,
			Class<? extends DBEventProcessor> procClass,
			Class<? extends DBEventVerification> verifiClassName) {

		if (dataClassName != null) {
			this.dataClassName = dataClassName;
		}
		if (processorClassName != null) {
			this.processorClassName = procClass;
		}
		if (verifiClassName != null) {
			this.verificationClassName = verifiClassName;
		}
	}

	public DefaultEventTypeConfig() {

	}

	public static DefaultEventTypeConfig getEventTypeConfig(String configName) {
		return eventTypeConfigMap.get(configName);
	}

	private static Map<String, DefaultEventTypeConfig> eventTypeConfigMap = new HashMap<String, DefaultEventTypeConfig>();

	static {
		eventTypeConfigMap.put(EventTypeEnum.PortfolioPriceChange.getName(),
				new DefaultEventTypeConfig(DefaultEventTypePayload.class,
						PriceChangeProcessor.class,
						PriceChangeVerification.class));
		
		eventTypeConfigMap.put(EventTypeEnum.ProjectGenerateUrl.getName(),
				new DefaultEventTypeConfig(DefaultEventTypePayload.class,
						SeoProjectChangeProcessor.class, null));
		
		eventTypeConfigMap.put(EventTypeEnum.PropertyGenerateUrl.getName(),
				new DefaultEventTypeConfig(DefaultEventTypePayload.class,
						SeoPropertyChangeProcessor.class, null));
		
		eventTypeConfigMap.put(EventTypeEnum.ProjectDeleteUrl.getName(),
				new DefaultEventTypeConfig(DefaultEventTypePayload.class,
						SeoProjectDeleteProcessor.class, null));
		
		eventTypeConfigMap.put(EventTypeEnum.PropertyDeleteUrl.getName(),
				new DefaultEventTypeConfig(DefaultEventTypePayload.class,
						SeoPropertyDeleteProcessor.class, null));
		
		eventTypeConfigMap.put(EventTypeEnum.LocalityGenerateUrl.getName(),
				new DefaultEventTypeConfig(DefaultEventTypePayload.class,
						SeoLocalityChangeProcessor.class, null));
		
		eventTypeConfigMap.put(EventTypeEnum.LocalityDeleteUrl.getName(),
				new DefaultEventTypeConfig(DefaultEventTypePayload.class,
						SeoLocalityDeleteProcessor.class, null));
		
		eventTypeConfigMap.put(EventTypeEnum.BuilderGenerateUrl.getName(),
				new DefaultEventTypeConfig(DefaultEventTypePayload.class,
						SeoBuilderChangeProcessor.class, null));
		
		eventTypeConfigMap.put(EventTypeEnum.BuilderDeleteUrl.getName(),
				new DefaultEventTypeConfig(DefaultEventTypePayload.class,
						SeoBuilderDeleteProcessor.class, null));
		
		eventTypeConfigMap.put(EventTypeEnum.ProjectContentChange.getName(),
				new DefaultEventTypeConfig(MultiValueEventTypePayload.class,
						SeoProjectContentChangeProcessor.class, null));

		eventTypeConfigMap.put(EventTypeEnum.PortfolioProjectNews.getName(),
				new DefaultEventTypeConfig(NewsEventTypePayload.class,
						ProjectNewsProcessor.class,
						DefaultDBEventVerification.class));

		eventTypeConfigMap.put(EventTypeEnum.PortfolioLocalityNews.getName(),
				new DefaultEventTypeConfig(NewsEventTypePayload.class,
						LocalityNewsProcessor.class,
						DefaultDBEventVerification.class));
	}

	protected Class<? extends EventTypePayload> dataClassName = DefaultEventTypePayload.class;
	protected Class<? extends DBEventProcessor> processorClassName = DefaultDBEventProcessor.class;
	protected Class<? extends DBEventVerification> verificationClassName = DefaultDBEventVerification.class;
	protected DBEventProcessor processorObject;
	protected EventTypePayload eventTypePayloadObject;
	protected DBEventVerification eventVerificationObject;

	public Class<? extends EventTypePayload> getDataClassName() {
		return dataClassName;
	}

	public void setDataClassName(Class<? extends EventTypePayload> dataClassName) {
		this.dataClassName = dataClassName;
	}

	public Class<? extends DBEventProcessor> getProcessorClassName() {
		return processorClassName;
	}

	public void setProcessorClassName(
			Class<? extends DBEventProcessor> processorClassName) {
		this.processorClassName = processorClassName;
	}

	public DBEventProcessor getProcessorObject() {
		return processorObject;
	}

	public void setProcessorObject(DBEventProcessor processorObject) {
		this.processorObject = processorObject;
	}

	public EventTypePayload getEventTypePayloadObject() {
		return eventTypePayloadObject;
	}

	public void setEventTypePayloadObject(
			EventTypePayload eventTypePayloadObject) {
		this.eventTypePayloadObject = eventTypePayloadObject;
	}

	public Class<? extends DBEventVerification> getVerificationClassName() {
		return verificationClassName;
	}

	public void setVerificationClassName(
			Class<? extends DBEventVerification> verificationClassName) {
		this.verificationClassName = verificationClassName;
	}

	public DBEventVerification getEventVerificationObject() {
		return eventVerificationObject;
	}

	public void setEventVerificationObject(
			DBEventVerification eventVerificationObject) {
		this.eventVerificationObject = eventVerificationObject;
	}
}