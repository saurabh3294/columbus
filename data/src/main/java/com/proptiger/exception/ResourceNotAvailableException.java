package com.proptiger.exception;

import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;

/**
 * @author Rajeev Pandey
 * 
 */
public class ResourceNotAvailableException extends ProAPIException {

    private static final long serialVersionUID = 6402255527485347856L;

    public ResourceNotAvailableException(ResourceType resourceType, ResourceTypeAction action) {
        super(resourceType.getType() + " " + "you are trying to " + action.getAction() + " is not available");
    }

    public ResourceNotAvailableException(ResourceType resourceType, Integer id, ResourceTypeAction action) {
        super(resourceType.getType() + " with id "
                + id
                + " you are trying to "
                + action.getAction()
                + " is not available");
    }

}
