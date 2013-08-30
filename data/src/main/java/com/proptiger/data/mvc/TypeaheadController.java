/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.service.TypeAheadService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author mukand
 */
@Controller
@RequestMapping(value="v1/entity/typeahead")
public class TypeaheadController {
    TypeAheadService typeaheadService = new TypeAheadService();
    
    @RequestMapping
    @ResponseBody
    public List<Typeahead> getSearchTypeahead(@RequestParam String query, @RequestParam(defaultValue = "5") int rows){
        return typeaheadService.getSearchTypeahead(query, rows);
    }
    
}
