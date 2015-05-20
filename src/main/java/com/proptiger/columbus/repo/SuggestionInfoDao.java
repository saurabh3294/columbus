package com.proptiger.columbus.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.columbus.model.SuggestionInfo;

public interface SuggestionInfoDao extends PagingAndSortingRepository<SuggestionInfo, Integer> {
    
    public SuggestionInfo findByEntityTypeAndSuggestionType(String entityType, String suggestionType);

}
