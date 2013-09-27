package com.proptiger.data.repo.portfolio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.City;
import com.proptiger.data.model.filter.MySqlQueryBuilder;
import com.proptiger.data.model.portfolio.ForumUserSavedSearch;
import com.proptiger.data.pojo.Selector;

@Component
public class ForumUserSavedSearchDao {

	@Autowired
	private EntityManagerFactory emf;
	
	public List<ForumUserSavedSearch> getUserSavedSearches(Selector selector,
			Integer userId) {

		EntityManager em = emf.createEntityManager();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		
		List<ForumUserSavedSearch> result = new ArrayList<ForumUserSavedSearch>();
		MySqlQueryBuilder<ForumUserSavedSearch> mySqlQueryBuilder = new MySqlQueryBuilder<ForumUserSavedSearch>(
				builder, ForumUserSavedSearch.class);

		mySqlQueryBuilder.buildQuery(selector, userId);
		result = em.createQuery(mySqlQueryBuilder.getQuery()).getResultList();
		
		return result;
	}
}
