package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.model.ProjectPaymentSchedule;
import com.proptiger.data.repo.ProjectPaymentScheduleDao;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * @author Rajeev Pandey
 * 
 */
@Service
public class ProjectPaymentScheduleService {

    @Autowired
    private ProjectPaymentScheduleDao paymentScheduleDao;

    public List<ProjectPaymentSchedule> getProjectPaymentSchedule(Integer projectId) {
        List<ProjectPaymentSchedule> list = paymentScheduleDao.findByProjectIdGroupByInstallmentNo(projectId);
        if (list == null || list.size() == 0) {
            throw new ResourceNotAvailableException(ResourceType.PROJECT_PAYMENT_SCHEDULE, ResourceTypeAction.GET);
        }
        return list;
    }

}
