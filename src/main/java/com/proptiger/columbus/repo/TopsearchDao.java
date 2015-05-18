package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.util.Topsearch;
import com.proptiger.columbus.util.TopsearchUtils;
import com.proptiger.core.enums.DomainObject;
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

    public List<Topsearch> getTopsearchess(int entityId, String requiredEntities) {
        List<Topsearch> topsearchResults = new ArrayList<Topsearch>();
        if (requiredEntities == null || requiredEntities.trim().isEmpty() || entityId == 0) {
            return topsearchResults;
        }
        String entityType = getEntityTypeFromEntityId(entityId);

        String typeaheadId = String.format(
                TypeaheadConstants.typeaheadIdPattern,
                StringUtils.upperCase(entityType),
                String.valueOf(entityId));

        List<Typeahead> results = new ArrayList<Typeahead>();
        results.addAll(typeaheadDao.getTypeaheadById(typeaheadId));

        topsearchResults = TopsearchUtils.typeaheadToTopsearchConverter(results, requiredEntities);
        return topsearchResults;

    }

    private String getEntityTypeFromEntityId(int entityId) {
        DomainObject dObj = DomainObject.getDomainInstance((long) entityId);
        return dObj.getText();
    }

}
