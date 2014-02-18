/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.NearLocalities;

/**
 * 
 * @author mukand
 */
public interface NearLocalitiesDao extends PagingAndSortingRepository<NearLocalities, Serializable> {

    @Query("SELECT nL FROM NearLocalities NL JOIN FETCH NL.locality L JOIN FETCH L.suburb S JOIN FETCH S.city C WHERE NL.mainLocality=?1 ORDER BY NL.distance ASC")
    public List<NearLocalities> findByMainLocalityOrderByDistanceAsc(Integer mainLocality, Pageable pageable);

}
