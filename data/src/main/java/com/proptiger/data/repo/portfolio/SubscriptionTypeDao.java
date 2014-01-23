package com.proptiger.data.repo.portfolio;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.portfolio.SubscriptionType;

@Repository
public interface SubscriptionTypeDao extends PagingAndSortingRepository<SubscriptionType, Integer> {
	public SubscriptionType findByName(String name);
}
