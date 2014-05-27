package com.proptiger.data.repo.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.UserDetail;

/**
 * B2b User Detail DAO
 * 
 * @author Azitabh Ajit
 * 
 */

public interface UserDetailDao extends PagingAndSortingRepository<UserDetail, Integer> {
}
