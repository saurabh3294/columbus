package com.proptiger.data.repo.user;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.model.filter.AbstractQueryBuilder;
import com.proptiger.data.model.filter.JPAQueryBuilder;
import com.proptiger.data.model.user.SavedSearch;

public class SavedSearchDaoImpl {
    @Autowired
    private EntityManagerFactory emf;

    /**
     * This method takes a selector object and form a database query to find
     * user saved searches
     * 
     * @param selector
     * @param userId
     * @return List<ForumUserSavedSearch>
     */
    public List<SavedSearch> getUserSavedSearches(Integer userId) {
        AbstractQueryBuilder<SavedSearch> queryBuilder = new JPAQueryBuilder<SavedSearch>(
                emf.createEntityManager(),
                SavedSearch.class);
        queryBuilder.buildQuery(new FIQLSelector().setFilters("userId==" + userId));
        return queryBuilder.retrieveResults();
    }

}