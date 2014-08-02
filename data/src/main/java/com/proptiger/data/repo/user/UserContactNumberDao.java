package com.proptiger.data.repo.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.user.UserContactNumber;

/**
 * 
 * @author azi
 * 
 */

public interface UserContactNumberDao extends PagingAndSortingRepository<UserContactNumber, Integer> {

}
