package com.proptiger.exception;

import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeField;

public class InvalidResourceException extends ProAPIException {

	private static final long serialVersionUID = 3916295250911467642L;
	
	public InvalidResourceException(ResourceType resourceType, ResourceTypeField resourceTypeField){
		super("Invalid "+resourceType.getType()+" "+resourceTypeField.getType());
	}
	
}
