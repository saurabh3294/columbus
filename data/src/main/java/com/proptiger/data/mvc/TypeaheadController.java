/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.TypeaheadService;

/**
 *
 * @author mukand
 */
@Controller
@RequestMapping(value="app/v1/typeahead")
public class TypeaheadController extends BaseController {
    @Autowired
    TypeaheadService typeaheadService;
    
    @RequestMapping
    @ResponseBody
    public ProAPIResponse getTypeaheads(@RequestParam String query, @RequestParam(defaultValue = "5") int rows){
        List<Typeahead> list = typeaheadService.getTypeaheads(query, rows);
        return new ProAPISuccessResponse(super.filterFields(list, null));
    }
    
    @RequestMapping(value="/{typeAheadType}")
    @ResponseBody
	public ProAPIResponse getTypeaheadForDocumentType(
			@PathVariable String typeAheadType, @RequestParam String query,
			@RequestParam(defaultValue = "5") int rows) {
    	typeAheadType = typeAheadType.toUpperCase();
		List<Typeahead> list = typeaheadService.getTypeaheadsByTypeAheadType(query, rows,
				typeAheadType);
		return new ProAPISuccessResponse(super.filterFields(list, null));
	}
}
