package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.LocalityReview;

/**
 * Dao class to handle CRUD operation for Locality review
 * 
 * @author Rajeev Pandey
 *
 */
@Repository
public interface LocalityReviewDao extends JpaRepository<LocalityReview, Long>{

	/**
	 * Finds all review for a locality based on locality id
	 * 
	 * @param localityId
	 * @return
	 */
	public List<LocalityReview> findReviewsByLocalityId(Long localityId);
}
