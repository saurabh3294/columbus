/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.mvc;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.service.TypeAheadService;
import java.awt.print.Pageable;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author mukand
 */
@Controller
@RequestMapping(value="v1/entity/typeahead")
public class TypeaheadController {
    TypeAheadService typeaheadService = new TypeAheadService();
    
    @RequestMapping(params = "{query}", method= RequestMethod.GET)
    @ResponseBody
    public List<Typeahead> getSearchTypeahead(@PathVariable String query){
        return typeaheadService.getSearchTypeahead(query);
    }
    
}
