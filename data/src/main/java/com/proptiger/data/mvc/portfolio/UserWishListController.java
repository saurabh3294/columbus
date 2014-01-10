package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio/wish-list")
public class UserWishListController extends BaseController{

	@Autowired
	private UserWishListService userWishListService;
	
	@RequestMapping
	@ResponseBody
	public ProAPIResponse getUserWishList(@PathVariable Integer userId,
			@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo){
		List<UserWishList> result = userWishListService.getUserWishList(userInfo.getUserIdentifier());
		return new ProAPISuccessCountResponse(result, result.size());
	}
	
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public ProAPIResponse setUserWishList(@ModelAttribute UserWishlist userWishlist, @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo){
		int status = userWishListService.saveUserWishList(userWishlist, userInfo);
		return new ProAPISuccessResponse(status);
	}
	
}
