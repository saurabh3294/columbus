package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.internal.dto.UserWishList;
import com.proptiger.data.model.UserWishlist;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.UserWishListService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}")
public class UserWishListController extends BaseController {

    @Autowired
    private UserWishListService userWishListService;

    @RequestMapping(value = { "/portfolio/wish-list", "wish-list" }, method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getUserWishList(@PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        List<UserWishList> result = userWishListService.getUserWishList(userInfo.getUserIdentifier());
        return new ProAPISuccessCountResponse(result, result.size());
    }

    @RequestMapping(value = "/wish-list", method = RequestMethod.POST)
    @ResponseBody
    public ProAPIResponse setUserWishList(@RequestBody UserWishlist userWishlist,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        return new ProAPISuccessResponse(userWishListService.saveUserWishList(userWishlist,
                userInfo.getUserIdentifier()));
    }

    @RequestMapping(value = "/wish-list/{wishlistId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ProAPIResponse deleteUserWishList(@PathVariable int wishlistId, @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo)
    {
        userWishListService.deleteWishlist(wishlistId);
        return getUserWishList(userId, userInfo);
    }
}
