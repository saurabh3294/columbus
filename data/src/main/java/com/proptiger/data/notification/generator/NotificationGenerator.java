package com.proptiger.data.notification.generator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.notification.NotificationInitiator;
import com.proptiger.data.notification.generator.handler.NotificationProcessorHandler;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.processor.dto.NotificationProcessorDto;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationProcessorDtoService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.pojo.LimitOffsetPageRequest;
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

    public Integer generateNotifications() {
        logger.info("Retrieving the notification messages from database.");
        // TODO to handle the pageable condition.
        List<NotificationMessage> notificationMessages = notificationMessageService
                .getRawNotificationMessages(new LimitOffsetPageRequest(0, 1));
        
        logger.info("Fetch "+notificationMessages.size()+" messages from the database.");
        try {
            logger.debug("Notification Messages Retrieved "+new ObjectMapper().writeValueAsString(notificationMessages));
        }
        catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        List<NotificationGenerated> scheduledNotificationGeneratedList = notificationGeneratedService
                .getScheduledAndNonExpiredNotifications();
        
        logger.info("Fetch "+scheduledNotificationGeneratedList.size()+" scheduled Notification Generated from database.");
        try {
            logger.debug("Notification Generated Retrieved "+new ObjectMapper().writeValueAsString(scheduledNotificationGeneratedList));
        }
        catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        List<NotificationProcessorDto> nDtos = nDtoService.buildPrimaryKeyDto(
                notificationMessages,
                scheduledNotificationGeneratedList);
        
        logger.debug(" BUILD PROCESSOR DTO "+Serializer.toJson(nDtos));
        
        List<NotificationGenerated> generatedNotifications = new ArrayList<NotificationGenerated>();

        for (NotificationProcessorDto intraProcessorDto : nDtos) {
            notificationProcessorHandler.handleNotificationMessage(intraProcessorDto);
            generatedNotifications.addAll(nDtoService.PersistProcessesNotifications(intraProcessorDto));
        }

        return generatedNotifications.size();
    }

}
