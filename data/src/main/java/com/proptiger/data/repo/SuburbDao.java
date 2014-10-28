/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.core.model.cms.Suburb;

/**
 * 
 * @author mukand
 */
public interface SuburbDao extends PagingAndSortingRepository<Suburb, Integer>, SuburbCustomDao {
    
}
