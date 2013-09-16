package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.BlogNewsService;

@Controller
@RequestMapping(value = "v1/entity/blognews")
public class BlogNewsController extends BaseController{

	@Autowired
	private BlogNewsService blogNewsService;
	
	@RequestMapping
	@ResponseBody
	public ProAPIResponse getBlogNews(){
		return new ProAPISuccessResponse(blogNewsService.getBlogNews());
	}
}
