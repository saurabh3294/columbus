package com.proptiger.mail.service;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.proptiger.data.internal.dto.mail.MailBody;

/**
 * This class generates html body for mail based on template file passed
 * 
 * @author Rajeev Pandey
 * 
 */
@Component
public class TemplateToHtmlGenerator {

    @Autowired
    private VelocityEngine velocityEngine;
    private static String  ENCODING_UTF8 = "UTF-8";

    public MailBody generateMailBody(MailTemplateDetail mailTemplateName, Object dataObject) {
        String body = generateHtmlFromTemplate(
                mailTemplateName.getKey(),
                mailTemplateName.getBodyTemplate(),
                dataObject);
        String subject = generateHtmlFromTemplate(
                mailTemplateName.getKey(),
                mailTemplateName.getSubjectTemplate(),
                dataObject);

        MailBody mailbody = new MailBody();
        mailbody.setBody(body);
        mailbody.setSubject(subject);
        return mailbody;
    }

    /**
     * Generate html using template file and data from dataObject
     * 
     * @param key
     * @param templateFilePath
     * @param dataObject
     * @return
     */
    public String generateHtmlFromTemplate(String key, String templateFilePath, Object dataObject) {
        String text = "";
        Map<String, Object> map = new HashMap<>();
        map.put(key, dataObject);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        map.put("currentDateTime", dateFormat.format(new Date()));

        text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateFilePath, ENCODING_UTF8, map);

        return text;
    }

    public String generateHtmlFromTemplate(Map<String, Object> map, String templateFilePath) {
        Locale locale = Locale.getDefault();
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        map.put("numberFormatter", nf);

        NumberFormat nfWithTwoDecimal = NumberFormat.getInstance(locale);
        nfWithTwoDecimal.setMaximumFractionDigits(2);
        nfWithTwoDecimal.setMinimumFractionDigits(0);
        nfWithTwoDecimal.setRoundingMode(RoundingMode.HALF_UP);
        map.put("numberFormatterDecimal", nfWithTwoDecimal);
        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateFilePath, ENCODING_UTF8, map);
        if (text != null) {
            text = text.replaceAll("[\\n\\t]", "").trim();
            text = text.replaceAll("\\s+", " ");
        }
        return text;
    }
}
