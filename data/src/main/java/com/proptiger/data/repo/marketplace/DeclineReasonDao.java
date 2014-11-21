package com.proptiger.data.repo.marketplace;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.marketplace.DeclineReason;

public interface DeclineReasonDao extends JpaRepository<DeclineReason, Integer> {
    public DeclineReason findById(int id); 
}
