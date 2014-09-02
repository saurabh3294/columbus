package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.GCMUsers;

/**
 * 
 * @author sahil garg
 */
public interface GCMUsersDao extends JpaRepository<GCMUsers, Integer> {

    public List<GCMUsers> findByEmail(String email);

}
