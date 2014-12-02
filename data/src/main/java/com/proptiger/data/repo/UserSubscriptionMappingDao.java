package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.core.model.proptiger.UserSubscriptionMapping;

public interface UserSubscriptionMappingDao extends JpaRepository<UserSubscriptionMapping, Integer> {

   public UserSubscriptionMapping findByUserId(int userId);
   
   public List<UserSubscriptionMapping> findAllByUserId(int userId);
   
   public UserSubscriptionMapping findById(int id);

   //@Query("select CU from CompanyUser CU join fetch CU.companyCoverages CC join fetch CC.locality L where CU.userId = ?1")

   //@Query("SELECT CS from UserSubscriptionMappings USM join fetch USM.subscription CS where CS.userId = ?1")
   //public List<CompanySubscription> getCompanySubscriptionsByUserId();
   
}