/**
 * 
 */
package com.proptiger.data.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author mandeep
 *
 */

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonInclude(Include.NON_NULL)
// DO NOT UNCOMMENT IT.
//@JsonFilter("fieldFilter")
public interface BaseModel {
    
}
