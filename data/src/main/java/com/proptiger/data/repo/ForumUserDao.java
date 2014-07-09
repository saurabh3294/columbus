package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.ForumUser.WhoAmIDetail;

/**
 * @author Rajeev Pandey
 * @author Azi
 * 
 */
public interface ForumUserDao extends JpaRepository<ForumUser, Integer> {

    @Query("select U.email from ForumUser U where U.userId=?1")
    public String findEmailByUserId(Integer userId);

    public ForumUser findByEmail(String email);

    public ForumUser findByEmailAndProvider(String email, String provider);

    public ForumUser findByUserId(int userId);

    @Query(" SELECT NEW com.proptiger.data.model.ForumUser$WhoAmIDetail(FU.username, FU.fbImageUrl) " + " FROM ForumUser FU WHERE FU.userId = ?1")
    public WhoAmIDetail getWhoAmIDetail(Integer userIdentifier);

    public List<ForumUser> findByProviderAndProviderid(String providerId, String providerUserId);

}
