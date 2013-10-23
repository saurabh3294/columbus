package com.proptiger.data.mvc.portfolio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserWishList;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.UserWishListService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/{userId}/portfolio/recently-viewed")
public class UserWishListController extends BaseController{

	@Autowired
	private UserWishListService userWishListService;
	
	@RequestMapping
	@ResponseBody
	public ProAPIResponse getUserWishList(@PathVariable Integer userId){
		List<UserWishList> result = userWishListService.getUserWishList(userId);
		return new ProAPISuccessResponse(result, result.size());
	}
	
}
