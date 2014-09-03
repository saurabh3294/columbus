package com.proptiger.data.repo.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.marketplace.MarketplaceNotificationType;

/**
 * 
 * @author azi
 * 
 */
public interface MarketplaceNotificationTypeDao extends JpaRepository<MarketplaceNotificationType, Integer> {
}
