package com.proptiger.data.pojo;

public class FIQLSelector {
    private String fields;
    private String filters;
    private String group;
    private String sort;
    private Integer start = 0;
    private Integer rows  = 10;

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
}
