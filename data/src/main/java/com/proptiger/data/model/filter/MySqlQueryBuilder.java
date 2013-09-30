package com.proptiger.data.model.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortBy;

/**
 * This class provides implementation to build query specific to my sql database.
 * 
 * @author Rajeev Pandey
 *
 * @param <T>
 */
public class MySqlQueryBuilder<T> extends AbstractQueryBuilder<T>{
	
	private CriteriaBuilder builder;
	private Class<T> domainClazz;
	private CriteriaQuery<T> query;
	private Root<T> root;
	
	public MySqlQueryBuilder(CriteriaBuilder builder, Class<T> clazz){
		this.builder = builder;
		domainClazz = clazz;
		query = builder.createQuery(domainClazz);
		root = query.from(domainClazz);
		
	}

	public void addEqualsFilter(String fieldName, List<Object> values) {
		if(values != null){
			if(values.size() == 1){
				//equal clause
			}
			else if(values.size() > 1){
				query.where(root.get(fieldName).in(values));
			}
		}
		
	}

	public void addGeoFilter(String daoFieldName, double distance,
			double latitude, double longitude) {
		// TODO Auto-generated method stub
		
	}

	public CriteriaQuery<T> getQuery(){
		return this.query;
	}

	@Override
	protected void buildSelectClause(Selector selector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void buildOrderByClause(Selector selector) {
		List<Order> orderByList = new ArrayList<Order>();
		if(selector != null && selector.getSort() != null){
			for (SortBy sortBy : selector.getSort()) {
				switch (sortBy.getSortOrder()) {
				case ASC:
					orderByList.add((builder.asc(root.get(sortBy.getField()))));
					break;
				case DESC:
					orderByList.add(builder.desc(root.get(sortBy.getField())));
					break;
				default:
					break;
				}
			}
		}
		query.orderBy(orderByList);
	}

	@Override
	protected void buildJoinClause(Selector selector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void buildFilterClause(Selector selector, Integer userId) {
		List<Predicate> predicateList = new ArrayList<>();
		if (selector != null && selector.getFilters() != null) {
			Object filterString = selector.getFilters();
			// Map<AND/OR, List<Map<Operator, Map<fieldName, Values> > > >
			Map<String, List<Map<String, Map<String, Object>>>> filters = (Map<String, List<Map<String, Map<String, Object>>>>) filterString;

			List<Map<String, Map<String, Object>>> andFilters = filters
					.get(Operator.and.name());

			if (andFilters != null && filters.size() == 1) {
				for (Map<String, Map<String, Object>> andFilter : andFilters) {
					for (String operator : andFilter.keySet()) {

						Map<String, Object> fieldNameValueMap = andFilter
								.get(operator);

						switch (Operator.valueOf(operator)) {

						case in:
						case equal:
							for (String jsonFieldName : fieldNameValueMap
									.keySet()) {
								List<Object> valuesList = new ArrayList<Object>();
								valuesList
										.addAll((List<Object>) fieldNameValueMap
												.get(jsonFieldName));
								predicateList.add(getEqualsOrInPredicate(
										jsonFieldName, valuesList));
							}
							break;
						case range:
							for (String jsonFieldName : fieldNameValueMap
									.keySet()) {

								Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap
										.get(jsonFieldName);

								if (obj != null) {
									predicateList.add(getRangePredicate(
											jsonFieldName, obj));
								}
								addRangeFilter(jsonFieldName,
										obj.get(Operator.from.name()),
										obj.get(Operator.to.name()));
							}
							break;

						case geoDistance:
							for (String jsonFieldName : fieldNameValueMap
									.keySet()) {
								Map<String, Object> obj = (Map<String, Object>) fieldNameValueMap
										.get(jsonFieldName);

								addGeoFilter(jsonFieldName,
										(Double) obj.get(Operator.distance
												.name()),
										(Double) obj.get(Operator.lat.name()),
										(Double) obj.get(Operator.lon.name()));
							}
							break;

						default:
							throw new IllegalArgumentException(
									"Operator not supported yet");
						}
					}
				}
			}

		}
		//creating user based filtering
		if(userId != null) {
			predicateList.add(root.get("userId").in(userId));
		}
		query = query.where(predicateList.toArray(new Predicate[] {}));
	}

	private Predicate getRangePredicate(String jsonFieldName, Map<String, Object> obj) {
		Predicate predicate = null;
		
		Object from =  obj.get(Operator.from.name());
		Object to =  obj.get(Operator.to.name());
		
		if(from != null && to != null){
		}
		else if(from != null){
			
		}
		else if(to != null){
			
		}
		return predicate;
	}

	private Predicate getEqualsOrInPredicate(String jsonFieldName, List<Object> valuesList) {
		if(valuesList == null || valuesList.size() == 0){
			throw new IllegalArgumentException("Invalid number of values for operator");
		}
		Predicate predicate = null;
		if(valuesList.size() == 1){
			predicate = builder.equal(root.get(jsonFieldName), valuesList.get(0));
		}
		else{
			predicate = root.get(jsonFieldName).in(valuesList);
		}
		
		return predicate;
	}

	@Override
	protected void buildGroupByClause(Selector selector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Class<T> getModelClass() {
		// TODO Auto-generated method stub
		return this.domainClazz;
	}

	 public void addRangeFilter(String fieldName, Object from, Object to) {
		 
	 }
}
