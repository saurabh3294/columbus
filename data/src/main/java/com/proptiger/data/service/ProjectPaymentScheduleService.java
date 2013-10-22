package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ProjectPaymentSchedule;
import com.proptiger.data.repo.ProjectPaymentScheduleDao;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class ProjectPaymentScheduleService {

	@Autowired
	private ProjectPaymentScheduleDao paymentScheduleDao;

	public List<ProjectPaymentSchedule> getProjectPaymentSchedule(Integer projectId){
		return paymentScheduleDao.findByProjectIdGroupByInstallmentNo(projectId);
	}
	
	
}
