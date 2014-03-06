/**
 * 
 */
package com.proptiger.data.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author mandeep
 * 
 */

@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public abstract class BaseModel implements Serializable {

    private static final long     serialVersionUID = -143320188173134494L;
    @JsonInclude(Include.NON_EMPTY)
    protected Map<String, Object> extraAttributes  = new HashMap<>();

    public Map<String, Object> getExtraAttributes() {
        return extraAttributes;
    }

    public void setExtraAttributes(Map<String, Object> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
