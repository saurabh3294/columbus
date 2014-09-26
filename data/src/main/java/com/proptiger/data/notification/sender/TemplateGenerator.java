package com.proptiger.data.notification.sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.service.NotificationTypeNotificationMediumMappingService;

@Service
public class TemplateGenerator {

    private static Logger                                    logger  = LoggerFactory.getLogger(TemplateGenerator.class);

    private static final Pattern                             PATTERN = Pattern.compile("(<.+?>)");

    @Autowired
    private NotificationTypeNotificationMediumMappingService ntNmMappingService;

    public String generatePopulatedTemplate(NotificationGenerated ntGenerated) {
        String template = ntNmMappingService.getTemplate(ntGenerated);
        logger.debug("Template: " + template);
        Map<String, Object> payloadDataMap = ntGenerated.getNotificationMessagePayload().getExtraAttributes();
        logger.debug("PayloadDataMap: " + payloadDataMap.toString());

        if (template == null || template.isEmpty()) {
            logger.info("Mail Template is null or empty");
            return null;
        }

        if (payloadDataMap == null || payloadDataMap.isEmpty()) {
            logger.info("payLoad Data Map is null or empty");
            return null;
        }

        String populatedTemplate = replaceTokensWithValue(template, payloadDataMap);

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

    private String replaceTokensWithValue(String template, Map<String, Object> payloadDataMap) {
        List<String> tokens = getTokenList(template);
        if (tokens == null || tokens.isEmpty()) {
            return template;
        }
        for (String token : tokens) {
            Object tokenValueObj = payloadDataMap.get(token.substring(1, token.length() - 1));
            if (tokenValueObj == null) {
                logger.error("Token value NOT present in payload Data Map for token - " + token);
                // Returning the token value same as token
                tokenValueObj = token;
            }
            String tokenValue = "";
            if (!(tokenValueObj instanceof String)) {
                tokenValue = tokenValueObj + "";
            }
            else {
                tokenValue = (String) tokenValueObj;
            }
            template = template.replaceAll(token, tokenValue);
        }
        return template;
    }

    private List<String> getTokenList(String template) {
        List<String> list = new ArrayList<String>();
        Matcher match = PATTERN.matcher(template);
        while (match.find()) {
            list.add(match.group());
        }
        return list;
    }
}
