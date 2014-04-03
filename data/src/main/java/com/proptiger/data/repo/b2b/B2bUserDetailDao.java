package com.proptiger.data.repo.b2b;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.b2b.B2bUserDetail;

/**
 * B2b User Detail DAO
 * 
 * @author Azitabh Ajit
 * 
 */

public interface B2bUserDetailDao extends PagingAndSortingRepository<B2bUserDetail, Integer> {
}
