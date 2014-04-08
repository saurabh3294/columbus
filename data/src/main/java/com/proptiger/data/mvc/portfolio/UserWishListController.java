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
import com.proptiger.data.internal.dto.UserWishListDto;
import com.proptiger.data.model.UserWishlist;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
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

    @RequestMapping(value = { "/portfolio/wish-list/project", "wish-list/project" }, method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getProjectUserWishList(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        List<UserWishListDto> result = userWishListService.getProjectUserWishList(userInfo.getUserIdentifier());
        return new ProAPISuccessCountResponse(result, result.size());
    }

    @RequestMapping(
            value = { "/portfolio/wish-list/property/property", "wish-list/property" },
            method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getPropertyUserWishList(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        List<UserWishListDto> result = userWishListService.getPropertyUserWishList(userInfo.getUserIdentifier());
        return new ProAPISuccessCountResponse(result, result.size());
    }

    @RequestMapping(value = { "/portfolio/wish-list", "wish-list" }, method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse getUserWishList(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        List<UserWishListDto> result = userWishListService.getUserWishList(userInfo.getUserIdentifier());
        return new ProAPISuccessCountResponse(result, result.size());
    }

    @RequestMapping(value = "/wish-list", method = RequestMethod.POST)
    @ResponseBody
    public ProAPIResponse createUserWishList(
            @RequestBody UserWishlist userWishlist,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        List<UserWishListDto> result = userWishListService.createUserWishList(
                userWishlist,
                userInfo.getUserIdentifier());
        return new ProAPISuccessCountResponse(result, result.size());
    }

    @RequestMapping(value = "/wish-list/{wishlistId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ProAPIResponse deleteUserWishList(
            @PathVariable int wishlistId,
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
        List<UserWishListDto> result = userWishListService.deleteWishlist(wishlistId);
        return new ProAPISuccessCountResponse(result, result.size());
    }
}
