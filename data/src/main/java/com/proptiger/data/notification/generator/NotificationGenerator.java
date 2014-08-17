package com.proptiger.data.notification.generator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.generator.handler.NotificationProcessorHandler;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.processor.dto.NotificationIntraProcessorDto;
import com.proptiger.data.notification.service.NotificationGeneratedService;
import com.proptiger.data.notification.service.NotificationIntraProcessorDtoService;
import com.proptiger.data.notification.service.NotificationMessageService;
import com.proptiger.data.pojo.LimitOffsetPageRequest;

@Service
public class NotificationGenerator {
    @Autowired
    private NotificationMessageService   notificationMessageService;

    @Autowired
    private NotificationProcessorHandler notificationProcessorHandler;

    @Autowired
    private NotificationGeneratedService notificationGeneratedService;
    
    @Autowired
    private NotificationIntraProcessorDtoService nDtoService;

    public Integer generateNotifications() {
        // TODO to handle the pageable condition.
        List<NotificationMessage> notificationMessages = notificationMessageService
                .getRawNotificationMessages(new LimitOffsetPageRequest(0, 1));

        List<NotificationGenerated> scheduledNotificationGeneratedList = notificationGeneratedService
                .getScheduledAndNonExpiredNotifications();

        List<NotificationIntraProcessorDto> nDtos = nDtoService.buildDto(notificationMessages, scheduledNotificationGeneratedList);       

        for(NotificationIntraProcessorDto intraProcessorDto:nDtos){
            notificationProcessorHandler.handleNotificationMessage(
                    intraProcessorDto.getNotificationByTypeDtos(), intraProcessorDto.getUserId());
            // finalProcessedNotificationMessages.addAll(processedNotificationMessages);
        }
        // List<NotificationMessage> finalNotificationMessages =
        // userNotificationHandler.

        return null;
    }

    
}
