package com.proptiger.data.event.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.DBRawEventTableLog;

public interface DBRawEventTableLogDao extends PagingAndSortingRepository<DBRawEventTableLog, Integer> {
    
    @Modifying
    @Query("Update DBRawEventTableLog D set D.lastTransactionKeyValue=?2 where D.id=?1 ")
    public Integer updateLastTransactionKeyValueById(int id, Long transactionId);
}
