package com.proptiger.data.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.EnquiryAttributes;

public interface EnquiryAttributesDao extends JpaRepository<EnquiryAttributes, Serializable> {

}
