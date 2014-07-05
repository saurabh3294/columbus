package com.proptiger.data.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.APIAccessLog;

public interface APIAccessLogDao extends JpaRepository<APIAccessLog, Integer> {

    public APIAccessLog findByUserId(Integer userId);

    public APIAccessLog findByAccessHash(String accessHash);
    
    public List<APIAccessLog> findByAccessHashIn(Set<String> accessHashList);
}
