package com.proptiger.data.service.portfolio;

import java.io.Serializable;

import com.proptiger.data.model.resource.NamedResource;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.exception.InvalidResourceNameException;
import com.proptiger.exception.ResourceNotAvailableException;

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
	protected <T extends Resource> void preProcessCreate(T resource){
		validateName((NamedResource) resource);
		//setting id to null as it will be auto generated, so ignoring its value if passed in request body
		resource.setId(null);
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
	private void validateId(Resource resource) {
		if(resource.getId() == null){
			throw new ResourceNotAvailableException("Resource "+resource.getId()+" not available");
		}
	}
	
	/**
	 * This method is to validate that resource should have a valid name
	 * @param resource
	 */
	private void validateName(NamedResource resource) {
		if(resource.getName() == null || "".equals(resource.getName())){
			throw new InvalidResourceNameException("Inva");
		}
	}
}
