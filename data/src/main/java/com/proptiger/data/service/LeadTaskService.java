package com.proptiger.data.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.external.dto.LeadTaskDto;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.LeadTaskStatus;
import com.proptiger.data.model.MasterLeadTask;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.LeadTaskDao;
import com.proptiger.data.repo.LeadTaskStatusDao;
import com.proptiger.data.repo.MasterLeadTaskDao;
import com.proptiger.data.util.DateUtil;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.SecurityContextUtils;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.ProAPIException;
import com.proptiger.exception.UnauthorizedException;

/**
 * 
 * @author azi
 * 
 */
@Service
public class LeadTaskService {
    @Autowired
    private LeadTaskDao         leadTaskDao;

    @Autowired
    private LeadTaskStatusDao   leadTaskStatusDao;

    @Autowired
    private MasterLeadTaskDao   masterLeadTaskDao;

    private static final int    defaultLeadTaskStatus = 1;

    private static final String defaultTaskSelection  = "";

    private static final String defaultTaskSorting    = "taskStatus.masterLeadTaskStatus.complete,-updatedAt";

    private static Logger       logger                = LoggerFactory.getLogger(LeadTaskService.class);

    /**
     * function to update leadtask for a user
     * 
     * @param LeadTaskDto
     *            taskDto
     * @return
     */
    @Transactional
    public LeadTask updateTask(LeadTaskDto taskDto) {
        ActiveUser user = SecurityContextUtils.getLoggedInUser();
        LeadTask savedTask = leadTaskDao.findOne(taskDto.getId());
        if (savedTask.getLeadOffer().getAgentId() != Integer.parseInt(user.getUserId())) {
            throw new UnauthorizedException();
        }

        LeadTask leadTask = getLeadTaskFromLeadTaskDto(taskDto);
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

    /**
     * 
     * 
     * @param leadOffer
     * @return
     */
    @Transactional
    private LeadTask createDefaultLeadTaskForLeadOffer(LeadOffer leadOffer) {
        LeadTask leadTask = new LeadTask();
        int leadOfferId = leadOffer.getId();
        List<LeadTask> leadTasks = leadTaskDao.findByLeadOfferId(leadOfferId);
        if (leadTasks.isEmpty()) {
            leadTask.setTaskStatusId(defaultLeadTaskStatus);
            leadTask.setScheduledFor(DateUtil.getWorkingTimeAddedIntoDate(new Date(), PropertyReader
                    .getRequiredPropertyAsType(PropertyKeys.MARKETPLACE_DEFAULT_TASK_DURATION, Integer.class)));
            leadTask.setLeadOfferId(leadOfferId);
            leadTask = leadTaskDao.save(leadTask);
        }
        else {
            throw new ProAPIException("Lead Task Already Exists");
        }
        return leadTask;
    }

    /**
     * 
     * @param selector
     * @param userId
     * @return
     */
    public PaginatedResponse<List<LeadTask>> getLeadTasksForUser(FIQLSelector selector, int userId) {
        applyDefaultsInFIQL(selector);

        Pageable pageable = new LimitOffsetPageRequest(
                selector.getStart(),
                selector.getRows(),
                selector.getDataDomainSort());

        PaginatedResponse<List<LeadTask>> response = new PaginatedResponse<>();
        response.setTotalCount(leadTaskDao.getLeadTaskCountForUser(userId));
        response.setResults(leadTaskDao.getLeadTasksForUser(userId, pageable));

        return response;
    }

    /**
     * applies defaults in {@link FIQLSelector} for get task apis
     * 
     * @param selector
     * @return
     */
    private FIQLSelector applyDefaultsInFIQL(FIQLSelector selector) {
        if (selector.getFields() == null) {
            selector.setFields(defaultTaskSelection);
        }
        if (selector.getSort() == null) {
            selector.setSort(defaultTaskSorting);
        }
        return selector;
    }
}