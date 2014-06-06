package com.proptiger.data.repo.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.UserPreference;

/**
 * 
 * @author Azitabh Ajit
 * 
 */

public interface UserPreferenceDao extends PagingAndSortingRepository<UserPreference, Integer> {
}
