package com.proptiger.data.repo.portfolio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.filter.MySqlQueryBuilder;
import com.proptiger.data.model.portfolio.ForumUserSavedSearch;
import com.proptiger.data.pojo.Selector;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public interface ForumUserSavedSearchDao extends PagingAndSortingRepository<ForumUserSavedSearch, Integer>, ForumUserSavedSearchCustomDao {
	ForumUserSavedSearch findBySearchQueryAndUserIdOrNameAndUserId(String searchQuery, int userId, String name, int user_id);
}
