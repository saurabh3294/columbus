package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.LocalityReview;
import com.proptiger.data.service.LocalityReviewService;

/**
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "v1/entity/locality-review")
public class LocalityReviewController {

	@Autowired
	private LocalityReviewService localityReviewService;
	
	@RequestMapping
	public @ResponseBody List<LocalityReview> getLocalityReviewByLocalityId(@RequestParam long localityId){
		List<LocalityReview> list = localityReviewService.findReviewByLocalityId(localityId);
		return list;
	}
	
}
