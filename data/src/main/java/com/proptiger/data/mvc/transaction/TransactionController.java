/**
 * 
 */
package com.proptiger.data.mvc.transaction;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.enums.transaction.PaymentType;
import com.proptiger.data.model.enums.transaction.TransactionType;
import com.proptiger.data.model.transaction.Transaction;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.transaction.CitrusPayPGService;
import com.proptiger.data.service.transaction.TransactionService;
import com.proptiger.exception.BadRequestException;

/**
 * @author mandeep
 * 
 */
@Controller
@DisableCaching
public class TransactionController extends BaseController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CitrusPayPGService citrusPayPGService;

    @ResponseBody
    @RequestMapping(value = "data/v1/transaction/coupon", method = RequestMethod.POST)
    public APIResponse createCouponTransaction(@RequestBody Transaction transaction) {
        transaction.setTypeId(TransactionType.BuyCoupon.getId());
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        URI uri = citrusPayPGService.initiatePaymentRequest(createdTransaction);

        if (uri == null) {
            throw new BadRequestException(
                    ResponseCodes.GATEWAY_ERROR,
                    "Some Problem with Gateway. Please try again after some time.");
        }
        return new APIResponse(uri);
    }
    
    @ResponseBody
    @RequestMapping(value = "data/v1/transaction/offline-coupon", method = RequestMethod.POST)
    public APIResponse createOfflineCouponTransaction(@RequestBody Transaction transaction, @RequestParam(defaultValue = "Cheque", required = false) PaymentType paymentType) {
        transaction.setTypeId(TransactionType.BuyCoupon.getId());
        Transaction createdTransaction = transactionService.createOfflineCoupon(transaction, paymentType);
        
        return new APIResponse(createdTransaction);
    }

    @ResponseBody
    @RequestMapping(value = {
            "data/v1/entity/user/transaction/{transactionId}",
            "data/v1/entity/transaction/{transactionId}" })
    public APIResponse get(@PathVariable int transactionId) {
        return new APIResponse(transactionService.getUpdatedTransaction(transactionId));
    }
}
