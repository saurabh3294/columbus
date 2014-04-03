package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.LocalityReviewComments;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.service.pojo.PaginatedResponse;

/**
 * @author Rajeev Pandey
 *
 */
public interface LocalityReviewCustomDao {

    public PaginatedResponse<List<LocalityReviewComments>> getLocalityReview(FIQLSelector selector);
}
