/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Enquiry;

/**
 *
 * @author mukand
 */
public interface EnquiryDao extends JpaRepository<Enquiry, Serializable>{
    
}
