package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.enums.AndroidApplication;
import com.proptiger.data.model.GCMUser;

/**
 * 
 * @author sahil garg
 */
public interface GCMUserDao extends JpaRepository<GCMUser, Integer> {

    public List<GCMUser> findByEmail(String email);

    public List<GCMUser> findByAppIdentifierAndUserIdAndLoginStatus(
            AndroidApplication appIdentifier,
            Integer userId,
            Boolean loginStatus);
    
    public List<GCMUser> findByUserIdAndLoginStatus(
            Integer userId,
            Boolean loginStatus);

    public List<GCMUser> findByAppIdentifier(AndroidApplication appIdentifier);
    
    public List<GCMUser> findByGcmRegId(String gcmRegId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM GCMUser WHERE gcmRegId = ?1")
    public void deleteByGcmRegId(String gcmRegId);

}
