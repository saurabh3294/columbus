package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.UserSubscriptionMapping;

public interface UserSubscriptionMappingDao extends JpaRepository<UserSubscriptionMapping, Integer> {

   public UserSubscriptionMapping findByUserId(int userId);
   
   public List<UserSubscriptionMapping> findAllByUserId(int userId);
   
}