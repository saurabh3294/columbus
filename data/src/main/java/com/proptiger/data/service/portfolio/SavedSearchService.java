package com.proptiger.data.service.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.portfolio.SavedSearch;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.portfolio.SavedSearchDao;
import com.proptiger.exception.ResourceAlreadyExistException;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class SavedSearchService {

	@Autowired
	private SavedSearchDao savedSearchDao;
	
	public List<SavedSearch> getUserSavedSearches(Selector selector, Integer userId){
		return savedSearchDao.getUserSavedSearches(selector, userId);
	}
	
	public SavedSearch setUserSearch(SavedSearch saveSearch, Integer userId){
		if(saveSearch.getName().isEmpty() || saveSearch.getSearchQuery().isEmpty())
			throw new IllegalArgumentException("Name or Search Query both should not be null.");
			
		SavedSearch alreadySavedSearch = savedSearchDao.findBySearchQueryAndUserIdOrNameAndUserId(saveSearch.getSearchQuery(), userId, saveSearch.getName(), userId);
		if(alreadySavedSearch != null)
			throw new ResourceAlreadyExistException("Name or Search Query Already Exists.");
		
		saveSearch.setUserId(userId);
		return savedSearchDao.save(saveSearch);
	}

    public void deleteSavedSearch(int savedSearchId) {
        savedSearchDao.delete(savedSearchId);
    }
}
