package com.proptiger.data.repo.portfolio;

import java.util.List;

import com.proptiger.data.model.Subscription;
import com.proptiger.data.pojo.FIQLSelector;

public interface SubscriptionCustomDao {
    public List<Subscription> getSubscriptions(FIQLSelector fiqlSelector);

}
