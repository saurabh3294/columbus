package com.proptiger.data.repo.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.user.User;

/**
 * 
 * @author azi
 * 
 */

public interface UserDao extends PagingAndSortingRepository<User, Integer>, UserCustomDao {
    public User findByEmail(String email);

    @Query("SELECT U FROM User U join U.userAuthProviderDetails APD WHERE " + " APD.providerId = ?1 AND APD.providerUserId = ?2")
    public User findByProviderIdAndProviderUserId(int providerId, String providerUserId);

    @Query("SELECT U FROM User U join U.contactNumbers CN Left join U.emails E WHERE " + " CN.contactNumber = ?1 AND E.email is null")
    public User findByContactNumberWithoutEmail(String contactNumber);
}
