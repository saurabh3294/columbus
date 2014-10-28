package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.model.user.User;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.service.CouponCatalogueService;

@Controller
@RequestMapping("data/v1/coupon/")
@DisableCaching
public class CouponCatalogController extends BaseController {

    @Autowired
    private CouponCatalogueService couponService;

    @ResponseBody
    @RequestMapping(value = "{couponCode}/redeem", method = RequestMethod.POST)
    public APIResponse redeemCoupon(@PathVariable String couponCode, @RequestParam String userProofId) {
        couponService.redeemCoupon(couponCode, userProofId);
        return new APIResponse("Coupon Has been redeemed.");
    }

    @ResponseBody
    @RequestMapping("{couponCode}/user-details")
    public APIResponse fetchUserDetails(@PathVariable String couponCode, @RequestParam String userProofId) {
        User user = couponService.fetchUserDetailsOfCouponBuyer(couponCode, userProofId);

        return new APIResponse(user);
    }
    
    @ResponseBody
    @RequestMapping("{couponCode}/details")
    public APIResponse fetchCouponDetails(@PathVariable String couponCode, @RequestParam String userProofId) {
        return new APIResponse(couponService.fetchCouponDetails(couponCode, userProofId));
    }
    
    @ResponseBody
    @RequestMapping(value = "{couponCode}/cancel", method = RequestMethod.POST)
    public APIResponse cancelCoupon(@PathVariable String couponCode, @RequestParam String userProofId) {
        return new APIResponse(couponService.cancelCoupon(couponCode, userProofId));
    }

}
