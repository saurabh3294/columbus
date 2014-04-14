package com.proptiger.data.pojo;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class FIQLSelector implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private String            fields;
    private String            filters;
    private String            group;
    private String            sort;
    private Integer           start            = 0;
    private Integer           rows;

    public String getFields() {
        return fields;
    }

    public FIQLSelector setFields(String fields) {
        this.fields = fields;
        return this;
    }

    public String getFilters() {
        return filters;
    }

    public FIQLSelector setFilters(String filters) {
        this.filters = filters;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public FIQLSelector setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public Integer getStart() {
        return start;
    }

    public FIQLSelector setStart(Integer start) {
        this.start = start;
        return this;
    }

    public Integer getRows() {
        return rows;
    }

    public FIQLSelector setRows(Integer rows) {
        this.rows = rows;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public FIQLSelector setGroup(String group) {
        this.group = group;
        return this;
    }

    public FIQLSelector clone() throws CloneNotSupportedException {
        return (FIQLSelector) super.clone();
    }

    public FIQLSelector addAndConditionToFilter(String condition) {
        if (filters == null) {
            filters = condition;
        }
        else {
            filters = "(" + filters + ");(" + condition + ")";
        }
        return this;
    }

    public FIQLSelector addOrConditionToFilter(String condition) {
        if (filters == null) {
            filters = condition;
        }
        else {
            filters = "(" + filters + "),(" + condition + ")";
        }
        return this;
    }

    public FIQLSelector addField(String field) {
        if (fields == null) {
            fields = field;
        }
        else {
            fields += "," + field;
        }
        return this;
    }

    public FIQLSelector addGroupByAtBeginning(String groupBy) {
        if (group == null) {
            group = groupBy;
        }
        else {
            group = groupBy + "," + group;
        }
        return this;
    }

    public FIQLSelector addSortASC(String fieldName) {
        if (fieldName != null) {
            if (this.sort == null || this.sort.trim().isEmpty()) {
                this.sort = fieldName;
            }
            else {
                this.sort += "," + fieldName;
            }
        }
        return this;
    }

    public FIQLSelector addSortDESC(String fieldName) {
        if (fieldName != null) {
            if (this.sort == null || this.sort.trim().isEmpty()) {
                this.sort = "-" + fieldName;
            }
            else {
                this.sort += ",-" + fieldName;
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
