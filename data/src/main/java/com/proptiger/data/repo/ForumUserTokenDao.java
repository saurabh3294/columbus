package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.ForumUserToken;

/**
 * @author Rajeev Pandey
 *
 */
public interface ForumUserTokenDao extends JpaRepository<ForumUserToken, Integer>{
    
    public ForumUserToken findByToken(String token);

}
