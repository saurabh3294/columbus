package com.proptiger.data.model.meta;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.core.annotation.AnnotationUtils;

import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * Contains information regarding a data model object.
 * 
 * @author Rajeev Pandey
 *
 */
public class ResourceModelMeta {
	private String name;
	
	private List<FieldMetaData> fieldMeta;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<FieldMetaData> getFieldMeta() {
		return fieldMeta;
	}
	public void setFieldMeta(List<FieldMetaData> fieldMeta) {
		this.fieldMeta = fieldMeta;
	}
	
	public void addFieldMeta(FieldMetaData fieldMeta){
		if(this.fieldMeta == null){
			this.fieldMeta = new ArrayList<FieldMetaData>();
		}
		this.fieldMeta.add(fieldMeta);
	}
	
	public static void main(String[] args){
		Reflections reflections = new Reflections("com.proptiger.data.model", new TypeAnnotationsScanner());
		 Set<Class<?>> annotated = 
				 reflections.getTypesAnnotatedWith(ResourceMetaInfo.class);
		 System.out.println("------------------");
		 Iterator<Class<?>> it = annotated.iterator();
		 while(it.hasNext()){
			 Class<?> cl = it.next();
			 Annotation annotation = cl.getAnnotation(ResourceMetaInfo.class);
			 System.out.println(AnnotationUtils.getAnnotationAttributes(annotation).get("name"));
		 }

	}
}
