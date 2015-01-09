package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.notification.NotificationTypeEnum;
import com.proptiger.data.notification.exception.NotificationTypeNotFoundException;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeConfig;
import com.proptiger.data.notification.repo.NotificationTypeDao;

@Service
public class NotificationTypeService {

    private static final String DEFAULT_NOTIFICATION_TYPE = "default";

    @Autowired
    private ApplicationContext  applicationContext;

    @Autowired
    private NotificationTypeDao notificationTypeDao;

    /**
     * Populate NotificationTypeConfig in NotificationType using the static map
     * defined in the code.
     * 
     * @param notificationType
     * @return
     */
    public NotificationType populateNotificationTypeConfig(NotificationType notificationType) {

        // Get the notificationTypeName whose config needs to be used
        String configName = notificationType.getName();
        if (notificationType.getOverwriteConfigName() != null) {
            configName = notificationType.getOverwriteConfigName();
        }

        // Find the config from the static map.
        // Use defaults if no config is found.
        NotificationTypeConfig savedNTConfig = NotificationTypeConfig.getNotificationTypeConfigMap().get(configName);
        if (savedNTConfig == null) {
            savedNTConfig = new NotificationTypeConfig();
        }

        savedNTConfig = setNotificationTypeConfigObjectAttributes(savedNTConfig);
        notificationType.setNotificationTypeConfig(savedNTConfig);
        return notificationType;
    }

    private NotificationTypeConfig setNotificationTypeConfigObjectAttributes(
            NotificationTypeConfig notificationTypeConfig) {

        try {
            notificationTypeConfig.setNotificationTypePayloadObject(notificationTypeConfig.getDataClassName()
                    .newInstance());
            notificationTypeConfig.setNonPrimaryKeyProcessorObject(applicationContext.getBean(notificationTypeConfig
                    .getNonPrimaryKeyProcessorClassName()));
            notificationTypeConfig.setPrimaryKeyProcessorObject(applicationContext.getBean(notificationTypeConfig
                    .getPrimaryKeyProcessorClassName()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        notificationTypeConfig.setNotificationTypeProcessorObject(applicationContext.getBean(notificationTypeConfig
                .getNotificationTypeProcessorClassName()));
        notificationTypeConfig.setNotificationMessageProcessorObject(applicationContext.getBean(notificationTypeConfig
                .getNotificationMessageProcessorClassName()));
        notificationTypeConfig.setTemplateDataFetcherObject(applicationContext.getBean(notificationTypeConfig
                .getTemplateDataFetcherClassName()));

        return notificationTypeConfig;
    }

    public Map<Integer, Integer> NotificationInterPrimaryKeySupressGroupingMap() {
        Iterable<NotificationType> notiIterable = findAllNotificationTypes();

        Map<Integer, Integer> mapping = new LinkedHashMap<Integer, Integer>();

        Iterator<NotificationType> it = notiIterable.iterator();
        NotificationType notificationType = null;
        Integer parentNotificationTypeId = null;

        while (it.hasNext()) {
            notificationType = it.next();

            if (notificationType.getInterPrimaryKeySuppressId() != null) {

                parentNotificationTypeId = notificationType.getInterPrimaryKeySuppressId();
                mapping.put(parentNotificationTypeId, notificationType.getId());
            }
        }

        return mapping;
    }

    public Map<Integer, Integer> getNotificationInterPrimaryKeySupressGroupingMap() {
        Iterable<NotificationType> notiIterable = findAllNotificationTypes();

        Map<Integer, Integer> mapping = new LinkedHashMap<Integer, Integer>();

        Iterator<NotificationType> it = notiIterable.iterator();
        NotificationType notificationType = null;
        Integer parentNotificationTypeId = null;

        while (it.hasNext()) {
            notificationType = it.next();

            if (notificationType.getInterPrimaryKeySuppressId() != null) {

                parentNotificationTypeId = notificationType.getInterPrimaryKeySuppressId();
                mapping.put(parentNotificationTypeId, notificationType.getId());
            }
        }

        return mapping;
    }

    public Map<Integer, Integer> getNotificationInterNonPrimaryKeySupressGroupingMap() {
        Iterable<NotificationType> notiIterable = findAllNotificationTypes();

        Map<Integer, Integer> mapping = new LinkedHashMap<Integer, Integer>();

        Iterator<NotificationType> it = notiIterable.iterator();
        NotificationType notificationType = null;
        Integer parentNotificationTypeId = null;

        while (it.hasNext()) {
            notificationType = it.next();

            if (notificationType.getInterNonPrimaryKeySuppressId() != null) {

                parentNotificationTypeId = notificationType.getInterNonPrimaryKeySuppressId();
                mapping.put(parentNotificationTypeId, notificationType.getId());
            }
        }

        return mapping;
    }

    public Map<Integer, List<Integer>> notificationInterKeyMergeGroupingMap() {

        Iterable<NotificationType> notiIterable = findAllNotificationTypes();
        Map<Integer, List<Integer>> mapping = new LinkedHashMap<Integer, List<Integer>>();

        Iterator<NotificationType> it = notiIterable.iterator();
        NotificationType notificationType = null;
        Integer parentNotificationTypeId = null;
        List<Integer> childNotificationTypeList = null;

        while (it.hasNext()) {
            notificationType = it.next();

            if (notificationType.getInterPrimaryKeyMergeId() != null) {

                parentNotificationTypeId = notificationType.getInterPrimaryKeyMergeId();
                childNotificationTypeList = mapping.get(parentNotificationTypeId);

                if (childNotificationTypeList == null) {
                    childNotificationTypeList = new ArrayList<Integer>();
                }

                childNotificationTypeList.add(notificationType.getId());
                mapping.put(parentNotificationTypeId, childNotificationTypeList);
            }
        }

        return mapping;
    }

    public Map<Integer, List<Integer>> notificationInterNonKeyMergeGroupingMap() {

        Iterable<NotificationType> notiIterable = findAllNotificationTypes();
        Map<Integer, List<Integer>> mapping = new LinkedHashMap<Integer, List<Integer>>();

        Iterator<NotificationType> it = notiIterable.iterator();
        NotificationType notificationType = null;
        Integer parentNotificationTypeId = null;
        List<Integer> childNotificationTypeList = null;

        while (it.hasNext()) {
            notificationType = it.next();

            if (notificationType.getInterNonPrimaryKeyMergeId() != null) {

                parentNotificationTypeId = notificationType.getInterNonPrimaryKeyMergeId();
                childNotificationTypeList = mapping.get(parentNotificationTypeId);

                if (childNotificationTypeList == null) {
                    childNotificationTypeList = new ArrayList<Integer>();
                }

                childNotificationTypeList.add(notificationType.getId());
                mapping.put(parentNotificationTypeId, childNotificationTypeList);
            }
        }

        return mapping;
    }

    public Iterable<NotificationType> findAllNotificationTypes() {
        Iterable<NotificationType> nIterable = notificationTypeDao.findAll();
        Iterator<NotificationType> it = nIterable.iterator();

        while (it.hasNext()) {
            populateNotificationTypeConfig(it.next());
        }

        return nIterable;
    }

    public NotificationType findOne(Integer notificationTypeId) {
        NotificationType nType = notificationTypeDao.findOne(notificationTypeId);
        populateNotificationTypeConfig(nType);
        return nType;
    }

    public NotificationType findByName(String notificationTypeName) {
        List<NotificationType> nTypes = notificationTypeDao.findByName(notificationTypeName);
        if (nTypes == null || nTypes.size() != 1) {
            throw new NotificationTypeNotFoundException(
                    "Zero or more than one Notification Type : " + notificationTypeName + " found in DB");
        }
        NotificationType nType = nTypes.get(0);
        populateNotificationTypeConfig(nType);
        return nType;
    }

    public NotificationType findDefaultNotificationType() {
        return findByName(DEFAULT_NOTIFICATION_TYPE);
    }

    public List<NotificationType> findByNotificationTypeEnums(List<NotificationTypeEnum> notificationTypeEnums) {
        List<String> names = new ArrayList<String>();
        for (NotificationTypeEnum notificationType : notificationTypeEnums) {
            names.add(notificationType.getName());
        }
        return notificationTypeDao.findByNames(names);
    }
}
