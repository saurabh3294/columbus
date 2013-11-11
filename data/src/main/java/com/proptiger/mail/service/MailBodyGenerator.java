package com.proptiger.mail.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * This class generates html body for mail based on template file passed
 * @author Rajeev Pandey
 *
 */
@Component
public class MailBodyGenerator {

	@Autowired
	private VelocityEngine velocityEngine;
	
	public String generateHtmlBody(MailTemplateDetail mailTemplateName, Object dataObject){

		Map<String, Object> map = new HashMap<>();
		map.put(mailTemplateName.getKey(), dataObject);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
		map.put("currentDateTime", dateFormat.format(new Date()));
	    String body = VelocityEngineUtils.mergeTemplateIntoString(
                 velocityEngine, mailTemplateName.getTemplateFileName(), "UTF-8", map);
		
		return body;
	}
}
