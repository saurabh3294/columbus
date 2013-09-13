package com.proptiger.data.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.meta.FieldMetaData;
import com.proptiger.data.model.meta.ResourceModelMeta;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * This class will provide the meta information of all classes annotated with ResourceMetaInfo,
 * that meta information includes resource name and field meta data of that class annotated with FieldMetaInfo
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class MetaService {

	private Map<String, ResourceModelMeta> resourceMetaMap;
	@Autowired
	private PropertyReader propertyReader;
	
	@PostConstruct
	public void init() {
		resourceMetaMap = new HashMap<String, ResourceModelMeta>();
		
		Reflections reflections = new Reflections(propertyReader.getRequiredProperty("metainfo.package.to.scan"),
				new TypeAnnotationsScanner());
		Set<Class<?>> annotated = reflections
				.getTypesAnnotatedWith(ResourceMetaInfo.class);
		Iterator<Class<?>> itr = annotated.iterator();
		Annotation resourceAnnotation = null;
		while(itr.hasNext()){
			ResourceModelMeta resourceModelMeta = new ResourceModelMeta();
			Class<?> clazz = itr.next();
			resourceAnnotation = clazz.getAnnotation(ResourceMetaInfo.class);
			resourceModelMeta.setName((String)AnnotationUtils.getAnnotationAttributes(resourceAnnotation).get("name"));
			
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				FieldMetaData fieldMetaData = new FieldMetaData();
				Annotation fieldAnnotation = field.getAnnotation(FieldMetaInfo.class);
				if(fieldAnnotation != null){
					fieldMetaData.setDataType((String) AnnotationUtils
							.getAnnotationAttributes(fieldAnnotation).get(
									"dataType").toString());
					fieldMetaData.setDescription((String) AnnotationUtils
							.getAnnotationAttributes(fieldAnnotation).get(
									"description"));
					fieldMetaData.setDisplayName((String) AnnotationUtils
							.getAnnotationAttributes(fieldAnnotation).get(
									"displayName"));
					fieldMetaData.setEditable((Boolean) AnnotationUtils
							.getAnnotationAttributes(fieldAnnotation).get(
									"editable"));
					fieldMetaData.setName((String) AnnotationUtils
							.getAnnotationAttributes(fieldAnnotation).get("name"));
					resourceModelMeta.addFieldMeta(fieldMetaData);
				}
				
			}
			resourceMetaMap.put(resourceModelMeta.getName(), resourceModelMeta);
		}
	}
	
	/**
	 * Return ResourceModelMeta for a particular resource if name passed otherwise returns
	 * meta information of all resources
	 * @param resourceName
	 * @return ResourceModelMeta or list of ResourceModelMeta
	 */
	public List<ResourceModelMeta> getResourceMeta(String resourceName){
		if(resourceName == null){
			return getAllResourceMeta();
		}
		ResourceModelMeta resourceModelMeta = resourceMetaMap.get(resourceName);
		if(resourceModelMeta == null){
			throw new ResourceNotAvailableException("Invalid resource name:"+resourceName);
		}
		List<ResourceModelMeta> list = new ArrayList<ResourceModelMeta>();
		list.add(resourceModelMeta);
		return list;
	}
	
	/**
	 * @return List<ResourceModelMeta>
	 */
	private List<ResourceModelMeta> getAllResourceMeta(){
		return new ArrayList<ResourceModelMeta>(resourceMetaMap.values());
	}

}
