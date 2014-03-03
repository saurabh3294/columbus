/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.Enquiry;

/**
 * 
 * @author mukand
 */
public interface EnquiryDao extends JpaRepository<Enquiry, Serializable> {
    public List<Enquiry> findByEmail(String email);

    @Query("select E.projectName, E.cityName, P.projectUrl, E.createdDate " + " from  Enquiry E left join E.project P "
            + " where E.email=?1 AND E.localityId > 0 order by E.createdDate DESC")
    public List<Object[]> findEnquiriesByEmail(String email);

    public List<Enquiry> findEnquiryByEmailAndProjectIdOrderByCreatedDateDesc(String email, Long projectId);
}
