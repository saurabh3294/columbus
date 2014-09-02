package com.proptiger.data.notification.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.model.NotificationMedium;

public interface NotificationMediumDao extends PagingAndSortingRepository<NotificationMedium, Integer> {

    List<NotificationMedium> findByName(MediumType name);   
    
}
