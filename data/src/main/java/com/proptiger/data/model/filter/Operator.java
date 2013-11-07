package com.proptiger.data.model.filter;

/**
 * @author Rajeev Pandey
 *
 */
public enum Operator {
	and, 
	or, 
	range, 
	equal, 
	from, to, 
	geoDistance, 
	lat, 
	lon, 
	distance,
	in,
	notEqual;
}
