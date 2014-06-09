package com.proptiger.data.repo.user;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.UserPreference;

/**
 * 
 * @author Azitabh Ajit
 * 
 */

public interface UserPreferenceDao extends PagingAndSortingRepository<UserPreference, Integer> {
    public List<UserPreference> findByuserId(int userId);
}
