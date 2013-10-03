package com.proptiger.data.model.resource;

/**
 * Any resource that have name must implement this interface
 * @author Rajeev Pandey
 *
 */
public interface NamedResource extends Resource{

	public String getName();
	public void setName(String name);
}
