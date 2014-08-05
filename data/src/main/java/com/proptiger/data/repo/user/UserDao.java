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
    @Query("SELECT U FROM User U join U.emails E WHERE " + " E.email = ?1 AND E.priority = "
            + UserEmail.primaryEmailPriority)
    public User findByPrimaryEmail(String email);

    @Query("SELECT U FROM User U join U.emails E WHERE " + " E.email = ?1")
    public User findByEmail(String email);

    @Query("SELECT U FROM User U join U.userAuthProviderDetails APD WHERE " + " APD.providerId = ?1 AND APD.providerUserId = ?2")
    public User findByProviderIdAndProviderUserId(int providerId, String providerUserId);

    @Query("SELECT U FROM User U join U.contactNumbers CN Left join U.emails E WHERE " + " CN.contactNumber = ?1 AND E.email is null")
    public User findByContactNumberWithoutEmail(String contactNumber);

    @Query("select U from User U join U.contactNumbers CN join U.emails E where (E.email = ?1 or CN.contactNumber = ?2)")
    public User findByPrimaryEmailOrPhone(String email,String contactNumber);
    
    @Query("select U from User U join U.contactNumbers CN where (CN.contactNumber = ?1)")
    public User findByPhone(String contactNumber);




}
