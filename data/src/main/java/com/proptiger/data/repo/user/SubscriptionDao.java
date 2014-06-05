package com.proptiger.data.repo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Subscription;

public interface SubscriptionDao extends JpaRepository<Subscription, Integer>, SubscriptionCustomDao {

    public Subscription findByUserId(int userId);

}
