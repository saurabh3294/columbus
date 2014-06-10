package com.proptiger.data.repo.user;

import java.util.List;

import com.proptiger.data.model.user.SavedSearch;

public interface SavedSearchCustomDao {
    public List<SavedSearch> getUserSavedSearches(Integer userId);

}
