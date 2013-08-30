/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.service;

import com.proptiger.data.model.Typeahead;
import com.proptiger.data.repo.TypeaheadDao;
import java.util.List;

/**
 *
 * @author mukand
 */
public class TypeAheadService {
    private TypeaheadDao typeaheadDao = new TypeaheadDao();
    
    public List<Typeahead> getSearchTypeahead(String query){
        return typeaheadDao.getSearchTypeahead(query);
    }
    
}
