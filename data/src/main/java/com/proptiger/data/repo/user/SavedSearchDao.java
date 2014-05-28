package com.proptiger.data.repo.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.user.SavedSearch;

/**
 * @author Rajeev Pandey
 * 
 */
@Component
public interface SavedSearchDao extends PagingAndSortingRepository<SavedSearch, Integer>, SavedSearchCustomDao {
    SavedSearch findBySearchQueryAndUserId(String searchQuery, Integer userId);

    SavedSearch findByNameAndUserId(String name, Integer userId);
}
