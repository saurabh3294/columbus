package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.ForumUser;

/**
 * @author Rajeev Pandey
 *
 */
public interface ForumUserDao extends JpaRepository<ForumUser, Integer>{

	public String findEmailByUserId(Integer userId);
}
