package com.proptiger.data.mvc;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Bank;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.BankService;

/**
 * This class provides the API to get bank details
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/bank")
public class BankController extends BaseController {

    @Autowired
    private BankService bankService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getBankList(@RequestParam(required = false, value = "selector") String selectorStr) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        List<Bank> list = bankService.getBanks();
        Set<String> fieldsToSerialize = null;
        if (selector != null) {
            fieldsToSerialize = selector.getFields();
        }
        return new APIResponse(super.filterFields(list, fieldsToSerialize), list.size());
    }

    @RequestMapping(value = "/{bankId}", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getBank(
            @PathVariable Integer bankId,
            @RequestParam(required = false, value = "selector") String selectorStr) {
        Selector selector = super.parseJsonToObject(selectorStr, Selector.class);
        Bank bank = bankService.getBank(bankId);
        Set<String> fieldsToSerialize = null;
        if (selector != null) {
            fieldsToSerialize = selector.getFields();
        }
        return new APIResponse(super.filterFields(bank, fieldsToSerialize), 1);
    }
}
