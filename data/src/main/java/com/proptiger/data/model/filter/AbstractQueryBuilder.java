/**
 * 
 */
package com.proptiger.data.model.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.proptiger.data.pojo.Selector;


/**
 * This class provides methods to build datastore query. Individual datastore type must implement
 * abstract methods to provide specific implementation supported by that datastore (mysql/oracle/solr/lucene).
 * 
 * 
 * @author Rajeev Pandey
 *
 * @param <T>
 */
@Component
public abstract class AbstractQueryBuilder<T>{
	
	public void buildQuery(Selector selector, Integer userId){
		if(selector != null){
			if(selector.getFields() != null){
				buildSelectClause(selector);
			}
			if(selector.getSort() != null){
				buildOrderByClause(selector);
			}
			buildFilterClause(selector, userId);
		}
	}
	
	protected abstract void buildSelectClause(Selector selector);
	protected abstract void buildOrderByClause(Selector selector);
	protected abstract void buildJoinClause(Selector selector);
	
	/**
	 * Override this method if where clause can not be called multiple times like in Spring Data JPA criteria query
	 * @param selector
	 */
	protected void buildFilterClause(Selector selector, Integer userId){
		

		Object filterString = selector.getFilters();
	       
	       //Map<AND/OR, List<Map<Operator, Map<fieldName, Values> > > >
	        Map<String, List<Map<String, Map<String, Object>>>> filters = (Map<String, List<Map<String, Map<String, Object>>>>) filterString;
	        
	        List<Map<String, Map<String, Object>>> andFilters = filters.get(Operator.and.name());
	        
	        if (andFilters != null && filters.size() == 1) {
	            for (Map<String, Map<String, Object>> andFilter : andFilters) {
	                for (String operator : andFilter.keySet()) {
	                	
	                    Map<String, Object> fieldNameValueMap = andFilter.get(operator);
	                    
						switch (Operator.valueOf(operator)) {
	                    
	                    case equal:
	                        for (String jsonFieldName : fieldNameValueMap.keySet()) {
	                            Field field = FieldsMapLoader.getField(getModelClass(), jsonFieldName);

	                            List<Object> valuesList = new ArrayList<Object>();
	                            for (Object obj : (List<Object>) fieldNameValueMap.get(jsonFieldName)) {
	                                //valuesList.add(typeConvertor.convert(obj, field.getType()));
	                                valuesList.add(obj);
	                            }
	                            addEqualsFilter(jsonFieldName, valuesList);
	                        }
	                        break;

	                    case range:
	                        for (String jsonFieldName : fieldNameValueMap.keySet()) {
	                            Field field = FieldsMapLoader.getField(getModelClass(), jsonFieldName);

	                            Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap.get(jsonFieldName);
	                            //addRangeFilter(jsonFieldName, typeConvertor.convert(obj.get(Operator.from.name()), field.getType()),
	                            //                                          typeConvertor.convert(obj.get(Operator.to.name()), field.getType()));
	                           
	                            addRangeFilter(jsonFieldName, obj.get(Operator.from.name()),
	                            		obj.get(Operator.to.name()));
	                        }
	                        break;
	                    
	                    case geoDistance:
	                        for (String jsonFieldName : fieldNameValueMap.keySet()) {
	                            Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap.get(jsonFieldName);
	                            addGeoFilter(jsonFieldName, (Double) obj.get(Operator.distance.name()),
	                                                                    (Double) obj.get(Operator.lat.name()),
	                                                                    (Double) obj.get(Operator.lon.name()));
	                        }
	                        break;

	                    default:
	                        // throw exception
	                        break;
	                    }
	                }
	            }
	        }
	    
		
	
	}
	protected abstract void buildGroupByClause(Selector selector);
	
	protected abstract void addEqualsFilter(String fieldName, List<Object> values);

	protected abstract void addRangeFilter(String fieldName, Object from, Object to);

    /*public abstract void addSort(Set<SortBy> sortBySet);

    public abstract void addField(String fieldName);*/

	protected abstract void addGeoFilter(String daoFieldName, double distance, double latitude, double longitude);
    protected abstract Class<T> getModelClass();
    
}
