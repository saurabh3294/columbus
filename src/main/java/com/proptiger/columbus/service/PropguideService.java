package com.proptiger.columbus.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.columbus.model.PropguideDocument;
import com.proptiger.columbus.repo.PropguideDao;
import com.proptiger.core.util.Constants;

@Service
public class PropguideService {

    @Autowired
    private PropguideDao propguideDao;

    /**
     * This method will return the list of typeahead results based on the
     * params.
     */
    public List<PropguideDocument> getDocumentsV1(String query, int rows) {
        List<PropguideDocument> results = propguideDao.getDocumentsV1(query, rows);
        return results;
    }

    /** Inner classes : Comparators for Propguide objects **/

    class PropguideComparatorScore implements Comparator<PropguideDocument> {
        @Override
        public int compare(PropguideDocument o1, PropguideDocument o2) {
            return o2.getScore().compareTo(o1.getScore());
        }
    }

    class PropguideComparatorId implements Comparator<PropguideDocument> {
        @Override
        public int compare(PropguideDocument o1, PropguideDocument o2) {
            return o1.getId().compareTo(o2.getId());
        }
    }

}
