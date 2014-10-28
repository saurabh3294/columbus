package com.proptiger.data.mvc.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.Constants;
import com.proptiger.data.internal.dto.UserWishListDto;
import com.proptiger.data.model.UserWishlist;
import com.proptiger.data.service.user.UserWishListService;

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
    public APIResponse getProjectUserWishList(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        List<UserWishListDto> result = userWishListService.getProjectUserWishList(userInfo.getUserIdentifier());
        return new APIResponse(result, result.size());
    }

    @RequestMapping(
            value = { "/portfolio/wish-list/property", "wish-list/property" },
            method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getPropertyUserWishList(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        List<UserWishListDto> result = userWishListService.getPropertyUserWishList(userInfo.getUserIdentifier());
        return new APIResponse(result, result.size());
    }

    @RequestMapping(value = { "/portfolio/wish-list", "wish-list" }, method = RequestMethod.GET)
    @ResponseBody
    public APIResponse getUserWishList(
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        List<UserWishListDto> result = userWishListService.getUserWishList(userInfo.getUserIdentifier());
        return new APIResponse(result, result.size());
    }

    @RequestMapping(value = "/wish-list", method = RequestMethod.POST)
    @ResponseBody
    public APIResponse createUserWishList(
            @RequestBody UserWishlist userWishlist,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        List<UserWishListDto> result = userWishListService.createUserWishList(
                userWishlist,
                userInfo.getUserIdentifier());
        return new APIResponse(result, result.size());
    }

    @RequestMapping(value = "/wish-list/{wishlistId}", method = RequestMethod.DELETE)
    @ResponseBody
    public APIResponse deleteUserWishList(
            @PathVariable int wishlistId,
            @PathVariable Integer userId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        List<UserWishListDto> result = userWishListService.deleteWishlist(wishlistId);
        return new APIResponse(result, result.size());
    }
}
