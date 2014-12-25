package com.proptiger.data.mvc;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.model.solr.DynamicSolrIndex;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.data.service.DynamicSolrIndexService;

@Controller
@RequestMapping(value = "data/v1/dynamic-solr-index")
public class DynamicSolrIndexController extends BaseController{

	@Autowired
	private DynamicSolrIndexService dynamicSolrIndexService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public APIResponse getSolrEventsByEventId(
			@RequestParam Integer eventIds[]) {
		List<Integer> eventGeneratedIds = Arrays.asList(eventIds);
		return new APIResponse(
				dynamicSolrIndexService
						.getSolrIndexingEventsOnEventId(eventGeneratedIds));
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public APIResponse saveSolrEvents(
			@RequestBody List<DynamicSolrIndex> saveSolrIndexEvents) {
		return new APIResponse(
				dynamicSolrIndexService.saveIndexing(saveSolrIndexEvents));
	}
}
