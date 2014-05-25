package com.proptiger.data.pojo;

/**
 * @author azi
 * @author Mandeep Dhir
 */

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.proptiger.exception.ProAPIException;

public class FIQLSelector implements Cloneable, Serializable {
    private static final long serialVersionUID         = 1L;
    private String            fields;
    private String            filters;
    private String            group;
    private String            sort;
    private int               start                    = 0;

    private int               rows                     = 10;

    private static String     monthFilterRegex         = "month(!=|=gt=|=ge=|=lt=|=le=|==)20[0-9]{2}-[0-9]{2}-[0-9]{2}";
    private static String     monthAlwaysTrueStatement = "month!=1970-01-01";

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

    public int getStart() {
        return start;
    }

    public FIQLSelector setStart(int start) {
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

    public FIQLSelector clone() {
        try {
            return (FIQLSelector) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new ProAPIException(e);
        }
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

    public FIQLSelector removeMonthFilter() {
        this.filters = this.filters.replaceAll(monthFilterRegex, monthAlwaysTrueStatement);
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public Set<String> getFieldSet() {
        Set<String> result = new HashSet<>();
        if (this.fields != null && !this.fields.trim().isEmpty()) {
            result = new HashSet<>(Arrays.asList(this.fields.split(",")));
        }
        return result;
    }

    public static enum FIQLOperator {
        Equal("=="), NotEqual("!="), LessThan("=lt="), LessThanEqual("=le="), GreaterThan("=gt="), GreaterThanEqual(
                "=ge="), And(";"), Or(",");

        private String value;

        private FIQLOperator(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}