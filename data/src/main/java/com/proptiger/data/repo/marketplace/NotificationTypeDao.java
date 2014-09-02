package com.proptiger.data.repo.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.marketplace.NotificationType;

/**
 * 
 * @author azi
 * 
 */
public interface NotificationTypeDao extends JpaRepository<NotificationType, Integer> {
}
