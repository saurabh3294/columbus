package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.util.TopsearchUtils;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.repo.SolrDao;

/**
 * 
 * @author Manmohan
 * 
 */

@Repository
public class TopsearchDao {

    @Autowired
    private SolrDao      solrDao;

    @Autowired
    private TypeaheadDao typeaheadDao;

    public List<Typeahead> getTopsearchess(int entityId, String entityType, String requiredEntities) {
        List<Typeahead> topsearchResults = new ArrayList<Typeahead>();
        if (requiredEntities == null || requiredEntities.trim().isEmpty() || entityId == 0) {
            return topsearchResults;
        }

        String typeaheadId = String.format(
                TypeaheadConstants.typeaheadIdPattern,
                StringUtils.upperCase(entityType),
                String.valueOf(entityId));

        List<Typeahead> typeaheadList = typeaheadDao.getTypeaheadById(typeaheadId);
        if (typeaheadList == null) {
            return topsearchResults;
        }
        List<Typeahead> results = new ArrayList<Typeahead>();
        results.addAll(typeaheadList);

        List<Typeahead> topsearchIncompleteResults = new ArrayList<Typeahead>();
        topsearchIncompleteResults = TopsearchUtils.typeaheadToTopsearchConverter(results, requiredEntities);

        List<String> typeaheadIds = new ArrayList<String>();

        for (Typeahead th : topsearchIncompleteResults) {
            typeaheadIds.add(th.getId());
        }

        if (!typeaheadIds.isEmpty()) {

            List<Typeahead> topsearchList = typeaheadDao.getTypeaheadById(typeaheadIds);
            if (topsearchList == null) {
                return topsearchResults;
            }
            else {
                return topsearchList;
            }
        }

        return topsearchResults;

    }

}
