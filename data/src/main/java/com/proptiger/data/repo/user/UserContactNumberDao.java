package com.proptiger.data.repo.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import com.proptiger.data.model.user.UserContactNumber;

/**
 * 
 * @author azi
 * 
 */

public interface UserContactNumberDao extends JpaRepository<UserContactNumber, Integer> {
    public List<UserContactNumber> findByContactNumber(String contactNumber);

    @Modifying
    @Query("UPDATE UserContactNumber SET priority=priority+1 WHERE userId = ?1")
    public int incrementPriorityForUser(int userId);

    @Query("select max(UCN.priority) from UserContactNumber UCN where UCN.userId = ?1")
    public int findMaxPriorityByUserId(int id);
}
