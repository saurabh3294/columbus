package com.proptiger.data.repo.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.user.User;
import com.proptiger.data.model.user.UserEmail;

/**
 * 
 * @author azi
 * 
 */

public interface UserDao extends PagingAndSortingRepository<User, Integer>, UserCustomDao {
    @Query("SELECT U FROM User U join U.emails E WHERE U.id=E.userId AND " + " E.email = ?1 AND E.priority = "
            + UserEmail.primaryEmailPriority)
    public User findByPrimaryEmail(String email);
}
