package com.proptiger.data.service.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.Subscription;
import com.proptiger.data.model.portfolio.SubscriptionType;
import com.proptiger.data.repo.portfolio.SubscriptionDao;
import com.proptiger.data.repo.portfolio.SubscriptionTypeDao;

@Service
public class SubscriptionService {

	@Autowired
	private SubscriptionDao subscriptionDao;
	
	@Autowired
	private SubscriptionTypeDao subscriptionTypeDao;
	
	@Transactional
	public Subscription enableOrAddUserSubscription(int userId, int projectId, String tableName, String subscriptionType){
		Subscription alreadySubscribed = subscriptionDao.findByUserIdAndTableIdAndTableName(userId, projectId, tableName);
		if(alreadySubscribed != null)
		{
			/*
			 *  As this method is transactional, Hence updating the model value will result in
			 *  updating of the value in the database when the method will return.
			 */
			//if( !alreadySubscribed.getIsSubscribed().equals("1") ){
			alreadySubscribed.setIsSubscribed("1");
			//}

			return alreadySubscribed;
		}
		
		return createUserSubscription(userId, projectId, tableName, subscriptionType);
	}
	
	public Subscription createUserSubscription(int userId, int projectId, String tableName, String subscriptionType){
		SubscriptionType alreadySubscriptionType = getSubscriptionTypeOnName(subscriptionType);
		
		Subscription subscription = new Subscription();
		
		subscription.setUserId(userId);
		subscription.setTableName(tableName);
		subscription.setTableId(projectId);
		subscription.setSubscriptionTypeId( alreadySubscriptionType.getId() );
		
		return subscriptionDao.save(subscription);
	}
	
	public Subscription getUserSubscription(int userId){
		return subscriptionDao.findByUserId(userId);
	}
	
	public SubscriptionType getSubscriptionTypeOnName(String name){
		return subscriptionTypeDao.findByName(name);
	}
}
