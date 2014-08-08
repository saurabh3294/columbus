package com.proptiger.data.mvc.marketplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.Listing;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.marketplace.ListingService;
import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user/listing")
public class ListingController extends BaseController {

    @Autowired
    private ListingService listingService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public APIResponse createListing(
            @RequestBody Listing listing,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) {
        Listing created = listingService.createListing(listing, userInfo.getUserIdentifier());
        return new APIResponse(super.filterFields(created, null));
    }
}
