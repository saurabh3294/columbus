package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.ProjectBanks;

public interface ProjectBanksDao extends JpaRepository<ProjectBanks, Integer>{

	@Query("select PB.bankId from ProjectBanks PB where PB.projectId=?1")
	public List<Integer> findBankIdByProjectId(Integer projectId);
}
