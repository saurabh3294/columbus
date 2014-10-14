package com.proptiger.data.notification.sender;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.service.NotificationTypeNotificationMediumMappingService;

@Service
public class TemplateGenerator {

    private static Logger                                    logger = LoggerFactory.getLogger(TemplateGenerator.class);

    @Autowired
    private VelocityEngine                                   velocityEngine;

    @Autowired
    private NotificationTypeNotificationMediumMappingService ntNmMappingService;

    public String generatePopulatedTemplate(NotificationGenerated nGenerated) {
        String template = ntNmMappingService.getTemplate(nGenerated.getNotificationType().getId(), nGenerated
                .getNotificationMedium().getId());
        logger.debug("Template: " + template);
        Map<String, Object> payloadDataMap = nGenerated.getNotificationMessagePayload().getExtraAttributes();
        logger.debug("PayloadDataMap: " + payloadDataMap.toString());

        if (template == null || template.isEmpty()) {
            logger.error("Mail Template is null or empty for notificationGenerated id: " + nGenerated.getId());
            return null;
        }

        if (payloadDataMap == null || payloadDataMap.isEmpty()) {
            logger.error("Payload Data Map is null or empty for notificationGenerated id: " + nGenerated.getId());
            return null;
        }

        String populatedTemplate = null;
        try {
            populatedTemplate = populateTemplate(template, payloadDataMap, nGenerated.getNotificationType().getName());
        }
        catch (Exception e) {
            logger.error("Got exception: " + e
                    + " while populating template via Velocity for Template: "
                    + template
                    + " and PayloadDataMap: "
                    + payloadDataMap.toString());
            throw e;
        }

        if (populatedTemplate == null || populatedTemplate.isEmpty()) {
            logger.error("Velocity unable to populate template. Template: " + template
                    + " and PayloadDataMap: "
                    + payloadDataMap.toString());
            return null;
        }

        return populatedTemplate;
    }

    private String populateTemplate(String template, Map<?, ?> dataMap, String logTag) {
        VelocityContext context = new VelocityContext(dataMap);
        StringWriter writer = new StringWriter();
        if (!velocityEngine.evaluate(context, writer, logTag, template)) {
            return null;
        }
        return writer.getBuffer().toString();
    }
}
