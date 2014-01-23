package com.proptiger.data.repo.portfolio;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.Subscription;

@Repository
public interface SubscriptionDao extends PagingAndSortingRepository<Subscription, Integer> {
	
	public Subscription findByUserId(int userId);
	public Subscription findByUserIdAndTableIdAndTableName(int userId, int tableId, String tableName);
}
