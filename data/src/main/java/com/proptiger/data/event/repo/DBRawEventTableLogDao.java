package com.proptiger.data.event.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.event.model.DBRawEventTableLog;

public interface DBRawEventTableLogDao extends PagingAndSortingRepository<DBRawEventTableLog, Integer> {
    
    @Modifying
    @Query("Update DBRawEventTableLog D set D.lastTransactionKeyValue=?2 where D.id=?1 ")
    @Transactional
    public Integer updateLastTransactionKeyValueById(int id, Long transactionId);
}
