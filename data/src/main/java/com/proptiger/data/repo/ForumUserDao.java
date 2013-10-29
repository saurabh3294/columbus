package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.ForumUser;

/**
 * @author Rajeev Pandey
 *
 */
public interface ForumUserDao extends JpaRepository<ForumUser, Integer>{

	@Query("select U.email from ForumUser U where U.userId=?1")
	public String findEmailByUserId(Integer userId);
}
