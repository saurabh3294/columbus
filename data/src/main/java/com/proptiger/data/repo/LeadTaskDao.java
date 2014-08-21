package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.marketplace.LeadTask;

/**
 * 
 * @author azi
 * 
 */
public interface LeadTaskDao extends JpaRepository<LeadTask, Integer> {
}