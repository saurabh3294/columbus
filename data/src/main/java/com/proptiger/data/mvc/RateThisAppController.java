/**
 * 
 */
package com.proptiger.data.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.meta.DisableCaching;

/**
 * @author mandeep
 * 
 */
@Controller
public class RateThisAppController extends BaseController {

    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    @SuppressWarnings("unused")
    private static class RateDialogAttributes {
        private int    launchCount = 6;
        private int    dayCount    = 3;
        private String message     = "If you enjoy using PropTiger, please take a moment to rate us. Thanks for your support!";
    }

    @RequestMapping("app/v1/rate-dialog-attrs")
    @DisableCaching
    @ResponseBody
    public APIResponse getRateDialogModel() {
        return new APIResponse(new RateDialogAttributes());
    }
}
