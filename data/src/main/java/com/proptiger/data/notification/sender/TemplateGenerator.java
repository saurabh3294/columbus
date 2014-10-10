package com.proptiger.data.notification.sender;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
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
    private NotificationTypeNotificationMediumMappingService ntNmMappingService;

    public String generatePopulatedTemplate(NotificationGenerated nGenerated) {
        String template = ntNmMappingService.getTemplate(nGenerated);
        logger.debug("Template: " + template);
        Map<String, Object> payloadDataMap = nGenerated.getNotificationMessagePayload().getExtraAttributes();
        logger.debug("PayloadDataMap: " + payloadDataMap.toString());

        if (template == null || template.isEmpty()) {
            logger.info("Mail Template is null or empty");
            return null;
        }

        if (payloadDataMap == null || payloadDataMap.isEmpty()) {
            logger.info("payLoad Data Map is null or empty");
            return null;
        }

        String populatedTemplate = populateTemplate(template, payloadDataMap, nGenerated.getNotificationType()
                .getName());

        if (populatedTemplate == null || populatedTemplate.isEmpty()) {
            logger.info("Template token values not found in the payloadData map");
            // TO DO
            // if payloadDataMap does not contain any of the token value in the
            // template
            // then by default the respective information will be retrieved from
            // DB and it's
            // implementation will be added in phase 2.
            return null;
        }

        return populatedTemplate;
    }

    private String populateTemplate(String template, Map<?, ?> dataMap, String logTag) {
        VelocityContext context = new VelocityContext(dataMap);
        StringWriter writer = new StringWriter();
        Velocity.init();
        Velocity.evaluate(context, writer, logTag, template);
        return writer.getBuffer().toString();
    }
}
