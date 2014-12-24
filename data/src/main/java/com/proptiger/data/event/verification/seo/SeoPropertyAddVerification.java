package com.proptiger.data.event.verification.seo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.enums.event.EventTypeEnum;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.cms.Property;
import com.proptiger.core.model.cms.Suburb;
import com.proptiger.core.model.event.EventGenerated;
import com.proptiger.core.model.event.EventGenerated.EventStatus;
import com.proptiger.data.event.service.EventGeneratedService;
import com.proptiger.data.event.verification.DBEventVerification;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.service.ProjectService;
import com.proptiger.data.service.PropertyService;
import com.proptiger.data.service.SuburbService;

public class SeoPropertyAddVerification extends DBEventVerification {

	@Autowired
	private PropertyService PropertyService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private LocalityService localityService;

	@Autowired
	private SuburbService SuburbService;

	@Autowired
	private EventGeneratedService eventGeneratedService;

	@Override
	public boolean verifyEvents(EventGenerated eventGenerated) {
		int propertyId = Integer.parseInt(eventGenerated
				.getEventTypeUniqueKey());
		Property property = PropertyService
				.getActivePropertyByIdFromDB(propertyId);
		Project project = projectService.getActiveProjectByIdFromDB(property
				.getProjectId());
		Locality locality = localityService
				.getActiveOrInactiveLocalityById(project.getLocalityId());
		Suburb suburb = SuburbService.getActiveOrInactiveSuburbById(locality
				.getSuburbId());
		property.setProject(project);
		project.setLocality(locality);
		locality.setSuburb(suburb);
		
		verifyDomainEvents(property);
		return super.verifyEvents(eventGenerated);
	}

	@Transactional
	public boolean verifyDomainEvents(Property property) {
		Project project = property.getProject();
		Locality locality = project.getLocality();
		Suburb suburb = locality.getSuburb();
		eventGeneratedService.updateEventStatusByEventTypeAndUniqueKey(
				EventTypeEnum.ProjectGenerateUrl.getName(),
				property.getProjectId(), EventStatus.Verified);
		eventGeneratedService.updateEventStatusByEventTypeAndUniqueKey(
				EventTypeEnum.LocalityGenerateUrl.getName(),
				project.getLocalityId(), EventStatus.Verified);
		eventGeneratedService.updateEventStatusByEventTypeAndUniqueKey(
				EventTypeEnum.BuilderGenerateUrl.getName(),
				project.getBuilderId(), EventStatus.Verified);
		eventGeneratedService.updateEventStatusByEventTypeAndUniqueKey(
				EventTypeEnum.SuburbGenerateUrl.getName(),
				locality.getSuburbId(), EventStatus.Verified);
		eventGeneratedService.updateEventStatusByEventTypeAndUniqueKey(
				EventTypeEnum.CityGenerateUrl.getName(), suburb.getCityId(),
				EventStatus.Verified);

		return true;
	}

}
