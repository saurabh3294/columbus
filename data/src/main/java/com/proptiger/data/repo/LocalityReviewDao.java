package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.LocalityReview;

public interface LocalityReviewDao extends JpaRepository<LocalityReview, Long>{

	public List<LocalityReview> findReviewsByLocalityId(Long localityId);
}
