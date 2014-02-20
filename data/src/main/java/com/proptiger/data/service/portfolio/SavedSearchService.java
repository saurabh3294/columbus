package com.proptiger.data.service.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proptiger.data.constants.ResponseCodes;
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

    /**
     * This method will get the save Searches Info based on the user Id. It will
     * filter the responses using selector.
     * 
     * @param selector
     * @param userId
     * @return
     */
    public List<SavedSearch> getUserSavedSearches(Integer userId) {
        return savedSearchDao.getUserSavedSearches(userId);
    }

    /**
     * This method will save the user searches. It will check whether name or
     * searchQuery already exists. If it not then it will save and return the
     * saved object.
     * 
     * @param saveSearch
     * @param userId
     * @return
     */
    public SavedSearch setUserSearch(SavedSearch saveSearch, Integer userId) {
        String searchQuery = saveSearch.getSearchQuery();
        String name = saveSearch.getName();
        if (name == null || name.isEmpty() || searchQuery == null || searchQuery.isEmpty())
            throw new IllegalArgumentException("Empty name or search query");

        SavedSearch alreadySavedSearch = savedSearchDao.findBySearchQueryAndUserId(searchQuery, userId);
        if (alreadySavedSearch != null) {
            throw new ResourceAlreadyExistException(
                    "Search query already exists",
                    ResponseCodes.SEARCH_QUERY_ALREADY_EXISTS,
                    alreadySavedSearch);
        }

        alreadySavedSearch = savedSearchDao.findByNameAndUserId(name, userId);
        if (alreadySavedSearch != null) {
            throw new ResourceAlreadyExistException(
                    "Name already exists",
                    ResponseCodes.NAME_ALREADY_EXISTS,
                    alreadySavedSearch);
        }

        saveSearch.setUserId(userId);
        return savedSearchDao.save(saveSearch);
    }

    /**
     * It will delete the save search based on it Id.
     * 
     * @param savedSearchId
     */
    public void deleteSavedSearch(int savedSearchId) {
        savedSearchDao.delete(savedSearchId);
    }
}
