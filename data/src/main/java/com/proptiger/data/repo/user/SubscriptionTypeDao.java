package com.proptiger.data.repo.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.user.SubscriptionType;

@Repository
public interface SubscriptionTypeDao extends PagingAndSortingRepository<SubscriptionType, Integer> {
    public SubscriptionType findByName(String name);
}
