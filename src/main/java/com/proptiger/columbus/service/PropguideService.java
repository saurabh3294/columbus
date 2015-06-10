package com.proptiger.columbus.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.columbus.model.PropguideDocument;
import com.proptiger.columbus.repo.PropguideDao;

@Service
public class PropguideService {

    @Autowired
    private PropguideDao propguideDao;

    /**
     * This method will return the list of typeahead results based on the
     * params.
     * 
     * @param query
     *            : Search query
     * @param categories
     *            : Categories to be used to filter results.
     * @param rows
     */
    public List<PropguideDocument> getDocumentsV1(String query, String[] categories, int rows) {
        List<PropguideDocument> results = propguideDao.getDocumentsV1(query, categories, rows);
        return results;
    }

    /**
     * This method will return the list of typeahead results based on the
     * params.
     * 
     * @param query
     *            : Search query
     * @param types
     *            : Categories used to filter results.
     * @param rows
     */
    public List<PropguideDocument> getListingDocumentsV1(String query, String[] categories, int start, int rows) {
        List<PropguideDocument> results = propguideDao.getListingDocumentsV1(query, categories, start, rows);
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
