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
import com.proptiger.data.model.Enquiry.EnquiryCustomDetails;

/**
 * 
 * @author mukand
 */
public interface EnquiryDao extends JpaRepository<Enquiry, Serializable> {
    public List<Enquiry> findByEmail(String email);

    @Query("select NEW com.proptiger.data.model.Enquiry$EnquiryCustomDetails(E.projectName, E.cityName, P.projectUrl, E.createdDate, B.name) " + " from  Enquiry E left join E.project P "
            + " left join P.builder B where P.version='Website' AND E.email=?1 AND E.localityId > 0 order by E.createdDate DESC ") 
    public List<EnquiryCustomDetails> findEnquiriesByEmail(String email);

    public List<Enquiry> findEnquiryByEmailAndProjectIdOrderByCreatedDateDesc(String email, Long projectId);
}
