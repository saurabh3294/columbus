package com.proptiger.data.service.portfolio;

import com.proptiger.data.model.resource.NamedResource;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.exception.InvalidResourceNameException;

/**
 * @author Rajeev Pandey
 *
 */
public abstract class AbstractService {

	protected <T extends Resource> void preProcessCreate(T resource){
		validateName((NamedResource) resource);
	}
	
	protected <T extends Resource> void preProcessUpdate(T resource){
		validateName((NamedResource) resource);
	}
	
	private void validateName(NamedResource resource) {
		if(resource.getName() == null || "".equals(resource.getName())){
			throw new InvalidResourceNameException("Inva");
		}
	}
}
