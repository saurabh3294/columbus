package com.proptiger.data.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.external.dto.LeadTaskDto;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.model.LeadTaskStatus;
import com.proptiger.data.model.MasterLeadTask;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.repo.LeadTaskDao;
import com.proptiger.data.repo.LeadTaskStatusDao;
import com.proptiger.data.repo.MasterLeadTaskDao;
import com.proptiger.exception.BadRequestException;

/**
 * 
 * @author azi
 * 
 */
@Service
public class LeadTaskService {
    @Autowired
    private LeadTaskDao       leadTaskDao;

    @Autowired
    private LeadTaskStatusDao leadTaskStatusDao;

    @Autowired
    private MasterLeadTaskDao masterLeadTaskDao;

    private static Logger     logger = LoggerFactory.getLogger(LeadTaskService.class);

    /**
     * function to update leadtask for a user
     * 
     * @param LeadTaskDto
     *            taskDto
     * @return
     */
    @Transactional
    public LeadTask updateTask(LeadTaskDto taskDto) {
        LeadTask leadTask = getLeadTaskFromLeadTaskDto(taskDto);
        LeadTask savedTask = leadTaskDao.findOne(leadTask.getId());
        if (isValidUpdate(leadTask)) {
            ExclusionAwareBeanUtilsBean beanUtilsBean = new ExclusionAwareBeanUtilsBean();
            try {
                beanUtilsBean.copyProperties(savedTask, leadTask);
                leadTaskDao.save(savedTask);
                if (savedTask.getNextTask() != null) {
                    leadTaskDao.save(savedTask.getNextTask());
                }
                return savedTask;
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                logger.debug("Error in bean copy");
            }
        }
        else {
            throw new BadRequestException();
        }
        return savedTask;
    }

    /**
     * function to convert user provided {@link LeadTaskDto} into
     * {@link LeadTask} model
     * 
     * @param LeadTaskDto
     *            leadTaskDto
     * @return
     */
    private LeadTask getLeadTaskFromLeadTaskDto(LeadTaskDto leadTaskDto) {
        LeadTask leadTask = new LeadTask();
        try {
            leadTask.setId(leadTaskDto.getId());
            leadTask.setLeadOfferId(leadTaskDto.getLeadOfferId());
            leadTask.setTaskStatusId(leadTaskStatusDao.getLeadTaskStatusFromTaskNameAndStatusName(
                    leadTaskDto.getTaskName(),
                    leadTaskDto.getTaskStatus()).getId());
            leadTask.setScheduledFor(leadTaskDto.getScheduledFor());
            leadTask.setCallDuration(leadTaskDto.getCallDuration());
            leadTask.setPerformedAt(leadTaskDto.getPerformedAt());
            leadTask.setNotes(leadTaskDto.getNotes());
            if (leadTaskDto.getNextTask() != null) {
                leadTask.setNextTask(getLeadTaskFromLeadTaskDto(leadTaskDto.getNextTask()));
            }
        }
        catch (Exception e) {
            logger.debug("Error in getLeadTaskFromLeadTaskDto: " + e);
            throw new BadRequestException(HttpStatus.BAD_REQUEST.toString(), HttpStatus.BAD_REQUEST.name());
        }
        return leadTask;
    }

    /**
     * validated if a update being requested is a valid one
     * 
     * @param leadTask
     * @return
     */
    @Transactional
    private boolean isValidUpdate(LeadTask leadTask) {
        boolean result = true;
        LeadTask savedLeadTask = leadTaskDao.findOne(leadTask.getId());
        LeadTaskStatus oldStatus = leadTaskStatusDao.findOne(savedLeadTask.getTaskStatusId());
        LeadTaskStatus newStatus = leadTaskStatusDao.findOne(leadTask.getTaskStatusId());

        if (leadTask.getLeadOfferId() != leadTask.getLeadOfferId()) {
            result = false;
        }
        else if (oldStatus.getMasterLeadTaskStatus().isComplete()) {
            result = false;
        }
        else if (oldStatus.getId() != newStatus.getId()) {
            if (oldStatus.getMasterTaskId() != newStatus.getMasterTaskId()) {
                result = false;
            }
            if (newStatus.getMasterLeadTaskStatus().isComplete() && leadTask.getPerformedAt() == null) {
                result = false;
            }
            else if (newStatus.getMasterLeadTaskStatus().isNextTaskRequired()) {
                if (leadTask.getNextTask() == null) {
                    result = false;
                }
                else if (!isValidNextTask(leadTask)) {
                    result = false;
                }
            }
            else if (!newStatus.getMasterLeadTaskStatus().isNextTaskRequired() && leadTask.getNextTask() != null) {
                result = false;
            }
        }
        return result;
    }

    /**
     * verifies whether next task is valid for the current task
     * 
     * @param LeadTask
     *            leadTask
     * @return Boolean
     */
    @Transactional
    private boolean isValidNextTask(LeadTask leadTask) {
        boolean isValid = true;
        LeadTask nextTask = leadTask.getNextTask();
        if (nextTask != null) {
            LeadTaskStatus nextTaskStatus = leadTaskStatusDao.findOne(nextTask.getTaskStatusId());
            if (leadTask.getLeadOfferId() != nextTask.getLeadOfferId()) {
                isValid = false;
            }
            else if (!isValidNextTaskType(leadTask)) {
                isValid = false;
            }
            else if (nextTaskStatus.getMasterLeadTaskStatus().isComplete()) {
                isValid = false;
            }
            else if (nextTask.getPerformedAt() != null) {
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * validates if next task type is valid depending upon previously performed
     * tasks and task hhierarchy
     * 
     * @param leadTask
     * @return
     */
    @Transactional
    private boolean isValidNextTaskType(LeadTask leadTask) {
        boolean result = false;
        LeadTaskStatus taskStatus = leadTaskStatusDao.findOne(leadTask.getTaskStatusId());
        LeadTaskStatus nextTaskStatus = leadTaskStatusDao.findOne(leadTask.getNextTask().getTaskStatusId());
        List<MasterLeadTask> incompleTasks = masterLeadTaskDao.getIncompleteMandatoryTasksWithLesserPriority(
                leadTask.getLeadOfferId(),
                nextTaskStatus.getMasterLeadTask().getPriority());
        if (incompleTasks.size() == 0) {
            result = true;
        }
        else if (incompleTasks.size() == 1 && incompleTasks.get(0).getId() == taskStatus.getMasterTaskId()) {
            result = true;
        }
        return result;
    }
}