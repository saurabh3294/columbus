package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Enquiry;

public interface LeadEnquiryDao extends JpaRepository<Enquiry, Integer> {

}
