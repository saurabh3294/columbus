/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.TypeaheadService;

/**
 * 
 * @author mukand
 * @author hemendra
 * 
 */
@Controller
@RequestMapping(value = "app/v1/typeahead")
public class TypeaheadController extends BaseController {
	@Autowired
	private TypeaheadService typeaheadService;

	private static Logger logger = LoggerFactory
			.getLogger(TypeaheadController.class);

	@RequestMapping
	@ResponseBody
	public APIResponse getTypeaheads(@RequestParam String query,
			@RequestParam(defaultValue = "5") int rows,
			@RequestParam(required = false) String typeAheadType,
			@RequestParam(required = false) String city) {

		List<String> filterQueries = new ArrayList<String>();
		if (typeAheadType != null && typeAheadType.trim() != "")
			filterQueries.add("TYPEAHEAD_TYPE:" + typeAheadType.toUpperCase());

		if (city != null && city.trim() != "") {
			filterQueries.add("TYPEAHEAD_CITY:" + city);
		}

		List<Typeahead> list = typeaheadService.getTypeaheads(query, rows,
				filterQueries);
		return new APIResponse(super.filterFields(list, null), list.size());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/exact")
	@ResponseBody
	public APIResponse getExactTypeaheads(@RequestParam String query,
			@RequestParam(defaultValue = "5") int rows,
			@RequestParam(required = false) String typeAheadType,
			@RequestParam(required = false) String city) {

		List<String> filterQueries = new ArrayList<String>();
		if (typeAheadType != null && typeAheadType.trim() != "")
			filterQueries.add("TYPEAHEAD_TYPE:" + typeAheadType.toUpperCase());

		if (city != null && city.trim() != "") {
			filterQueries.add("TYPEAHEAD_CITY:" + city);
		}

		List<Typeahead> list = typeaheadService.getExactTypeaheads(query, rows,
				filterQueries);
		return new APIResponse(super.filterFields(list, null),list.size());
	}

}
