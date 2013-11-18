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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Joiner;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.service.TypeaheadService;
import com.proptiger.data.service.portfolio.CityService;
import com.proptiger.data.service.portfolio.DashboardService;

/**
 *
 * @author mukand
 * @author hemendra
 * 
 */
@Controller
@RequestMapping(value="app/v1/typeahead")
public class TypeaheadController extends BaseController {
    @Autowired
    TypeaheadService typeaheadService;
    
    @Autowired
    CityService cityService;
    
    private static Logger logger = LoggerFactory.getLogger(DashboardService.class);
    
	@RequestMapping
	@ResponseBody
	public ProAPIResponse getTypeaheads(@RequestParam String query,
			@RequestParam(defaultValue = "5") int rows,
			@RequestParam(required = false) String typeAheadType,
			@RequestParam(required = false) String city) {
		
		List<String> filterQueries = new ArrayList<String>();
		if (typeAheadType != null && typeAheadType.trim() != "")
			filterQueries.add("TYPEAHEAD_TYPE:"+typeAheadType);
		
		if (city != null && city.trim() != "") {
			filterQueries.add("TYPEAHEAD_CITY:"+city);
		}
		else {
			List<String> cityList = this.findCities(query);
			if (!cityList.isEmpty()) {
				query = this.parseCities(query, cityList);
				String fqCity = Joiner.on(" OR TYPEAHEAD_CITY:").skipNulls().join(cityList);
			
				if (fqCity.trim() != "")
					filterQueries.add("TYPEAHEAD_CITY:"+fqCity.trim());
			}
		}
		
		List<Typeahead> list = typeaheadService.getTypeaheads(query, rows, filterQueries);
		return new ProAPISuccessCountResponse(super.filterFields(list, null), list.size());
	}
	
	private String parseCities(String query, List<String> queryCities) {		
		String query_new = this.substituteQuery(query, queryCities);
		
		if (query_new != "")
			return query_new;
		else
			return query;
	}
	
	private List<String> findCities(String query) {
		// TODO Auto-generated method stub
		List<City> cityList = cityService.getCityList(null);
		List<String> cityLabels = new ArrayList<String>();
		
		for (City city : cityList) {
			String label = city.getLabel();
			if (label != "")
				cityLabels.add(city.getLabel());
		}

		List<String> matchedCities = new ArrayList<String>();
		for (String city: cityLabels) {
			if (query.matches("(?i).*"+city+".*")) {
				if (city != "")
					matchedCities.add(city);
			}
		}
		return matchedCities;
	}

	private String substituteQuery(String query, List<String> terms) {
		// replaces terms in query with terms
		for (String term : terms) {
			query = query.replace(term.toLowerCase(), "");
		}
		return query.trim();
	}
}
