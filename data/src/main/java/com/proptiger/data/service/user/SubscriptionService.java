package com.proptiger.data.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxrs.ext.search.PropertyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.data.model.Subscription;
import com.proptiger.data.model.user.SubscriptionType;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.user.SubscriptionDao;
import com.proptiger.data.repo.user.SubscriptionTypeDao;
import com.proptiger.exception.BadRequestException;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionDao     subscriptionDao;

    @Autowired
    private SubscriptionTypeDao subscriptionTypeDao;

    public List<Subscription> enableOrAddUserSubscription(
            int userId,
            int tableId,
            String tableName,
            String... subscriptionTypes) {
        FIQLSelector fiqlSelector = new FIQLSelector();

        for (String subscriptionType : subscriptionTypes) {
            fiqlSelector.addOrConditionToFilter("subscriptionType.name==" + subscriptionType);
        }
        fiqlSelector.addAndConditionToFilter("userId==" + userId);
        fiqlSelector.addAndConditionToFilter("tableId==" + tableId);
        fiqlSelector.addAndConditionToFilter("tableName==" + tableName);

        List<Subscription> subscriptionsPresent;
        try {
            subscriptionsPresent = subscriptionDao.getSubscriptions(fiqlSelector);   // Getting old Subscriptions for same tableId, 
        }
        catch (PropertyNotFoundException e) {
            throw new BadRequestException(ResponseErrorMessages.BAD_REQUEST);
        }
        
        List<Subscription> subscriptionsCreated = new ArrayList<Subscription>();
        Map<String, Subscription> subscriptionAlreadyPresentMap = new HashMap<String, Subscription>();

        for (Subscription subscriptionPresent : subscriptionsPresent) {
            subscriptionAlreadyPresentMap.put(subscriptionPresent.getSubscriptionType().getName().toLowerCase(), subscriptionPresent);
        }
        for (String subscriptionType : subscriptionTypes) {
            if (subscriptionAlreadyPresentMap.containsKey(subscriptionType)) {
                subscriptionAlreadyPresentMap.get(subscriptionType).setIsSubscribed("1");  //Updating old subscriptions
            }
            else {
                subscriptionsCreated.add(createUserSubscription(userId, tableId, tableName, subscriptionType));  // Creating new Subscriptions
            }
        }
        subscriptionsCreated.addAll(subscriptionsPresent);
        return subscriptionDao.save(subscriptionsCreated);
    }

    public List<Subscription> disableSubscription(
            int userId,
            int tableId,
            String tableName,
            String... subscriptionTypes) {

        FIQLSelector fiqlSelector = new FIQLSelector();
        for (String subscriptionType : subscriptionTypes) {
            fiqlSelector.addOrConditionToFilter("subscriptionType.name==" + subscriptionType);
        }
        fiqlSelector.addAndConditionToFilter("userId==" + userId);
        fiqlSelector.addAndConditionToFilter("tableId==" + tableId);
        fiqlSelector.addAndConditionToFilter("tableName==" + tableName);
        
        List<Subscription> subscriptionsPresent;
        try {
            subscriptionsPresent = subscriptionDao.getSubscriptions(fiqlSelector);
        }
        catch (PropertyNotFoundException e) {
            throw new BadRequestException(ResponseErrorMessages.BAD_REQUEST);
        }

        if (subscriptionsPresent != null) {
            /*  
             * As this method is transactional, Hence updating the model value
             * will result in updating of the value in the database when the
             * method will return.
             */
            for (Subscription subscriptionPresent : subscriptionsPresent) {
                subscriptionPresent.setIsSubscribed("0");
            }

        }
        return subscriptionDao.save(subscriptionsPresent);
    }

    public Subscription createUserSubscription(int userId, int tableId, String tableName, String subscriptionType) {
        SubscriptionType alreadySubscriptionType = getSubscriptionTypeOnName(subscriptionType);

        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setTableName(tableName);
        subscription.setTableId(tableId);
        subscription.setSubscriptionTypeId(alreadySubscriptionType.getId());

        return subscription;
    }

    public Subscription getUserSubscription(int userId) {
        return subscriptionDao.findByUserId(userId);
    }

    public SubscriptionType getSubscriptionTypeOnName(String name) {
        return subscriptionTypeDao.findByName(name);
    }
}
