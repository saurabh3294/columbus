package com.proptiger.mail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.proptiger.data.internal.dto.MailObject;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.mail.service.MailService;

/**
 * Mail API
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/mail")
public class MailController {

	@Autowired
	private MailService mailService;
	
	@RequestMapping(method = RequestMethod.POST)
	public ProAPIResponse sendMail(@PathVariable Integer userId,@RequestBody MailObject mailObject){
		
		return new ProAPISuccessResponse();
	}
}
