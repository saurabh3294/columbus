package com.proptiger.data.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.external.dto.LeadTaskDto;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.LeadTaskStatus;
import com.proptiger.data.model.MasterLeadTask;
import com.proptiger.data.model.marketplace.LeadOffer;
import com.proptiger.data.model.marketplace.LeadOfferedListing;
import com.proptiger.data.model.marketplace.LeadTask;
import com.proptiger.data.model.marketplace.LeadTaskStatusReason;
import com.proptiger.data.model.marketplace.TaskOfferedListingMapping;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.LeadTaskDao;
import com.proptiger.data.repo.LeadTaskStatusDao;
import com.proptiger.data.repo.MasterLeadTaskDao;
import com.proptiger.data.repo.marketplace.LeadOfferedListingDao;
import com.proptiger.data.repo.marketplace.LeadTaskStatusReasonDao;
import com.proptiger.data.repo.marketplace.TaskOfferedListingMappingDao;
import com.proptiger.data.service.marketplace.LeadOfferService;
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
    private LeadTaskDao                  leadTaskDao;

    @Autowired
    private LeadTaskStatusDao            leadTaskStatusDao;

    @Autowired
    private MasterLeadTaskDao            masterLeadTaskDao;

    @Autowired
    private LeadOfferService             leadOfferService;

    @Autowired
    private LeadTaskStatusReasonDao      taskStatusReasonDao;

    private static final int             offerDefaultLeadTaskStatusMappingId = 1;

    private static final String          newTaskDefaultStatus                = "Scheduled";

    private static final String          defaultTaskSelection                = "id";

    private static final String          defaultTaskSorting                  = "taskStatus.masterLeadTaskStatus.complete,-updatedAt";

    private static Logger                logger                              = LoggerFactory
                                                                                     .getLogger(LeadTaskService.class);

    @Autowired
    private LeadOfferedListingDao        offeredListingDao;

    @Autowired
    private TaskOfferedListingMappingDao taskOfferedListingMappingDao;

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
        int currentTaskId = taskDto.getId();
        int nextTaskId = 0;
        LeadTask savedTask = leadTaskDao.findOne(currentTaskId);
        if (savedTask.getLeadOffer().getAgentId() != Integer.parseInt(user.getUserId())) {
            throw new UnauthorizedException();
        }

        LeadTask leadTask = getLeadTaskFromLeadTaskDto(taskDto);
        LeadTaskStatus taskStatus = leadTaskStatusDao.findOne(leadTask.getTaskStatusId());

        if (isValidUpdate(leadTask)) {
            ExclusionAwareBeanUtilsBean beanUtilsBean = new ExclusionAwareBeanUtilsBean();
            try {
                beanUtilsBean.copyProperties(savedTask, leadTask);
                leadTaskDao.saveAndFlush(savedTask);
                manageLeadTaskListingsOnUpdate(currentTaskId, taskDto.getListingIds());
                if (taskStatus.getResultingStatusId() != null) {
                    leadOfferService
                            .updateLeadOfferStatus(leadTask.getLeadOfferId(), taskStatus.getResultingStatusId());
                }

                if (savedTask.getNextTask() != null) {
                    LeadTask nextTask = leadTaskDao.saveAndFlush(savedTask.getNextTask());
                    nextTaskId = nextTask.getId();
                    manageLeadTaskListingsOnUpdate(nextTaskId, taskDto.getNextTask().getListingIds());
                }
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                logger.debug("Error in bean copy");
            }
        }
        else {
            throw new BadRequestException();
        }

        LeadTask finalTask = leadTaskDao.getLeadTaskDetails(currentTaskId);
        if(nextTaskId != 0){
            finalTask.setNextTask(leadTaskDao.getLeadTaskDetails(nextTaskId));
        }

        return finalTask.populateTransientAttributes();
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
            LeadTaskStatusReason reason = taskStatusReasonDao.findByReasonAndTaskStatusMappingId(
                    leadTaskDto.getStatusReason(),
                    leadTask.getTaskStatusId());
            if (reason != null) {
                leadTask.setStatusReasonId(reason.getId());
            }
        }
        catch (Exception e) {
            logger.debug("Error in getLeadTaskFromLeadTaskDto: " + e);
            throw new BadRequestException();
        }
        return leadTask;
    }

    @Transactional
    private void manageLeadTaskListingsOnUpdate(int taskId, Set<Integer> listingIds) {
        LeadTask task = leadTaskDao.findOne(taskId);

        LeadTaskStatus taskStatus = leadTaskStatusDao.findOne(task.getTaskStatusId());

        // System.out.println("AAAAAAAAAAAAAA " + new Gson().toJson(task));

        // checking id listingIds size is of allowed count
        int minListingCount = taskStatus.getMasterLeadTask().getMinListingCount();
        int maxListingCount = taskStatus.getMasterLeadTask().getMaxListingCount();
        if (listingIds.size() < minListingCount || listingIds.size() > maxListingCount) {
            throw new BadRequestException("Listing Id Count Should Lie Between " + minListingCount
                    + " and "
                    + maxListingCount);
        }

        List<LeadOfferedListing> offeredListings = offeredListingDao.findByLeadOfferId(task.getLeadOfferId());
        Map<Integer, LeadOfferedListing> listingToOfferedListingMap = new HashMap<>();
        for (LeadOfferedListing offeredListing : offeredListings) {
            listingToOfferedListingMap.put(offeredListing.getListingId(), offeredListing);
        }

        for (Integer listingId : listingIds) {
            if (!listingToOfferedListingMap.containsKey(listingId)) {
                throw new BadRequestException("ListingId " + listingId + " Not Yet Offered");
            }
        }

        List<TaskOfferedListingMapping> listingMappings = leadTaskDao.getMappedListingMappingsForTask(taskId);
        Map<Integer, TaskOfferedListingMapping> listingToListingMappingMap = new HashMap<>();
        for (TaskOfferedListingMapping taskOfferedListingMapping : listingMappings) {
            listingToListingMappingMap.put(
                    taskOfferedListingMapping.getOfferedListing().getListingId(),
                    taskOfferedListingMapping);
        }

        Map<Integer, Integer> listingToListingIdMap = new HashMap<>();
        for (Integer listingId : listingIds) {
            listingToListingIdMap.put(listingId, listingId);
        }

        List<Integer> toBeRemoved = new ArrayList<>();
        for (Integer taskMappedListingId : listingToListingMappingMap.keySet()) {
            if (listingToListingIdMap.get(taskMappedListingId) == null) {
                toBeRemoved.add(listingToListingMappingMap.get(taskMappedListingId).getId());
            }
        }

        List<Integer> toBeAdded = new ArrayList<>();
        for (Integer listingId : listingIds) {
            if (listingToListingMappingMap.get(listingId) == null) {
                toBeAdded.add(listingToOfferedListingMap.get(listingId).getId());
            }
        }

        for (Integer listingId : toBeRemoved) {
            taskOfferedListingMappingDao.delete(listingId);
        }

        for (Integer listingOfferId : toBeAdded) {
            TaskOfferedListingMapping offeredListingMapping = new TaskOfferedListingMapping(taskId, listingOfferId);
            taskOfferedListingMappingDao.save(offeredListingMapping);
        }
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

        // offer id should be same
        if (leadTask.getLeadOfferId() != leadTask.getLeadOfferId()) {
            result = false;
        }
        // complete tasks should not be editable
        else if (oldStatus.getMasterLeadTaskStatus().isComplete()) {
            result = false;
        }
        // task status shoud not be the one for the nex tasks
        else if (newStatus.getMasterLeadTaskStatus().getStatus().equals(newTaskDefaultStatus)) {
            result = false;
        }
        // validating status reason
        else if (!isValidStatusReason(leadTask)) {
            result = false;
        }
        else if (oldStatus.getId() != newStatus.getId()) {
            // task type is not editable
            if (oldStatus.getMasterTaskId() != newStatus.getMasterTaskId()) {
                result = false;
            }
            // cases where performed at is mandatory
            if (newStatus.getMasterLeadTaskStatus().isComplete() && leadTask.getPerformedAt() == null) {
                result = false;
            }
            // cases where next task is mandatory
            else if (newStatus.getMasterLeadTaskStatus().isNextTaskRequired()) {
                if (leadTask.getNextTask() == null) {
                    result = false;
                }
                else if (!isValidNextTask(leadTask)) {
                    result = false;
                }
            }
            // next task is not required but is provided
            else if (!newStatus.getMasterLeadTaskStatus().isNextTaskRequired() && leadTask.getNextTask() != null) {
                result = false;
            }
        }
        return result;
    }

    /**
     * validates if the reason provided for a particulat status is correct
     * 
     * @param leadTask
     * @return
     */
    private boolean isValidStatusReason(LeadTask leadTask) {
        if (leadTask.getStatusReasonId() == null) {
            return taskStatusReasonDao.findByTaskStatusMappingId(leadTask.getTaskStatusId()).isEmpty();
        }
        else {
            return taskStatusReasonDao.findOne(leadTask.getStatusReasonId()).getTaskStatusMappingId() == leadTask
                    .getTaskStatusId();
        }
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

            // offer id of the next task should be same as the prev one
            if (leadTask.getLeadOfferId() != nextTask.getLeadOfferId()) {
                isValid = false;
            }
            // checking that the next task must be in default status
            else if (!nextTaskStatus.getMasterLeadTaskStatus().getStatus().equals(newTaskDefaultStatus)) {
                isValid = false;
            }
            // checking is next task is of valid type
            else if (!isValidNextTaskType(leadTask)) {
                isValid = false;
            }
            // next task should not have performed at
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
        boolean result = true;
        LeadTaskStatus taskStatus = leadTaskStatusDao.findOne(leadTask.getTaskStatusId());
        LeadTaskStatus nextTaskStatus = leadTaskStatusDao.findOne(leadTask.getNextTask().getTaskStatusId());
        List<MasterLeadTask> completedTasks = masterLeadTaskDao.getcompleteMandatoryTasks(leadTask.getLeadOfferId());
        Map<Integer, MasterLeadTask> indexedCompletedTasks = new HashMap<>();
        for (MasterLeadTask masterLeadTask : completedTasks) {
            indexedCompletedTasks.put(masterLeadTask.getId(), masterLeadTask);
        }

        List<MasterLeadTask> mustDoTasks = masterLeadTaskDao.findByPriorityLessThanAndOptional(nextTaskStatus
                .getMasterLeadTask().getPriority(), true);

        for (MasterLeadTask masterLeadTask : mustDoTasks) {
            int notDoneTaskId = masterLeadTask.getId();
            if (!indexedCompletedTasks.containsKey(notDoneTaskId)) {
                if (taskStatus.getMasterLeadTask().getId() != notDoneTaskId) {
                    result = false;
                }
            }
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
    public LeadTask createDefaultLeadTaskForLeadOffer(LeadOffer leadOffer) {
        LeadTask leadTask = new LeadTask();
        int leadOfferId = leadOffer.getId();
        List<LeadTask> leadTasks = leadTaskDao.findByLeadOfferId(leadOfferId);
        if (leadTasks.isEmpty()) {
            leadTask.setTaskStatusId(offerDefaultLeadTaskStatusMappingId);
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

        LeadTask.populateTransientAttributes(response.getResults());

        return response;
    }

    // Lets see if we need this ever again
    // /**
    // * populated offered listings in task list
    // *
    // * @param leadTasks
    // */
    // private void populateTaskOfferedListings(List<LeadTask> leadTasks) {
    // List<Integer> taskIds = new ArrayList<>();
    // for (LeadTask leadTask : leadTasks) {
    // taskIds.add(leadTask.getId());
    // }
    //
    // if (!taskIds.isEmpty()) {
    // List<LeadTask> listingMappLeadTasks =
    // leadTaskDao.getListingMappedTasksByTaskIds(taskIds);
    // Map<Integer, LeadTask> idMappedTasks = new HashMap<>();
    // for (LeadTask leadTask : listingMappLeadTasks) {
    // idMappedTasks.put(leadTask.getId(), leadTask);
    // }
    //
    // for (LeadTask leadTask : leadTasks) {
    // LeadTask taskWithListing = idMappedTasks.get(leadTask.getId());
    // if (taskWithListing != null) {
    // leadTask.setOfferedListingMappings(taskWithListing.getOfferedListingMappings());
    // }
    // }
    // }
    // }

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