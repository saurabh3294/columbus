package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
public interface LocalityReviewCustomDao {

    public PaginatedResponse<List<LocalityReviewComments>> getLocalityReview(FIQLSelector selector);
}
