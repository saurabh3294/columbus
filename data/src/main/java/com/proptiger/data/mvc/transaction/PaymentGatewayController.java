/**
 * 
 */
package com.proptiger.data.mvc.transaction;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.config.scheduling.QuartzScheduledClass;
import com.proptiger.core.config.scheduling.QuartzScheduledJob;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.transaction.CitrusPayPGService;

/**
 * @author mandeep
 * 
 */
@Controller
@QuartzScheduledClass
public class PaymentGatewayController {
    @Autowired
    private CitrusPayPGService citrusPayPGService;

    @ResponseBody
    @RequestMapping(value = "app/v1/citrus-pay-pg/payment", method=RequestMethod.POST)
    public APIResponse handlePaymentResponse(@RequestParam Map<String, Object> allRequestParams) {
        citrusPayPGService.handlePaymentResponse(allRequestParams);
        return new APIResponse();
    }

    @QuartzScheduledJob(cron="0 */15 * * * ?")
    public void updateTransactions() {
        citrusPayPGService.updateRefundableTransaction();
    }
}
