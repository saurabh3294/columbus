package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.SubscriptionPermission;

public interface SubscriptionPermissionDao extends JpaRepository<SubscriptionPermission, Integer> {

    public List<SubscriptionPermission> findBySubscriptionIdIn(List<Integer> subscriptionIdList);
}