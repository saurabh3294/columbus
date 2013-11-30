package com.proptiger.data.util;

import com.proptiger.data.model.DomainObject;
import com.proptiger.data.model.Property;

public class IdConverterForDatabase {

	public static Integer convertProjectIdFromCMSToProptiger(Property property){
		Integer projectId = 0;
		if (property != null
				&& property.getProjectId() > DomainObject.project
						.getStartId()) {
			projectId = property.getProjectId()	- DomainObject.project.getStartId();
		}
		return projectId;
	}
	public static Integer convertPropertyIdFromCMSToProptiger(Integer propertyId){
		Integer typeId = 0;
		if (propertyId != null
				&& propertyId > DomainObject.property.getStartId()) {
			typeId = propertyId	- DomainObject.property.getStartId();
		}
		return typeId;
	}
}
