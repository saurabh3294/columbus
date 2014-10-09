package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectDiscussionSubscription;

/**
 * 
 * @author azi
 * 
 */

public interface ProjectDiscussionSubscriptionDao extends
        PagingAndSortingRepository<ProjectDiscussionSubscription, Integer> {
}
