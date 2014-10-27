package com.proptiger.data.repo.user;

import java.util.List;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.model.Subscription;

public interface SubscriptionCustomDao {
    public List<Subscription> getSubscriptions(FIQLSelector fiqlSelector);

}
