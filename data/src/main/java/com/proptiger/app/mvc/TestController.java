package com.proptiger.app.mvc;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.B2b;
import com.proptiger.data.model.Bank;
import com.proptiger.data.model.Locality;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.service.B2bService;
import com.proptiger.data.service.BankService;
import com.proptiger.data.service.LocalityService;

@Controller
@RequestMapping(value="test")
public class TestController extends BaseController {
	
	@Autowired
	private BankService bankService;
	
	@Autowired
    private B2bService b2bService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse test(@RequestParam(required = false, value = "selector") String selectorStr){
		Selector testSelector = super.parseJsonToObject(selectorStr, Selector.class);
		List<Bank> list = bankService.getBanks();
		
		List<B2b> docList = b2bService.getFilteredDocuments(testSelector);
		
		System.out.println("Here" + testSelector);
		Set<String> fieldsToSerialize = null;
		if(testSelector != null){
			fieldsToSerialize = testSelector.getFields();
		}
		//return new ProAPISuccessCountResponse(super.filterFields(list, fieldsToSerialize), list.size());
		return new ProAPISuccessCountResponse(super.filterFields(list, fieldsToSerialize), list.size());
	}
}
