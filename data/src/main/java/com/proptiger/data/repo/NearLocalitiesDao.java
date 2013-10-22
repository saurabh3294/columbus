/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import com.proptiger.data.model.NearLocalities;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author mukand
 */
public interface NearLocalitiesDao extends PagingAndSortingRepository<NearLocalities, Serializable>{
    public List<NearLocalities> findByMainLocalityOrderByDistanceAsc(Integer mainLocality, Pageable pageable);
    
}
