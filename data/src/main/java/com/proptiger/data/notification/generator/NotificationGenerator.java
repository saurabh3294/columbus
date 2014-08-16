package com.proptiger.data.notification.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.NotFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.generator.handler.NotificationProcessorHandler;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.legacy.NotificationPayloadOld;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.dto.NotificationByKey;
import com.proptiger.data.notification.processor.dto.NotificationByTypeDto;
import com.proptiger.data.notification.processor.dto.NotificationIntraProcessorDto;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
import com.sun.tools.javac.util.Pair;

@Service
public class NotificationGenerator {
    @Autowired
    private NotificationMessageService   notificationMessageService;

    @Autowired
    private NotificationProcessorHandler notificationProcessorHandler;

    @Autowired
    private NotificationGeneratedService notificationGeneratedService;

    public Integer generateNotifications() {
        // TODO to handle the pageable condition.
        List<NotificationMessage> notificationMessages = notificationMessageService
                .getRawNotificationMessages(new LimitOffsetPageRequest(0, 1));

        List<NotificationGenerated> scheduledNotificationGeneratedList = notificationGeneratedService
                .getScheduledAndNonExpiredNotifications();

        Map<Integer, List<NotificationMessage>> groupNotificationMessagesByuser = notificationMessageService
                .groupNotificationMessageByuser(notificationMessages);

        Map<Integer, List<NotificationGenerated>> groupNotificationGeneratedByuser = notificationGeneratedService
                .groupNotificationGeneratedByuser(scheduledNotificationGeneratedList);

        // List<NotificationMessage> finalProcessedNotificationMessages = new
        // ArrayList<NotificationMessage>();
        List<NotificationMessage> processedNotificationMessages = null;

        for (Map.Entry<Integer, List<NotificationMessage>> entry : groupNotificationMessagesByuser.entrySet()) {
            processedNotificationMessages = notificationProcessorHandler.handleNotificationMessage(
                    entry.getValue(),
                    groupNotificationGeneratedByuser.get(entry.getKey()));
            // finalProcessedNotificationMessages.addAll(processedNotificationMessages);
        }
        // List<NotificationMessage> finalNotificationMessages =
        // userNotificationHandler.

        return null;
    }

    
}
