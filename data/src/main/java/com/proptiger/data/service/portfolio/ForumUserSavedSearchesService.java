package com.proptiger.data.service.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.model.portfolio.ForumUserSavedSearch;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.portfolio.ForumUserSavedSearchDao;

@Component
public class ForumUserSavedSearchesService {

	@Autowired
	private ForumUserSavedSearchDao savedSearchDao;
	
	public List<ForumUserSavedSearch> getUserSavedSearches(Selector selector, Integer userId){
		return savedSearchDao.getUserSavedSearches(selector, userId);
	}
}
