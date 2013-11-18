/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.City;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.repo.TypeaheadDao;
import com.proptiger.data.service.portfolio.CityService;

/**
 * @author mukand
 * @author Hemendra
 * 
 * 
 */


@Service
public class TypeaheadService {
    @Autowired
    private TypeaheadDao typeaheadDao;
    
    @Autowired
    private CityService cityService; 
    
    public List<Typeahead> getTypeaheads(String query, int rows, List<String> filterQueries) {
        return typeaheadDao.getTypeaheads(query, rows, filterQueries);
    }
}
