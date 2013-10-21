package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proptiger.data.model.ProjectPaymentSchedule;

/**
 * @author Rajeev Pandey
 *
 */
public interface ProjectPaymentScheduleDao extends JpaRepository<ProjectPaymentSchedule, Integer>{

	public List<ProjectPaymentSchedule> findByProjectId(Integer projectId);
	
	@Query("select P from ProjectPaymentSchedule P where P.projectId = ? " +
			" group by P.installmentNumber order by P.installmentNumber ASC")
	public List<ProjectPaymentSchedule> findByProjectIdGroupByInstallmentNo(
			Integer projectId);
}
