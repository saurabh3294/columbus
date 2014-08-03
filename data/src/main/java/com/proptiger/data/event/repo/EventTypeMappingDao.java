package com.proptiger.data.event.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.event.model.EventType;
import com.proptiger.data.event.model.EventTypeMapping;

public interface EventTypeMappingDao extends PagingAndSortingRepository<EventTypeMapping, Integer> {
    public List<EventTypeMapping> findByEventTypeId(Integer eventTypeId);
    public List<EventType> getEventTypesForInsertDBOperation(String hostName, String dbName, String tableName);
    
    public List<EventType> getEventTypesForDeleteDBOperation(String hostName, String dbName, String tableName);
    
    public List<EventType> getEventTypesForUpdateDBOperation(String hostName, String dbName, String tableName, String attributeName);
    
    
}
