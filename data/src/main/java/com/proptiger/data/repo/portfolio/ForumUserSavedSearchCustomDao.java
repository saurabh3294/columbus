package com.proptiger.data.repo.portfolio;

import java.util.List;

import com.proptiger.data.model.portfolio.ForumUserSavedSearch;
import com.proptiger.data.pojo.Selector;

public interface ForumUserSavedSearchCustomDao {
	public List<ForumUserSavedSearch> getUserSavedSearches(Selector selector,
			Integer userId);

}
