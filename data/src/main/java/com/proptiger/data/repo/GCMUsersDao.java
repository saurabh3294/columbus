package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.enums.AndroidApplication;
import com.proptiger.data.model.GCMUsers;

/**
 * 
 * @author sahil garg
 */
public interface GCMUsersDao extends JpaRepository<GCMUsers, Integer> {

    public List<GCMUsers> findByEmail(String email);

    public List<GCMUsers> findByAppIdentifierAndUserIdAndLoginStatus(
            AndroidApplication appIdentifier,
            Integer userId,
            Boolean loginStatus);
    
    public List<GCMUsers> findByUserIdAndLoginStatus(
            Integer userId,
            Boolean loginStatus);

    public List<GCMUsers> findByAppIdentifier(AndroidApplication appIdentifier);

}
