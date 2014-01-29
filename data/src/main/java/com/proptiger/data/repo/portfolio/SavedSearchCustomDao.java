package com.proptiger.data.repo.portfolio;

import java.util.List;

import com.proptiger.data.model.portfolio.SavedSearch;
import com.proptiger.data.pojo.Selector;

public interface SavedSearchCustomDao {
	public List<SavedSearch> getUserSavedSearches(Integer userId);

}
