package com.proptiger.data.notification.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.notification.model.SentNotificationLog;

public interface SentNotificationLogDao extends JpaRepository<SentNotificationLog, Integer>{

}
