package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.core.model.cms.VideoLinks;

@Repository
public interface VideoLinksDao extends PagingAndSortingRepository<VideoLinks, Integer> {
    public List<VideoLinks> findByTableIdAndTableName(int tableId, String tableName);
}
