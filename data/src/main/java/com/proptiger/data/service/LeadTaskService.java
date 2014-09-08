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

import com.proptiger.data.enums.LeadOfferStatus;
import com.proptiger.data.enums.ListingCategory;
import com.proptiger.data.enums.TaskStatus;
import com.proptiger.data.external.dto.LeadTaskDto;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.LeadTaskStatus;
import com.proptiger.data.model.MasterLeadOfferStatus;
import com.proptiger.data.model.MasterLeadTask;
import com.proptiger.data.model.marketplace.Lead;
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
import com.proptiger.data.repo.marketplace.LeadDao;
import com.proptiger.data.repo.marketplace.LeadOfferDao;
import com.proptiger.data.repo.marketplace.LeadOfferedListingDao;
import com.proptiger.data.repo.marketplace.LeadTaskStatusReasonDao;
import com.proptiger.data.repo.marketplace.TaskOfferedListingMappingDao;
import com.proptiger.data.service.marketplace.LeadOfferService;
import com.proptiger.data.service.marketplace.ListingService;
import com.proptiger.data.service.marketplace.NotificationService;
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

    @Autowired
    private LeadOfferDao                 leadOfferDao;

    @Autowired
    private LeadDao                      leadDao;

    @Autowired
    private ListingService               listingService;

    @Autowired
    private NotificationService          notificationService;

    private static final int             offerDefaultLeadTaskStatusMappingId = 1;

    private static final String          defaultTaskSelection                = "id";

    private static final String          defaultTaskSorting                  = "taskStatus.masterLeadTaskStatus.complete,-updatedAt";

    private static final String          interestedInPrimary                 = "InterestedInPrimary";

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
                if (taskStatus.getMasterLeadTaskStatus().isComplete()) {
                    if (savedTask.getNextTask() != null) {
                        LeadTask nextTask = leadTaskDao.saveAndFlush(savedTask.getNextTask());
                        nextTaskId = nextTask.getId();
                        manageLeadTaskListingsOnUpdate(nextTaskId, taskDto.getNextTask().getListingIds());
                    }
                    Integer offerNextTaskId = nextTaskId == 0 ? null : nextTaskId;
                    leadOfferService.updateLeadOfferTasks(savedTask.getLeadOffer(), currentTaskId, offerNextTaskId);
                }
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                logger.debug("Error in bean copy");
            }
        }
        else {
            throw new BadRequestException();
        }

        managePostUpdateActivities(savedTask);

        LeadTask finalTask = getLeadTaskWithAllDetails(currentTaskId);
        if (nextTaskId != 0) {
            LeadTask nextTask = getLeadTaskWithAllDetails(nextTaskId);
            finalTask.setNextTask(nextTask);
        }
        finalTask.unlinkCircularLoop();
        return finalTask.populateTransientAttributes();
    }

    /**
     * manages actions which are supposed to happen post update of a task
     * 
     * @param task
     */
    private void managePostUpdateActivities(LeadTask task) {
        notificationService.manageTaskNotificationForLeadOffer(task.getLeadOfferId());
        manageDealClosed(task);
        // manageMoveToPrimary(task.getLeadOffer().getLeadId());
    }

    /**
     * controls actions post closed won
     * 
     * @param task
     */
    private void manageDealClosed(LeadTask task) {
        task.setTaskStatus(leadTaskStatusDao.getLeadTaskStatusDetail(task.getTaskStatusId()));
        if (task.getTaskStatus().getMasterLeadTaskStatus().getStatus().equals(TaskStatus.AtsSignedChequeCollected)) {
            listingService.deleteListing(SecurityContextUtils.getLoggedInUserId(), task.getOfferedListingMappings()
                    .get(0).getOfferedListing().getListingId());
            notificationService.manageDealClosedNotification(task.getLeadOfferId());
        }
    }

    /**
     * decides if a lead needs to be moved to primary
     * 
     * @param leadId
     */
    private void manageMoveToPrimary(int leadId) {
        Lead lead = leadDao.findOne(leadId);
        if (lead.getTransactionType().equals(ListingCategory.PrimaryAndResale.toString())) {
            List<LeadOffer> offers = leadOfferDao.findByLeadId(leadId);
            boolean lost = true;
            boolean primaryLead = false;
            for (LeadOffer offer : offers) {
                MasterLeadOfferStatus status = offer.getMasterLeadOfferStatus();
                if (status.isOpen() || LeadOfferStatus.ClosedWon.equals(LeadOfferStatus.valueOf(status.getStatus()))) {
                    lost = false;
                }
                if (!(leadTaskDao.findByofferIdAndStatusReason(offer.getId(), interestedInPrimary) == null)) {
                    primaryLead = true;
                }
            }
            if (lost && primaryLead) {
                // XXX API CALL TO MOVE LEAD TO PRIMARY
            }
        }
    }

    // XXX -- introduced only because we have not yet found a solution to load
    // all depending objects in one single query ignoring hibernate cache
    private LeadTask getLeadTaskWithAllDetails(int taskId) {
        LeadTask leadTask = leadTaskDao.findOne(taskId);
        leadTask.setLeadOffer(leadOfferDao.findOne(leadTask.getLeadOfferId()));
        leadTask.setTaskStatus(leadTaskStatusDao.getLeadTaskStatusDetail(leadTask.getTaskStatusId()));
        leadTask.setOfferedListingMappings(taskOfferedListingMappingDao.findByTaskId(taskId));
        if (leadTask.getStatusReasonId() != null) {
            leadTask.setStatusReason(taskStatusReasonDao.findOne(leadTask.getStatusReasonId()));
        }
        return leadTask;
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
            leadTask.setTaskStatusId(leadTaskDto.getTaskStatusMappingId());
            leadTask.setScheduledFor(leadTaskDto.getScheduledFor());
            leadTask.setCallDuration(leadTaskDto.getCallDuration());
            leadTask.setPerformedAt(leadTaskDto.getPerformedAt());
            leadTask.setNotes(leadTaskDto.getNotes());
            if (leadTaskDto.getNextTask() != null) {
                leadTask.setNextTask(getLeadTaskFromLeadTaskDto(leadTaskDto.getNextTask()));
            }
            leadTask.setStatusReasonId(leadTaskDto.getStatusReasonId());
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
            logger.info("OFFER ID PASSED IS NOT CONSISTENT");
            result = false;
        }
        // complete tasks should not be editable
        else if (oldStatus.getMasterLeadTaskStatus().isComplete()) {
            logger.info("COMPLATE TASK CANT BE EDITED");
            result = false;
        }
        // task status should not be the one for the new tasks
        else if (newStatus.getMasterLeadTaskStatus().isBeginning()) {
            logger.info("NOT A VALID STATUS FOR TASK BEING UPDATED");
            result = false;
        }
        // validating status reason
        else if (!isValidStatusReason(leadTask)) {
            logger.info("NOT A VALID STATUS REASON");
            result = false;
        }
        else if (oldStatus.getId() != newStatus.getId()) {
            // task type is not editable
            if (oldStatus.getMasterTaskId() != newStatus.getMasterTaskId()) {
                logger.info("TASK TYPE CANT BE CHANGED");
                result = false;
            }
            // cases where performed at is mandatory
            if (newStatus.getMasterLeadTaskStatus().isComplete() && leadTask.getPerformedAt() == null) {
                logger.info("COMPLETE TASKS SHOULD HAVE PERFORMED AT");
                result = false;
            }
            // cases where next task is mandatory
            else if (newStatus.getMasterLeadTaskStatus().isNextTaskRequired()) {
                if (leadTask.getNextTask() == null) {
                    logger.info("NEXT TASK IS MENDATORY");
                    result = false;
                }
                else if (!isValidNextTask(leadTask)) {
                    result = false;
                }
            }
            // next task is not required but is provided
            else if (!newStatus.getMasterLeadTaskStatus().isNextTaskRequired() && leadTask.getNextTask() != null) {
                logger.info("NEXT TASK IS NOT NEEDED");
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
                logger.info("OFFER ID OF NEXT TASK SHOULD BE SAME");
                isValid = false;
            }
            // checking that the next task must be in default status
            else if (!nextTaskStatus.getMasterLeadTaskStatus().isBeginning()) {
                logger.info("TASK STATUS NOT VALID FOR NEW TASK");
                isValid = false;
            }
            // checking is next task is of valid type
            else if (!isValidNextTaskType(leadTask)) {
                logger.info("NOT A VALID NEXT TASK TYPE");
                isValid = false;
            }
            // next task should not have performed at
            else if (nextTask.getPerformedAt() != null) {
                logger.info("NEXT TASK SHOULDNT HAVE PERFORMED AT");
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
                .getMasterLeadTask().getPriority(), false);

        for (MasterLeadTask masterLeadTask : mustDoTasks) {
            int notDoneTaskId = masterLeadTask.getId();
            if (!indexedCompletedTasks.containsKey(notDoneTaskId)) {
                if (!taskStatus.getMasterLeadTaskStatus().isComplete() || taskStatus.getMasterLeadTask().getId() != notDoneTaskId) {
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

            leadOfferService.updateLeadOfferTasks(leadOffer, null, leadTask.getId());
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
        selector.applyDefSort(defaultTaskSorting).applyDefFields(defaultTaskSelection);

        Pageable pageable = new LimitOffsetPageRequest(
                selector.getStart(),
                selector.getRows(),
                selector.getSpringDataSort());

        PaginatedResponse<List<LeadTask>> response = new PaginatedResponse<>();
        response.setTotalCount(leadTaskDao.getLeadTaskCountForUser(userId));
        response.setResults(leadTaskDao.getLeadTasksForUser(userId, pageable));

        LeadTask.populateTransientAttributes(response.getResults());
        LeadTask.unlinkCircularLoop(response.getResults());
        return response;
    }

    /**
     * fetches list of master tasks including all possible actions
     * 
     * @return
     */
    public List<MasterLeadTask> getMasterTaskDetails() {
        List<MasterLeadTask> leadTasks = masterLeadTaskDao.getMasterTaskDetails();

        List<LeadTaskStatusReason> statusReasons = taskStatusReasonDao.findAll();
        Map<Integer, List<LeadTaskStatusReason>> mappedStatusReasons = new HashMap<>();
        for (LeadTaskStatusReason leadTaskStatusReason : statusReasons) {
            int statusId = leadTaskStatusReason.getTaskStatusMappingId();
            if (!mappedStatusReasons.containsKey(statusId)) {
                mappedStatusReasons.put(statusId, new ArrayList<LeadTaskStatusReason>());
            }
            mappedStatusReasons.get(statusId).add(leadTaskStatusReason);
        }

        for (MasterLeadTask masterLeadTask : leadTasks) {
            for (LeadTaskStatus status : masterLeadTask.getLeadTaskStatuses()) {
                status.setMasterLeadTask(null);
                status.setStatusReasons(mappedStatusReasons.get(status.getId()));
            }
        }
        return leadTasks;
    }

    public Map<Integer, LeadTask> getTaskById(List<Integer> leadTaskIds) {
        Map<Integer, LeadTask> taskMap = new HashMap<>();

        if (leadTaskIds != null && !leadTaskIds.isEmpty()) {
            List<LeadTask> leadTasks = leadTaskDao.findById(leadTaskIds);
            Map<Integer, List<TaskOfferedListingMapping>> taskOfferedListingMappings = extractListings(leadTaskDao
                    .getTaskOfferedListingMappings(leadTaskIds));
            LeadTask.populateTransientAttributes(leadTasks);

            for (LeadTask leadTask : leadTasks) {
                leadTask.setLeadOffer(null);
                leadTask.setOfferedListingMappings(taskOfferedListingMappings.get(leadTask.getId()));
                leadTask.getMasterLeadTask().setLeadTaskStatuses(null);
                taskMap.put(leadTask.getId(), leadTask);
            }
        }

        return taskMap;
    }

    public List<LeadTask> getTasksByLeadOfferId(int leadOfferId) {
        List<LeadTask> leadTasks = leadTaskDao.findTasksByLeadOfferId(leadOfferId);

        List<Integer> leadTaskIds = new ArrayList<>();
        for (LeadTask leadTask : leadTasks) {
            leadTaskIds.add(leadTask.getId());
        }

        if (!leadTaskIds.isEmpty()) {
            Map<Integer, List<TaskOfferedListingMapping>> taskOfferedListingMappings = extractListings(leadTaskDao
                    .getTaskOfferedListingMappings(leadTaskIds));
            LeadTask.populateTransientAttributes(leadTasks);

            for (LeadTask leadTask : leadTasks) {
                leadTask.setLeadOffer(null);
                leadTask.setOfferedListingMappings(taskOfferedListingMappings.get(leadTask.getId()));
                leadTask.getMasterLeadTask().setLeadTaskStatuses(null);
            }
        }

        return leadTasks;
    }

    private Map<Integer, List<TaskOfferedListingMapping>> extractListings(
            List<TaskOfferedListingMapping> taskOfferedListingMappings) {
        Map<Integer, List<TaskOfferedListingMapping>> listings = new HashMap<>();

        for (TaskOfferedListingMapping taskOfferedListingMapping : taskOfferedListingMappings) {
            int taskId = taskOfferedListingMapping.getTaskId();
            if (!listings.containsKey(taskId)) {
                listings.put(taskId, new ArrayList<TaskOfferedListingMapping>());
            }

            listings.get(taskId).add(taskOfferedListingMapping);
        }

        return listings;
    }

    /**
     * method to get task object along with all associated objects
     * 
     * @param taskId
     * @return
     */
    public LeadTask getTaskDetails(int taskId) {
        return leadTaskDao.getLeadTaskDetails(taskId);
    }

    public static int getOfferdefaultleadtaskstatusmappingid() {
        return offerDefaultLeadTaskStatusMappingId;
    }

    public LeadTask getLeadTask(int taskId) {
        return leadTaskDao.findOne(taskId);
    }

    @Transactional
    public LeadTask createLeadTask(LeadTask leadTask) {
        return leadTaskDao.saveAndFlush(leadTask);
    }

    public List<LeadTask> getLeadTaskIdsByLeadOfferId(int leadOfferId) {
        return leadTaskDao.findByLeadOfferId(leadOfferId);
    }
}