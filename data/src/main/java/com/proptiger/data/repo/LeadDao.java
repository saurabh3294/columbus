package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Enquiry;

public interface LeadDao extends JpaRepository<Enquiry, Integer> {

}
