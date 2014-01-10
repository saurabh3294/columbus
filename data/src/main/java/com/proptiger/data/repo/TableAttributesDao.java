package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.TableAttributes;

@Repository
public interface TableAttributesDao extends PagingAndSortingRepository<TableAttributes, Long>{
	List<TableAttributes> findByTableIdAndTableName(int tableId, String tableName);
}
