package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.ext.search.PropertyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.model.Subscription;
import com.proptiger.data.model.portfolio.SubscriptionType;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.portfolio.SubscriptionDao;
import com.proptiger.data.repo.portfolio.SubscriptionTypeDao;
import com.proptiger.exception.BadRequestException;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionDao     subscriptionDao;

    @Autowired
    private SubscriptionTypeDao subscriptionTypeDao;

    @Transactional
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
            subscriptionsPresent = subscriptionDao.getSubscriptions(fiqlSelector);
        }
        catch (PropertyNotFoundException e) {
            throw new BadRequestException(ResponseErrorMessages.BAD_REQUEST);
        }
        List<Subscription> subscriptionsCreated = new ArrayList<Subscription>();

        if (subscriptionsPresent.size() < subscriptionTypes.length) {

            for (String subscriptionType : subscriptionTypes) {
                subscriptionsCreated.add(createUserSubscription(userId, tableId, tableName, subscriptionType));
            }
        }

        else {
            /*
             * As this method is transactional, Hence updating the model value
             * will result in updating of the value in the database when the
             * method will return.
             */
            for (Subscription subscriptionPresent : subscriptionsPresent) {
                if (!subscriptionPresent.getIsSubscribed().equals("1")) {

                    subscriptionPresent.setIsSubscribed("1");
                }
            }
            return subscriptionsPresent;
        }

        return subscriptionsCreated;
    }

    @Transactional
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

        return subscriptionDao.save(subscription);
    }

    public Subscription getUserSubscription(int userId) {
        return subscriptionDao.findByUserId(userId);
    }

    public SubscriptionType getSubscriptionTypeOnName(String name) {
        return subscriptionTypeDao.findByName(name);
    }
}
