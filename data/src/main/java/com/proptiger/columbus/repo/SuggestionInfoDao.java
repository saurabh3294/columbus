package com.proptiger.columbus.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.columbus.model.SuggestionInfo;

public interface SuggestionInfoDao extends PagingAndSortingRepository<SuggestionInfo, Integer> {
    
    @Query("SELECT SI FROM  SuggestionInfo SI JOIN SI.suggestionObjectType ST WHERE SI.entityTypeId = ?1 AND ST.suggestionType = ?2")
    public SuggestionInfo findByEntityTypeIdAndSuggestionType(int entityTypeId, String suggestionType);

}
