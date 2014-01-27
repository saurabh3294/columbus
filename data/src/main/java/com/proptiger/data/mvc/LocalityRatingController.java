package com.proptiger.data.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.LocalityRatings;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.LocalityRatingService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 * 
 */
@Controller
public class LocalityRatingController extends BaseController {

	@Autowired
	private LocalityRatingService localityRatingService;

	/**
	 * This method will create locality rating for non logged in or unregistered
	 * user
	 * 
	 * @param localityId
	 * @param localityReview
	 * @return
	 */
	@RequestMapping(value = { "data/v1/entity/locality/{localityId}/rating" }, method = RequestMethod.POST)
	@ResponseBody
	@DisableCaching
	public ProAPIResponse createLocalityRating(
			@PathVariable Integer localityId,
			@RequestBody LocalityRatings localityReview) {
		LocalityRatings createdRating = localityRatingService.createLocalityRating(null, localityId,
				localityReview);
		return new ProAPISuccessResponse(createdRating);
	}

	/**
	 * This method will create locality rating for logged in user
	 * 
	 * @param localityId
	 * @param localityReview
	 * @param userInfo
	 * @return
	 */
	@RequestMapping(value = {
			"data/v1/entity/user/locality/{localityId}/rating",
			"data/v1/entity/user/{userId}/locality/{localityId}/rating" }, method = RequestMethod.POST)
	@ResponseBody
	@DisableCaching
	public ProAPIResponse createLocalityRating(
			@PathVariable Integer userId,
			@PathVariable Integer localityId,
			@RequestBody LocalityRatings localityReview,
			@ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) {
		LocalityRatings createdRating = localityRatingService.createLocalityRating(
				userInfo.getUserIdentifier(), localityId, localityReview);
		return new ProAPISuccessResponse(createdRating);
	}
}
