package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.proptiger.core.model.BaseModel;

public class SampleResult extends BaseModel {

    public SampleResult() {
        super();
    }

    private Integer      id;
    private String       type;
    private String       field;
    private Integer      count;
    private List<String> wrongWords;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getWrongWords() {
        return wrongWords;
    }

    public void setWrongWords(Set<String> wrongWords) {
        this.wrongWords = new ArrayList<String>(wrongWords);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id);
        sb.append("\t");
        sb.append(this.type);
        sb.append("\t");
        sb.append(this.field);
        sb.append("\t");
        sb.append(this.count);
        sb.append("\n");
        sb.append(this.wrongWords);
        sb.append("\n");
        return sb.toString();
    }

}
