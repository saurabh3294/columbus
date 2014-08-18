package com.proptiger.data.notification.sender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.service.NotificationTypeNotificationMediumMappingService;

@Service
public class TemplateGenerator {
    private static Logger      logger = LoggerFactory.getLogger(TemplateGenerator.class);
    
    private static Pattern PATTERN = Pattern.compile("(<.+?>)");

    @Autowired
    private NotificationTypeNotificationMediumMappingService ntNmMappingService;

    public MailBody generateMailBodyFromTemplate(NotificationGenerated ntGenerated) {
        String template = ntNmMappingService.getTemplate(ntGenerated);
        Map<String, Object> payloadDataMap = ntGenerated.getNotificationMessagePayload().getPayloadDataMap();
        return getMailBody(ntGenerated, template, payloadDataMap);
    }

    private MailBody getMailBody(NotificationGenerated ntGenerated, String template, Map<String, Object> payloadDataMap) {
        if (template == null || template.isEmpty()) {
            logger.info("Mail Template is null or empty");
            return null;
        }
        
        if (payloadDataMap == null || payloadDataMap.isEmpty()) {
            logger.info("payLoad Data Map is null or empty");
            return null;
        }
        
        HashMap<String, String> mailContentMap = getMailContentFromJsonTemplate(template);
        if (mailContentMap == null || mailContentMap.isEmpty()) {
            return null;
        }
        
        String subject = replaceTokensWithValue(mailContentMap.get("subject"), payloadDataMap);
        String body = replaceTokensWithValue(mailContentMap.get("body"), payloadDataMap);
        
        if (subject == null || subject.isEmpty() || body == null || body.isEmpty()) {
            logger.info("Mail Subject or Body is null or empty");
            //TO DO 
            //if payloadDataMap does not contain any of the token value in the template
            // then by default the respective information will be retrieved from DB and it's
            // implementation will be added in phase 2.
            return null;
        }
        
        //creating mail body and setting mail subject and mail body.
        MailBody mailBody = new MailBody();
        mailBody.setSubject(subject);
        mailBody.setBody(body);
        return mailBody;
    }

    private HashMap<String, String> getMailContentFromJsonTemplate(String template) {
        HashMap<String,String> map = new HashMap<String,String>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(template, HashMap.class);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String replaceTokensWithValue(String template, Map<String, Object> payloadDataMap) {
        List<String> tokens = getTokenList(template);
        if (tokens == null || tokens.isEmpty()) {
            return template;
        }
        for(String token : tokens) {
            String tokenValue = (String) payloadDataMap.get(token.substring(1, token.length()-1));
            if (tokenValue == null ) {
                logger.info("Token value NOT present in payload Data Map for token -" + token);
                return null;
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
