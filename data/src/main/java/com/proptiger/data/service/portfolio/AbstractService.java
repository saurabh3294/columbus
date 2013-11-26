package com.proptiger.data.service.portfolio;

import com.proptiger.data.model.resource.NamedResource;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.data.util.ResourceType;

/**
 * @author Rajeev Pandey
 *
 */
public abstract class AbstractService {

	/**
	 * This method creates a resource
	 * @param resource
	 * @return
	 */
	protected abstract <T extends Resource> T create(T resource);
	/**
	 * This method updates a resource if resource is present in database
	 * @param resource
	 * @return
	 */
	protected abstract <T extends Resource> T update(T resource);
	
	/**
	 * This method pre process the resource object before creating.
	 * 
	 * Override this method if particular service need different validations
	 * @param resource
	 */
	protected <T extends Resource & NamedResource> void preProcessCreate(T resource){
		validateName(resource);
		//setting id to null as it will be auto generated, so ignoring its value if passed in request body
		resource.setId(null);
	}
	
	/**
	 * This method is a place holder for post process work after creating a resource
	 * @param resource
	 */
	protected <T extends Resource> void postProcessCreate(T resource){

	}
	
	/**
	 * This method pre process the resource before updating that in data store.
	 * Override if different set of validation needed.
	 * @param resource
	 */
	protected <T extends Resource> T preProcessUpdate(T resource){
		validateId(resource);
		validateName((NamedResource) resource);
		return resource;
	}
	
	/**
	 * Validating id part of resource, that should not be null, this is pre check before update
	 * @param resource
	 */
	protected void validateId(Resource resource) {
		if(resource.getId() == null){
			//throw new InvalidResourceException();
		}
	}
	
	/**
	 * This method is to validate that resource should have a valid name
	 * @param resource
	 */
	protected void validateName(NamedResource resource) {
		if(resource.getName() == null || "".equals(resource.getName())){
			//throw new InvalidResourceException("Invalid resource name");
		}
	}
	
	protected abstract ResourceType  getResourceType();
}
