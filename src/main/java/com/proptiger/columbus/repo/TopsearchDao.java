package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.columbus.util.TopsearchUtils;
import com.proptiger.columbus.util.TypeaheadUtils;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.core.util.UtilityClass;

/**
 * 
 * @author Manmohan
 * 
 */

@Repository
public class TopsearchDao {

    @Autowired
    private SolrDao         solrDao;

    @Autowired
    private TypeaheadDao    typeaheadDao;

    private ComparatorChain chain;

    public List<Typeahead> getTopsearchess(
            int entityId,
            String entityType,
            String requiredEntities,
            Boolean isGroup,
            int rows) {
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
        topsearchIncompleteResults = TopsearchUtils.typeaheadToTopsearchConverter(
                results,
                requiredEntities,
                isGroup,
                rows);

        List<String> typeaheadIds = new ArrayList<String>();

        Map<String, Float> map = new HashedMap();

        for (Typeahead th : topsearchIncompleteResults) {
            typeaheadIds.add(th.getId());
            map.put(th.getId(), th.getScore());
        }

        List<Typeahead> topsearchList = typeaheadDao.getTypeaheadById(typeaheadIds);

        if (topsearchList == null) {
            return topsearchResults;
        }
        else {
            return sortTopsearch(topsearchList, map, isGroup, rows);
        }

    }

    public List<Typeahead> sortTopsearch(
            List<Typeahead> topsearchList,
            Map<String, Float> map,
            Boolean isGroup,
            int rows) {

        for (Typeahead th : topsearchList) {
            th.setScore(map.get(th.getId()));
        }
        if (isGroup) {
            chain = new ComparatorChain();
            chain.addComparator(new TypeaheadUtils.TypeaheadComparatorTypeaheadType());
            chain.addComparator(new TypeaheadUtils.TypeaheadComparatorScore());
            Collections.sort(topsearchList, chain);
        }
        else {

            Collections.sort(topsearchList, new TypeaheadUtils.TypeaheadComparatorScore());
            topsearchList = UtilityClass.getFirstNElementsOfList(topsearchList, rows);
        }
        return topsearchList;
    }

}
