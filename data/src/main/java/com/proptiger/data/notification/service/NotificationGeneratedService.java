package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMedium;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationMessageUpdateHistory;
import com.proptiger.data.notification.repo.NotificationGeneratedDao;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.proptiger.data.util.Serializer;

@Service
public class NotificationGeneratedService {

    @Autowired
    private NotificationGeneratedDao notificationGeneratedDao;
    
    @Autowired
    private MediumTypeService mediumTypeService;

    @Autowired
    private NotificationTypeNotificationMediumMappingService nMappingService;
    
    @Autowired
    private NotificationTypeService notificationTypeService;

    public List<NotificationGenerated> getScheduledAndNonExpiredNotifications() {
        List<NotificationGenerated> notificationGenerateds = notificationGeneratedDao.findByNotificationStatusAndScheduleTimeLessThan(NotificationStatus.Scheduled, new Date());
        if(notificationGenerateds == null){
            return new ArrayList<NotificationGenerated>();
        }
        
        for(NotificationGenerated nGenerated: notificationGenerateds){
           populateDataOnLoad(nGenerated);
        }
        
        return notificationGenerateds;
    }

	public void populateDataOnLoad(NotificationGenerated nGenerated){
        String data = nGenerated.getData();
        nGenerated.setNotificationMessagePayload(Serializer.fromJson(data, NotificationMessagePayload.class));
        NotificationType notificationType = nGenerated.getNotificationType();
        notificationTypeService.populateNotificationTypeConfig(notificationType);
    }

 	public List<NotificationGenerated> getScheduledAndReadyNotifications(int mediumId){
 	    List<NotificationGenerated> ntGeneratedList = notificationGeneratedDao.findByStatusAndExpiryTimeGreaterThanEqualAndMediumId(NotificationStatus.Scheduled, new Date(), mediumId);
        mediumTypeService.setNotificationMediumSender(ntGeneratedList);
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
            userId = notificationGenerated.getForumUser().getUserId();
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
    public Iterable<NotificationGenerated> save(List<NotificationGenerated> nGenerateds){
        for(NotificationGenerated notificationGenerated:nGenerateds){
            populateDataBeforeSave(notificationGenerated);
        }
        return notificationGeneratedDao.save(nGenerateds);
        
    }
    
    @Transactional
    public NotificationGenerated save(NotificationGenerated notificationGenerated){
        populateDataBeforeSave(notificationGenerated);
        return notificationGeneratedDao.save(notificationGenerated);
    }
    
    public void populateDataBeforeSave(NotificationGenerated notificationGenerated){
        notificationGenerated.setData(Serializer.toJson(notificationGenerated.getNotificationMessagePayload()));
    }
    
    public void updateNotificationGeneratedStatusOnOldStatus(Map<NotificationStatus, List<NotificationGenerated>> map){
        if(map == null){
            return;
        }
        
        for(Map.Entry<NotificationStatus, List<NotificationGenerated>> entry:map.entrySet()){
             for(NotificationGenerated nGenerated:entry.getValue()){
                 notificationGeneratedDao.updateByNotificationStatusOnOldNotificationStatus(nGenerated.getId(), nGenerated.getNotificationStatus(), entry.getKey());
             }
        }
    }
    
    public NotificationGenerated createNotificationGenerated(NotificationMessage notificationMessage, NotificationMedium notificationMedium){
        NotificationGenerated nGenerated = new NotificationGenerated();
        nGenerated.setUserId(notificationMessage.getUserId());
        nGenerated.setNotificationMedium(notificationMedium);
        nGenerated.setNotificationMessage(notificationMessage);
        nGenerated.setNotificationMessagePayload(notificationMessage.getNotificationMessagePayload());
        nGenerated.setNotificationType(notificationMessage.getNotificationType());
                
        return nGenerated;
    }
    
    public List<NotificationGenerated> generateNotficationGenerated(List<NotificationMessage> nMessages ){
        Map<Integer, List<NotificationMedium>> typeMediumMapping = nMappingService.getTypeMediumMapping();
                
        NotificationType nType = null;
        List<NotificationMedium> nMediums = null;
        List<NotificationGenerated> generatedList = new ArrayList<NotificationGenerated>();
        NotificationGenerated nGenerated = null;
        for(NotificationMessage nMessage:nMessages){
            nType = nMessage.getNotificationType();
            nMediums = typeMediumMapping.get(nType.getId());
            // TODO handle the scenario when no mapping of notification medium with type.
            if(nMediums == null || nMediums.size() < 1){
                continue;
            }
            for(NotificationMedium nMedium:nMediums){
                nGenerated = createNotificationGenerated(nMessage, nMedium);
                nGenerated = save(nGenerated);
                generatedList.add(nGenerated);
            }
        }
        
        return generatedList;
    }
    
    public NotificationGenerated getLastScheduledOrSendNotificationGeneratedSameAs(NotificationGenerated ntGenerated) {
        List<NotificationStatus> notificationStatusList = new ArrayList<NotificationStatus>();
        notificationStatusList.add(NotificationStatus.Scheduled);
        notificationStatusList.add(NotificationStatus.Sent);
        List<NotificationGenerated> ntGeneratedList = notificationGeneratedDao.getLastNotificationGenerated(
                notificationStatusList,
                ntGenerated.getNotificationMedium().getId(),
                ntGenerated.getForumUser().getUserId(),
                ntGenerated.getNotificationType().getId(),
                ntGenerated.getObjectId());
        if (ntGeneratedList !=null && !ntGeneratedList.isEmpty()) {
            return ntGeneratedList.get(0);
        }
        return null;
    }
    
    public NotificationGenerated getLastScheduledOrSentNotificationGeneratedInMediumSameAs(NotificationGenerated ntGenerated) {
        List<NotificationStatus> notificationStatusList = new ArrayList<NotificationStatus>();
        notificationStatusList.add(NotificationStatus.Scheduled);
        notificationStatusList.add(NotificationStatus.Sent);
        List<NotificationGenerated> ntGeneratedList = notificationGeneratedDao.getLastSentNotificationGeneratedInMedium(
                notificationStatusList,
                ntGenerated.getForumUser().getUserId(),
                ntGenerated.getNotificationMedium().getId());
        if (ntGeneratedList !=null && !ntGeneratedList.isEmpty()) {
            return ntGeneratedList.get(0);
        }
        return null;
    }

    public void updateNotificationGeneratedStatusOnOldStatus(Integer id, NotificationStatus newStatus, NotificationStatus oldStatus) {
        notificationGeneratedDao.updateByNotificationStatusOnOldNotificationStatus(id, newStatus, oldStatus);
    }
    
    public List<NotificationGenerated> getRawNotificationGeneratedList() {
        return notificationGeneratedDao.findByNotificationStatus(NotificationStatus.Generated);
    }
}
