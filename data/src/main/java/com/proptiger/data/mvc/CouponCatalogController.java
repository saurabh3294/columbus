package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.user.User;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.CouponCatalogueService;

@Controller
public class CouponCatalogController extends BaseController {

    @Autowired
    private CouponCatalogueService couponService;

    @ResponseBody
    @RequestMapping("data/v1/coupon/{couponCode}/redeem")
    public APIResponse redeemCoupon(@PathVariable String couponCode, @RequestParam String userProofId) {
        couponService.redeemCoupon(couponCode, userProofId);
        return new APIResponse("Coupon Has been redeemed.");
    }

    @ResponseBody
    @RequestMapping("data/v1/coupon/{couponCode}/user-details")
    public APIResponse fetchUserDetails(@PathVariable String couponCode, @RequestParam String userProofId) {
        User user = couponService.fetchUserDetailsOfCouponBuyer(couponCode, userProofId);

        return new APIResponse(user);
    }
}
