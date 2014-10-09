package com.proptiger.data.repo.marketplace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.marketplace.LeadTaskStatusReason;

/**
 * 
 * @author azi
 * 
 */
public interface LeadTaskStatusReasonDao extends JpaRepository<LeadTaskStatusReason, Integer> {
    public LeadTaskStatusReason findByReasonAndTaskStatusMappingId(String reason, int taskStatusMappingId);

    public List<LeadTaskStatusReason> findByTaskStatusMappingId(int taskStatusMappingId);
}
