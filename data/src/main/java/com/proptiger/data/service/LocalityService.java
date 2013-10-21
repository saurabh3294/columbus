/**
 * 
 */
package com.proptiger.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.proptiger.data.model.Locality;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.LocalityDao;

/**
 * @author mandeep
 *
 */
@Service
public class LocalityService {
    @Autowired
    private LocalityDao localityDao;

    public List<Locality> getLocalities(Selector selector) {
        return Lists.newArrayList(localityDao.getLocalities(selector));
    }
}
