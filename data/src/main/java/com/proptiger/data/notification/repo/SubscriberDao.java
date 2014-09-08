package com.proptiger.data.notification.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.model.Subscriber;

public interface SubscriberDao extends PagingAndSortingRepository<Subscriber, Integer> {
    
    @Modifying
    @Query("Update Subscriber S set S.lastEventDate=?2 where S.id=?1 ")
    public Integer updateLastEventDateById(int id, Date lastEventDate);
    
}