package com.proptiger.columbus.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



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

public class Topsearch {
	

    private String              entityId;
    
    private String              entityType;

    private List<TopsearchObjectField>               builder; 
    
    private List<TopsearchObjectField>               project;
        
    private List<TopsearchObjectField>              locality;
    
    private List<TopsearchObjectField>              suburb;
    

    public String getEntityId() {
        return entityId;
    }
    
    public void setEntityId(String entityId) {
    	this.entityId = entityId;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType.toLowerCase();
    }
    
    public List<TopsearchObjectField> getSuburb() {
        return suburb;
    }
    
    @SuppressWarnings("unchecked")
	public void setSuburb(List<?> suburb) {
    	if(suburb.get(0) instanceof TopsearchObjectField){
    		this.suburb = (List<TopsearchObjectField>) suburb;
    	}
    }
    
    public List<TopsearchObjectField> getLocality() {
        return locality;
    }
    
    @SuppressWarnings("unchecked")
	public void setLocality(List<?> locality) {
    	if(locality.get(0) instanceof TopsearchObjectField){
    		this.locality = (List<TopsearchObjectField>) locality;
    	}
        
    }
    
    public List<TopsearchObjectField> getBuilder() {
        return builder;
    }
    
    @SuppressWarnings("unchecked")
	public void setBuilder(List<?> builder) {
    	if(builder.get(0) instanceof TopsearchObjectField){
    		this.builder = (List<TopsearchObjectField>) builder;
    	}       
    }
    
    public List<TopsearchObjectField> getProject() {
        return project;
    }
    
    @SuppressWarnings("unchecked")
	public void setProject(List<?> project) {
    	if(project.get(0) instanceof TopsearchObjectField){
    		this.project = (List<TopsearchObjectField>) project;
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
