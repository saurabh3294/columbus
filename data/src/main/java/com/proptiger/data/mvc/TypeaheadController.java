/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
}
