/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.columbus.model;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.proptiger.columbus.util.TopsearchObjectField;
import com.proptiger.columbus.util.TypeaheadUtils;
import com.proptiger.core.model.BaseModel;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.init.CustomObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;




/**
 * 
 * @author Manmohan
 */
@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
@JsonIgnoreProperties({"mapper"})
public class Topsearch extends BaseModel {
	
	//private Logger  logger = LoggerFactory.getLogger(Topsearch.class);

    private static final long   serialVersionUID              = 2096261268711516512L;

    @Field(value = "id")
    private String              entityId;
    
    @Field(value = "TYPEAHEAD_TYPE")
    private String              entityType;

    @Transient
    private boolean             authorized                    = true;

   
    @Field(value = "TYPEAHEAD_TOP_SEARCHED_BUILDER")
    private List<TopsearchObjectField>               builder; 
    
    @Field(value = "TYPEAHEAD_TOP_SEARCHED_PROJECT")
    private List<TopsearchObjectField>               project;
    
    
    private List<TopsearchObjectField>              locality;
    
    @Field(value = "TYPEAHEAD_TOP_SEARCHED_SUBURB")
    private List<TopsearchObjectField>              suburb;
    
    private ObjectMapper  mapper= new ObjectMapper();
    
    
    
    public String getEntityId() {
        return entityId;
    }
    
    @Field(value = "id")
    public void setEntityId(String entityId) {
    	this.entityId = TypeaheadUtils.parseEntityIdAsString(entityId);
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    @Field(value = "TYPEAHEAD_TYPE")
    public void setEntityType(String entityType) {
        this.entityType = entityType.toLowerCase();
    }
    
    public List<TopsearchObjectField> getSuburb() {
        return suburb;
    }
    
    @Field(value = "TYPEAHEAD_TOP_SEARCHED_SUBURB")
    public void setSuburb(String suburb) {
    	
    	try {
    		
    		//String jsonString = "[{\"ID\":10229,\"TYPEAHEAD_LABEL\":\"Ahmedabad West Ahmedabad\",\"TYPEAHEAD_REDIRECT_URL\":\"ahmedabad\\/property-sale-ahmedabad-west-10229\"}]";
    		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    	    List<TopsearchObjectField> navigation = mapper.readValue(
    	    		suburb,
    	    		mapper.getTypeFactory().constructCollectionType(
    	                    List.class, TopsearchObjectField.class));
    	    this.suburb = navigation;
        }
        catch (IOException e) {
            throw new ProAPIException("Error Mapping json to :" + TopsearchObjectField.class, e);
        }
    	
        
    }
    
    
    public List<TopsearchObjectField> getLocality() {
        return locality;
    }
    
    @Field(value = "TYPEAHEAD_TOP_SEARCHED_LOCALITY")
    public void setLocality(String locality) {
    	
    	try {
    		
    		//String jsonString = "[{\"ID\":10229,\"TYPEAHEAD_LABEL\":\"Ahmedabad West Ahmedabad\",\"TYPEAHEAD_REDIRECT_URL\":\"ahmedabad\\/property-sale-ahmedabad-west-10229\"}]";
    		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    	    List<TopsearchObjectField> navigation = mapper.readValue(
    	    		locality,
    	    		mapper.getTypeFactory().constructCollectionType(
    	                    List.class, TopsearchObjectField.class));
    	    this.locality = navigation;
        }
        catch (IOException e) {
            throw new ProAPIException("Error Mapping json to :" + TopsearchObjectField.class, e);
        }
    	
        
    }
    
    public List<TopsearchObjectField> getBuilder() {
        return builder;
    }
    
    @Field(value = "TYPEAHEAD_TOP_SEARCHED_BUILDER")
    public void setBuilder(String builder) {
    	
    	try {
    		
    		//String jsonString = "[{\"ID\":10229,\"TYPEAHEAD_LABEL\":\"Ahmedabad West Ahmedabad\",\"TYPEAHEAD_REDIRECT_URL\":\"ahmedabad\\/property-sale-ahmedabad-west-10229\"}]";
    		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    	    List<TopsearchObjectField> navigation = mapper.readValue(
    	    		builder,
    	    		mapper.getTypeFactory().constructCollectionType(
    	                    List.class, TopsearchObjectField.class));
    	    this.builder = navigation;
        }
        catch (IOException e) {
            throw new ProAPIException("Error Mapping json to :" + TopsearchObjectField.class, e);
        }
    	
        
    }
    
    public List<TopsearchObjectField> getProject() {
        return project;
    }
    
    @Field(value = "TYPEAHEAD_TOP_SEARCHED_PROJECT")
    public void setProject(String project) {
    	
    	try {
    		
    		//String jsonString = "[{\"ID\":10229,\"TYPEAHEAD_LABEL\":\"Ahmedabad West Ahmedabad\",\"TYPEAHEAD_REDIRECT_URL\":\"ahmedabad\\/property-sale-ahmedabad-west-10229\"}]";
    		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    	    List<TopsearchObjectField> navigation = mapper.readValue(
    	    		project,
    	    		mapper.getTypeFactory().constructCollectionType(
    	                    List.class, TopsearchObjectField.class));
    	    this.project = navigation;
        }
        catch (IOException e) {
            throw new ProAPIException("Error Mapping json to :" + TopsearchObjectField.class, e);
        }
    	
        
    }
   
   
    
    

     


    @Override
    public String toString() {
    	String str=entityId+":";
    	for (TopsearchObjectField tmp: suburb){
            str += tmp.toString();
        }
        return str;
    }
}