package com.proptiger.data.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class SeoPage extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6065883078087404621L;

}
