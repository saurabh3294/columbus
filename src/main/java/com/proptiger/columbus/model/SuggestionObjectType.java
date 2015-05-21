package com.proptiger.columbus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "proptiger.master_typeahead_suggestion_types")
@JsonInclude(Include.NON_NULL)
public class SuggestionObjectType {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "typeahead_suggestion_type")
    private String  suggestionType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSuggestionType() {
        return suggestionType;
    }

    public void setSuggestionType(String suggestionType) {
        this.suggestionType = suggestionType;
    }

    @Override
    public String toString() {
        return (this.getId() + ":" + this.getSuggestionType());
    }
}
