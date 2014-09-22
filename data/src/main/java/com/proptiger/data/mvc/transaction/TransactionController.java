/**
 * 
 */
package com.proptiger.data.mvc.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.enums.transaction.TransactionType;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.transaction.CitrusPayPGService;
import com.proptiger.data.service.transaction.TransactionService;

/**
 * @author mandeep
 *
 */
@Controller
public class TransactionController extends BaseController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CitrusPayPGService citrusPayPGService;

    @ResponseBody
    @RequestMapping(value="data/v1/transaction/coupon", method=RequestMethod.POST)
    public APIResponse createCouponTransaction(@RequestBody Transaction transaction) {
        transaction.setTypeId(TransactionType.BuyCoupon.getId());
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return new APIResponse(citrusPayPGService.initiatePaymentRequest(createdTransaction));
    }

    @ResponseBody
    @RequestMapping(value="data/v1/entity/user/transaction/{transactionId}")
    public APIResponse get(@RequestParam int transactionId) {
        return new APIResponse(transactionService.getUpdatedTransaction(transactionId));
    }    
}
