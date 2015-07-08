package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.comparators.ComparatorChain;
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
    private TypeaheadDao    typeaheadDao;

    private ComparatorChain chain;

    @PostConstruct
    private void initialize() {
        chain = new ComparatorChain();
        chain.addComparator(new TypeaheadUtils.TypeaheadComparatorTypeaheadType());
        chain.addComparator(new TypeaheadUtils.AbstractTypeaheadComparatorScore());
    }

    /**
     * 
     * This method will return the list of topsearch results based on the
     * params.
     * 
     * @param entityId
     *            (cityId, suburbId, localityId, builderId)
     * @param entityType
     *            (type of entity whose id is given in entityId,
     *            city|suburb|locality|builder)
     * @param requiredEntities
     *            (geographically lower order entity types compare to entity
     *            given in entityType param for which top searches are required,
     *            in comma separated string format, eg. if entity type is city
     *            than requiredEntities can be 'locality,project')
     * @param isGroup
     *            (true if topsearch results required in entity groups)
     * @param rows
     *            (no of rows, if isGroup is true than its the result count of
     *            individual entity types, if false than its the total count of
     *            results)
     * @return
     */

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
                TypeaheadConstants.TYPEAHEAD_ID_PATTERN,
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

        Map<String, Float> typeaheadIdScoreMap = new HashMap<String, Float>();

        for (Typeahead th : topsearchIncompleteResults) {
            typeaheadIds.add(th.getId());
            typeaheadIdScoreMap.put(th.getId(), th.getScore());
        }

        List<Typeahead> topsearchList = typeaheadDao.getTypeaheadById(typeaheadIds);

        if (topsearchList == null) {
            return topsearchResults;
        }
        else {
            return sortTopsearchByTypeAndScore(topsearchList, typeaheadIdScoreMap, isGroup, rows);
        }

    }

    private List<Typeahead> sortTopsearchByTypeAndScore(
            List<Typeahead> topsearchList,
            Map<String, Float> typeaheadIdScoreMap,
            Boolean isGroup,
            int rows) {

        for (Typeahead th : topsearchList) {
            th.setScore(typeaheadIdScoreMap.get(th.getId()));
        }
        if (isGroup) {
            Collections.sort(topsearchList, chain);
        }
        else {

            Collections.sort(topsearchList, new TypeaheadUtils.AbstractTypeaheadComparatorScore());
            topsearchList = UtilityClass.getFirstNElementsOfList(topsearchList, rows);
        }
        return topsearchList;
    }

}
