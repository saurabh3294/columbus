package com.proptiger.data.repo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.core.model.user.MetaUserAttributes;

/**
 * @author Nikhil Singhal
 */

public interface MetaUserAttributesDao extends JpaRepository<MetaUserAttributes, Integer> {
    
}
