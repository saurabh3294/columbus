package com.proptiger.data.model.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.proptiger.data.pojo.SortBy;

/**
 * @author Rajeev Pandey
 *
 * @param <T>
 */
public class MySqlQueryBuilder<T> implements QueryBuilder{
	
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

	@Override
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

	@Override
	public void addRangeFilter(String fieldName, Object from, Object to) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addSort(Set<SortBy> sortBySet) {
		List<Order> orderByList = new ArrayList<Order>();
		for (SortBy sortBy : sortBySet) {
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
		query.orderBy(orderByList);

	}

	@Override
	public void addField(String fieldName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addGeoFilter(String daoFieldName, double distance,
			double latitude, double longitude) {
		// TODO Auto-generated method stub
		
	}

	public CriteriaQuery<T> getQuery(){
		return this.query;
	}

}
