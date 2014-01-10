package com.proptiger.data.service.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.portfolio.ForumUserSavedSearch;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.portfolio.ForumUserSavedSearchDao;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class ForumUserSavedSearchesService {

	@Autowired
	private ForumUserSavedSearchDao savedSearchDao;
	
	public List<ForumUserSavedSearch> getUserSavedSearches(Selector selector, Integer userId){
		return savedSearchDao.getUserSavedSearches(selector, userId);
	}
	
	public int setUserSearch(ForumUserSavedSearch saveSearch, Integer userId){
		if(saveSearch.getName().isEmpty() || saveSearch.getSearchQuery().isEmpty())
			return -1;
		
		ForumUserSavedSearch alreadySavedSearch = savedSearchDao.findBySearchQueryAndUserIdOrNameAndUserId(saveSearch.getSearchQuery(), userId, saveSearch.getName(), userId);
		if(alreadySavedSearch != null)
			return 0;
		
		saveSearch.setUserId(userId);
		return savedSearchDao.save(saveSearch)!= null? 1: -2;
	}
}
