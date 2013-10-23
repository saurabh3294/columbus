package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ProjectPriceHistory;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.ProjectPriceHistoryService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value="data/v1/entity/user/{userId}/project/price-history")
public class ProjectPriceHistoryController {

	@Autowired
	private ProjectPriceHistoryService priceHistoryService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProAPIResponse getProjectPriceHistory(@PathVariable Integer userId,
			@RequestParam(required = true, value = "ids") List<Integer> ids, @RequestParam(required = false) Integer typeId,
			@RequestParam(required = false) Integer months) {
		
		List<ProjectPriceHistory> response = priceHistoryService.getProjectPriceHistory(ids, typeId, months);
		return new ProAPISuccessResponse(response, response.size());
	}
}
