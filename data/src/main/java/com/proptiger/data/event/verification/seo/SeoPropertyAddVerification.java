package com.proptiger.data.event.verification.seo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
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
		
		eventGeneratedService.verifyDomainEvents(property);
		eventGenerated.setEventStatus(EventStatus.Verified);
		eventGeneratedService.saveOrUpdateOneEvent(eventGenerated);
		return super.verifyEvents(eventGenerated);
	}

	

}
