package com.proptiger.data.repo.portfolio;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.portfolio.SavedSearch;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public interface SavedSearchDao extends PagingAndSortingRepository<SavedSearch, Integer>, SavedSearchCustomDao {
	SavedSearch findBySearchQueryAndUserIdOrNameAndUserId(String searchQuery, int userId, String name, int user_id);
}
