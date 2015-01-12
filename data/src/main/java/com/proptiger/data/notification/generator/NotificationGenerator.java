package com.proptiger.data.notification.generator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.pojo.LimitOffsetPageRequest;
import com.proptiger.data.notification.generator.handler.NotificationProcessorHandler;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.processor.dto.NotificationProcessorDto;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.notification.service.NotificationProcessorDtoService;
import com.proptiger.data.notification.service.SubscriberConfigService;
import com.proptiger.data.util.Serializer;

@Service
public class NotificationGenerator {
    private static Logger                   logger = LoggerFactory.getLogger(NotificationGenerator.class);

    @Autowired
    private NotificationMessageService      notificationMessageService;

    @Autowired
    private NotificationProcessorHandler    notificationProcessorHandler;

    @Autowired
    private NotificationGeneratedService    notificationGeneratedService;

    @Autowired
    private NotificationProcessorDtoService nDtoService;

    @Autowired
    private SubscriberConfigService         subscriberConfigService;

    public boolean isNotificationGeneratedGenerationRequired() {
        Long activeNGCount = notificationGeneratedService.getNumberOfActiveNotificationGenerated();
        Integer maxActiveNGCount = subscriberConfigService.getMaxActiveNotificationGeneratedCount();

        if (activeNGCount < maxActiveNGCount) {
            logger.debug("NotificationGenerated Generation required as activeNGCount " + activeNGCount
                    + " is less than maxActiveNGCount "
                    + maxActiveNGCount);
            return true;
        }
        logger.debug("NotificationGenerated Generation not required as activeNGCount " + activeNGCount
                + " is greater then or equal to maxActiveNGCount "
                + maxActiveNGCount);
        return false;
    }

    public Integer generateNotifications() {
        logger.info("Retrieving the notification messages from database.");
        // TODO to handle the pageable condition.
        List<NotificationMessage> notificationMessages = notificationMessageService.getRawNotificationMessages();

        logger.info("Fetch " + notificationMessages.size() + " messages from the database.");
        logger.debug("Notification Messages Retrieved " + Serializer.toJson(notificationMessages));

        List<NotificationGenerated> scheduledNotificationGeneratedList = notificationGeneratedService
                .getScheduledAndNonReadyNotifications();

        logger.info("Fetch " + scheduledNotificationGeneratedList.size()
                + " scheduled Notification Generated from database.");
        logger.debug("Notification Generated Retrieved " + Serializer.toJson(scheduledNotificationGeneratedList));

        List<NotificationProcessorDto> nDtos = nDtoService.buildPrimaryKeyDto(
                notificationMessages,
                scheduledNotificationGeneratedList);

        logger.debug(" BUILD PROCESSOR DTO " + Serializer.toJson(nDtos));

        List<NotificationGenerated> generatedNotifications = new ArrayList<NotificationGenerated>();

        for (NotificationProcessorDto intraProcessorDto : nDtos) {
            notificationProcessorHandler.handleNotificationMessage(intraProcessorDto);
            generatedNotifications.addAll(nDtoService.PersistProcessesNotifications(intraProcessorDto));
        }

        return generatedNotifications.size();
    }

}
