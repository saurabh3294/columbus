package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.NotificationType;

public interface NotificationTypeDao extends PagingAndSortingRepository<NotificationType, Integer> {

    List<NotificationType> findByName(String name);

    @Query("SELECT N FROM NotificationType N WHERE N.name IN ?1")
    List<NotificationType> findByNames(List<String> names);

}
