package com.proptiger.data.repo.portfolio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.City;
import com.proptiger.data.model.filter.FilterQueryBuilder;
import com.proptiger.data.model.filter.MySqlQueryBuilder;
import com.proptiger.data.model.filter.SortQueryBuilder;
import com.proptiger.data.pojo.Selector;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class CityDao {
	@Autowired
	private EntityManagerFactory emf;
	
	@Autowired
	private FilterQueryBuilder filterQueryBuilder;
	
	public List<City> getCities(Selector selector) {

		EntityManager em = emf.createEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		List<City> result = new ArrayList<City>();

		MySqlQueryBuilder<City> mySqlQueryBuilder = new MySqlQueryBuilder<City>(
				builder, City.class);
		filterQueryBuilder.applyFilter(mySqlQueryBuilder,
				selector, City.class);
		SortQueryBuilder.applySort(mySqlQueryBuilder, selector);
		result = em.createQuery(mySqlQueryBuilder.getQuery()).getResultList();

		return result;
	}
	
}
