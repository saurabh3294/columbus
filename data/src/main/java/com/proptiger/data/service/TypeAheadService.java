/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import java.util.List;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.repo.TypeaheadDao;

/**
 *
 * @author mukand
 */
public class TypeAheadService {
    private TypeaheadDao typeaheadDao = new TypeaheadDao();
    
    public List<Typeahead> getSearchTypeahead(String query, int rows){
        return typeaheadDao.getSearchTypeahead(query, rows);
    }
    
}
