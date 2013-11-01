package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Bank;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.BankService;

/**
 * This class provides the API to get bank details
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/bank")
public class BankController {
	
	@Autowired
	private BankService bankService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getBankList(){
		List<Bank> list = bankService.getBanks();
		return new ProAPISuccessCountResponse(list, list.size());
	}
	
	@RequestMapping(value = "/{bankId}", method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getBank(@PathVariable Integer bankId){
		Bank bank = bankService.getBank(bankId);
		return new ProAPISuccessResponse(bank);
	}
}
