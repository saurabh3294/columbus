package com.proptiger.data.repo.user;

import com.proptiger.data.model.user.User;

/**
 * 
 * @author azi
 * 
 */

public interface UserCustomDao {
    public User findUserDetailsByPrimaryEmail(String email);
}
