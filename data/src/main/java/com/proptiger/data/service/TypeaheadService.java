/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.repo.TypeaheadDao;

/**
 *
 * @author mukand
 */
@Service
public class TypeaheadService {
    @Autowired
    private TypeaheadDao typeaheadDao;
    
    public List<Typeahead> getTypeaheads(String query, int rows){
        return typeaheadDao.getTypeaheads(query, rows);
    }
    
    public List<Typeahead> getTypeaheadsByTypeAheadType(String query, int rows, String typeAheadType){
        return typeaheadDao.getTypeaheads(query, rows);
    }
    
}
