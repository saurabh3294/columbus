package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.enums.notification.MediumType;
import com.proptiger.core.internal.dto.mail.MediumDetails;
import com.proptiger.core.pojo.LimitOffsetPageRequest;
import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMedium;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationMessageUpdateHistory;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.notification.repo.NotificationGeneratedDao;
import com.proptiger.data.util.Serializer;

@Service
public class NotificationGeneratedService {
    private static Logger                                    logger = LoggerFactory
                                                                            .getLogger(NotificationGenerated.class);

    @Autowired
    private NotificationGeneratedDao                         notificationGeneratedDao;

    @Autowired
    private MediumTypeService                                mediumTypeService;

    @Autowired
    private NotificationTypeNotificationMediumMappingService nMappingService;

    @Autowired
    private NotificationTypeService                          notificationTypeService;

    @Autowired
    private NotificationMediumService                        notificationMediumService;

    @Autowired
    private NotificationMessageService                       notificationMessageService;

    public List<NotificationGenerated> getScheduledAndNonReadyNotifications() {
        List<NotificationGenerated> notificationGenerateds = notificationGeneratedDao
                .findByNotificationStatusAndScheduleTimeGreaterThanOrNotificationStatusAndScheduleTimeIsNull(
                        NotificationStatus.Scheduled,
                        new Date(),
                        NotificationStatus.Generated);
        if (notificationGenerateds == null) {
            return new ArrayList<NotificationGenerated>();
        }

        populateDataOnLoad(notificationGenerateds);

        return notificationGenerateds;
    }

    public void populateDataOnLoad(List<NotificationGenerated> notificationGenerateds) {
        for (NotificationGenerated nGenerated : notificationGenerateds) {
            String data = nGenerated.getData();
            nGenerated.setNotificationMessagePayload(Serializer.fromJson(data, NotificationMessagePayload.class));
            NotificationType notificationType = nGenerated.getNotificationType();
            notificationTypeService.populateNotificationTypeConfig(notificationType);
        }
    }

    public List<NotificationGenerated> getScheduledAndReadyNotifications(MediumType medium) {
        List<NotificationGenerated> ntGeneratedList = notificationGeneratedDao
                .findByStatusAndScheduleTimeLessThanEqualAndMediumName(NotificationStatus.Scheduled, new Date(), medium);
        if (ntGeneratedList == null) {
            return new ArrayList<NotificationGenerated>();
        }
        mediumTypeService.setNotificationMediumSender(ntGeneratedList);
        populateDataOnLoad(ntGeneratedList);
        return ntGeneratedList;
    }

    public Map<Integer, List<NotificationGenerated>> groupNotificationGeneratedByuser(
            List<NotificationGenerated> notificationGeneratedList) {
        if (notificationGeneratedList == null) {
            return new HashMap<Integer, List<NotificationGenerated>>();
        }

        Map<Integer, List<NotificationGenerated>> groupNotificationMessageMap = new HashMap<Integer, List<NotificationGenerated>>();
        Integer userId = null;
        List<NotificationGenerated> groupNotifcationGenerated = null;
        for (NotificationGenerated notificationGenerated : notificationGeneratedList) {
            userId = notificationGenerated.getUserId();
            groupNotifcationGenerated = groupNotificationMessageMap.get(userId);

            if (groupNotificationMessageMap.get(userId) == null) {
                groupNotifcationGenerated = new ArrayList<NotificationGenerated>();
            }
            groupNotifcationGenerated.add(notificationGenerated);
            groupNotificationMessageMap.put(userId, groupNotifcationGenerated);
        }

        return groupNotificationMessageMap;
    }

    public Map<String, List<NotificationGenerated>> groupNotificationsByNotificationType(
            List<NotificationGenerated> notificationGeneratedList) {
        if (notificationGeneratedList == null) {
            return new HashMap<String, List<NotificationGenerated>>();
        }

        Map<String, List<NotificationGenerated>> groupNotificationMessageMap = new HashMap<String, List<NotificationGenerated>>();
        NotificationType notificationType = null;
        String notificationName = null;
        List<NotificationGenerated> groupNotifcationMessage = null;
        for (NotificationGenerated notificationGenerated : notificationGeneratedList) {

            notificationType = notificationGenerated.getNotificationType();
            notificationName = notificationType.getName();
            groupNotifcationMessage = groupNotificationMessageMap.get(notificationName);

            if (groupNotificationMessageMap.get(notificationName) == null) {
                groupNotifcationMessage = new ArrayList<NotificationGenerated>();
            }
            groupNotifcationMessage.add(notificationGenerated);
            groupNotificationMessageMap.put(notificationName, groupNotifcationMessage);
        }

        return groupNotificationMessageMap;
    }

    public void addNotificationGeneratedUpdateHistory(
            NotificationGenerated notificationGenerated,
            NotificationStatus notificationStatus) {
        NotificationMessageUpdateHistory nHistory = new NotificationMessageUpdateHistory(notificationStatus, new Date());

        notificationGenerated.getNotificationMessagePayload().getNotificationMessageUpdateHistories().add(nHistory);
    }

    @Transactional
    public Iterable<NotificationGenerated> save(List<NotificationGenerated> nGenerateds) {
        for (NotificationGenerated notificationGenerated : nGenerateds) {
            populateDataBeforeSave(notificationGenerated);
            if (notificationGenerated.getNotificationMessage() != null) {
                notificationGenerated.getNotificationMessage().setNotificationStatus(NotificationStatus.Generated);
            }
        }
        return notificationGeneratedDao.save(nGenerateds);

    }

    @Transactional
    public NotificationGenerated save(NotificationGenerated notificationGenerated) {
        if (notificationGenerated.getNotificationMessage() != null) {
            notificationGenerated.getNotificationMessage().setNotificationStatus(NotificationStatus.Generated);
        }
        populateDataBeforeSave(notificationGenerated);
        return notificationGeneratedDao.save(notificationGenerated);
    }

    public void populateDataBeforeSave(NotificationGenerated notificationGenerated) {
        notificationGenerated.setData(Serializer.toJson(notificationGenerated.getNotificationMessagePayload()));
    }

    public void updateNotificationGeneratedStatusOnOldStatus(
            Map<NotificationStatus, List<NotificationGenerated>> map,
            Map<Integer, List<NotificationGenerated>> allNotificationGenMap) {
        if (map == null) {
            return;
        }
        List<NotificationGenerated> allNGenerateds = null;
        NotificationStatus oldNotificationStatus;
        /**
         * Getting All the Distinct NotificationGenerated By their Notification
         * Message Id.
         */
        for (Map.Entry<NotificationStatus, List<NotificationGenerated>> entry : map.entrySet()) {
            for (NotificationGenerated nGenerated : entry.getValue()) {
                // Getting all the Notification Generated for a Notification
                // Message Id.
                allNGenerateds = allNotificationGenMap.get(nGenerated.getNotificationMessage().getId());
                // Removing the first one as it was already present in the
                // distinct Notification Generated.

                if (allNGenerateds != null) {
                    allNGenerateds.remove(0);
                    /**
                     * Iterating on rest of the notification Generated Group by
                     * Notification Message Id. Replacing the rest Notification
                     * Generated payload and status with distinct Notification
                     * Generated.
                     */
                    for (NotificationGenerated finalGenerated : allNGenerateds) {

                        finalGenerated.setNotificationMessagePayload(nGenerated.getNotificationMessagePayload());
                        // getting their old status.
                        oldNotificationStatus = finalGenerated.getNotificationStatus();
                        // setting the new status.
                        finalGenerated.setNotificationStatus(nGenerated.getNotificationStatus());
                        notificationGeneratedDao.updateByNotificationStatusOnOldNotificationStatus(
                                finalGenerated.getId(),
                                finalGenerated.getNotificationStatus(),
                                oldNotificationStatus);

                    }
                }
                // Updating the Distinct Notification Generated with their new
                // status.
                notificationGeneratedDao.updateByNotificationStatusOnOldNotificationStatus(
                        nGenerated.getId(),
                        nGenerated.getNotificationStatus(),
                        entry.getKey());
            }
        }
    }

    private NotificationGenerated createNotificationGenerated(
            NotificationMessage notificationMessage,
            NotificationMedium notificationMedium) {
        NotificationGenerated nGenerated = new NotificationGenerated();
        nGenerated.setUserId(notificationMessage.getUserId());
        nGenerated.setNotificationMedium(notificationMedium);
        nGenerated.setNotificationMessage(notificationMessage);

        NotificationMessagePayload notificationMessagePayload = new NotificationMessagePayload(
                notificationMessage.getNotificationMessagePayload());
        nGenerated.setNotificationMessagePayload(notificationMessagePayload);
        nGenerated.setNotificationType(notificationMessage.getNotificationType());

        logger.debug(Serializer.toJson(notificationMessage));
        NotificationTypePayload payload = notificationMessage.getNotificationMessagePayload()
                .getNotificationTypePayload();
        if (payload != null) {
            Integer objectId = Integer.parseInt((String) payload.getPrimaryKeyValue());
            if (objectId != null) {
                nGenerated.setObjectId(objectId);
            }
        }
        return nGenerated;
    }

    /**
     * Create NotificationGenerateds for given Notification Messages in given
     * Mediums
     * 
     * @param nMessages
     * @param mediumTypes
     * @return
     */
    public List<NotificationGenerated> createNotificationGenerated(
            List<NotificationMessage> nMessages,
            List<MediumDetails> mediumDetailList) {
        if (mediumDetailList == null) {
            return generateNotficationGenerated(nMessages);
        }
        List<NotificationGenerated> generatedList = new ArrayList<NotificationGenerated>();
        NotificationType defaultNotificationType = notificationTypeService.findDefaultNotificationType();

        for (MediumDetails mediumDetail : mediumDetailList) {
            MediumType mediumType = mediumDetail.getMediumType();
            NotificationMedium nMedium = notificationMediumService.findNotificationMediumByMediumType(mediumType);
            for (NotificationMessage nMessage : nMessages) {
                if (nMessage.getNotificationType() == null) {
                    nMessage.setNotificationType(defaultNotificationType);
                }
                NotificationGenerated nGenerated = createNotificationGenerated(nMessage, nMedium);
                nGenerated.getNotificationMessagePayload().setMediumDetails(mediumDetail);
                generatedList.add(nGenerated);
            }
        }
        save(generatedList);
        return generatedList;
    }

    public List<NotificationGenerated> generateNotficationGenerated(List<NotificationMessage> nMessages) {
        NotificationType nType = null;
        List<NotificationMedium> nMediums = null;
        List<NotificationGenerated> generatedList = new ArrayList<NotificationGenerated>();
        NotificationGenerated nGenerated = null;
        NotificationType defaultNotificationType = notificationTypeService.findDefaultNotificationType();
        for (NotificationMessage nMessage : nMessages) {

            if (nMessage.getNotificationType() == null) {
                nMessage.setNotificationType(defaultNotificationType);
            }
            nType = nMessage.getNotificationType();
            nMediums = nMappingService.getMediumsByNotificationTypeId(nType.getId());
            // TODO handle the scenario when no mapping of notification medium
            // with type.
            if (nMediums == null || nMediums.size() < 1) {
                continue;
            }
            for (NotificationMedium nMedium : nMediums) {
                nGenerated = createNotificationGenerated(nMessage, nMedium);
                generatedList.add(nGenerated);
            }
        }
        save(generatedList);
        return generatedList;
    }

    public NotificationGenerated getLastScheduledOrSendNotificationGeneratedSameAs(NotificationGenerated ntGenerated) {
        List<NotificationStatus> notificationStatusList = new ArrayList<NotificationStatus>();
        notificationStatusList.add(NotificationStatus.Scheduled);
        notificationStatusList.add(NotificationStatus.Sent);
        LimitOffsetPageRequest pageable = new LimitOffsetPageRequest(0, 1);
        List<NotificationGenerated> ntGeneratedList = notificationGeneratedDao.getLastNotificationGenerated(
                notificationStatusList,
                ntGenerated.getNotificationMedium().getId(),
                ntGenerated.getUserId(),
                ntGenerated.getNotificationType().getId(),
                ntGenerated.getObjectId(),
                pageable);
        if (ntGeneratedList != null && !ntGeneratedList.isEmpty()) {
            return ntGeneratedList.get(0);
        }
        return null;
    }

    public NotificationGenerated getLastScheduledOrSentNotificationGeneratedInMediumSameAs(
            NotificationGenerated ntGenerated) {
        List<NotificationStatus> notificationStatusList = new ArrayList<NotificationStatus>();
        notificationStatusList.add(NotificationStatus.Scheduled);
        notificationStatusList.add(NotificationStatus.Sent);
        LimitOffsetPageRequest pageable = new LimitOffsetPageRequest(0, 1);
        List<NotificationGenerated> ntGeneratedList = notificationGeneratedDao
                .getLastSentNotificationGeneratedInMedium(notificationStatusList, ntGenerated.getUserId(), ntGenerated
                        .getNotificationMedium().getId(), pageable);
        if (ntGeneratedList != null && !ntGeneratedList.isEmpty()) {
            return ntGeneratedList.get(0);
        }
        return null;
    }

    public Map<Integer, Map<Integer, List<NotificationGenerated>>> groupNotificationByMessageId(
            List<NotificationGenerated> nGenerateds,
            List<NotificationGenerated> distinctNotificationGenerated) {
        Map<Integer, Map<Integer, List<NotificationGenerated>>> map = new LinkedHashMap<Integer, Map<Integer, List<NotificationGenerated>>>();
        List<NotificationGenerated> groupGenerateds = null;
        Map<Integer, List<NotificationGenerated>> groupByMessageId = null;

        for (NotificationGenerated nGenerated : nGenerateds) {
            groupByMessageId = map.get(nGenerated.getNotificationMessage().getId());
            if (groupByMessageId == null) {
                groupByMessageId = new LinkedHashMap<Integer, List<NotificationGenerated>>();
                map.put(nGenerated.getUserId(), groupByMessageId);
            }
            groupGenerateds = groupByMessageId.get(nGenerated.getNotificationMessage().getId());
            if (groupGenerateds == null) {
                groupGenerateds = new ArrayList<NotificationGenerated>();
                groupByMessageId.put(nGenerated.getNotificationMessage().getId(), groupGenerateds);
                groupGenerateds.add(nGenerated);
                distinctNotificationGenerated.add(nGenerated);
            }
            groupGenerateds.add(nGenerated);
        }

        return map;
    }

    public void updateNotificationGeneratedStatusOnOldStatus(
            Integer id,
            NotificationStatus newStatus,
            NotificationStatus oldStatus) {
        notificationGeneratedDao.updateByNotificationStatusOnOldNotificationStatus(id, newStatus, oldStatus);
    }

    public List<NotificationGenerated> getRawNotificationGeneratedList() {
        return notificationGeneratedDao.findByNotificationStatus(NotificationStatus.Generated);
    }

    @Transactional
    public void markNotificationGeneratedScheduled(NotificationGenerated ntGenerated, Date scheduledTime) {
        notificationGeneratedDao.updatedNotificationStatusAndScheduleTimeById(
                ntGenerated.getId(),
                NotificationStatus.Scheduled,
                scheduledTime);
    }

    @Transactional
    public void markNotificationGeneratedSuppressed(NotificationGenerated ntGenerated) {
        notificationGeneratedDao.updateNotificationStatusById(
                ntGenerated.getId(),
                NotificationStatus.SchedulerSuppressed);
    }

    public NotificationGeneratedDao getNotificationGeneratedDao() {
        return notificationGeneratedDao;
    }

    public void setNotificationGeneratedDao(NotificationGeneratedDao notificationGeneratedDao) {
        this.notificationGeneratedDao = notificationGeneratedDao;
    }

    public NotificationMediumService getNotificationMediumService() {
        return notificationMediumService;
    }

    public void setNotificationMediumService(NotificationMediumService notificationMediumService) {
        this.notificationMediumService = notificationMediumService;
    }

    public NotificationTypeService getNotificationTypeService() {
        return notificationTypeService;
    }

    public void setNotificationTypeService(NotificationTypeService notificationTypeService) {
        this.notificationTypeService = notificationTypeService;
    }

    public NotificationTypeNotificationMediumMappingService getnMappingService() {
        return nMappingService;
    }

    public void setnMappingService(NotificationTypeNotificationMediumMappingService nMappingService) {
        this.nMappingService = nMappingService;
    }
}
