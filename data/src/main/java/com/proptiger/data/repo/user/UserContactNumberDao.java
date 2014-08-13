package com.proptiger.data.repo.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.user.UserContactNumber;

/**
 * 
 * @author azi
 * 
 */

public interface UserContactNumberDao extends JpaRepository<UserContactNumber, Integer> {
    public List<UserContactNumber> findByContactNumber(String contactNumber);
}
