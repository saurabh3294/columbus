package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.data.model.LocalityReviewComments;

/**
 * @author Rajeev Pandey
 *
 */
public interface LocalityReviewCustomDao {

    public PaginatedResponse<List<LocalityReviewComments>> getLocalityReview(FIQLSelector selector);
}
