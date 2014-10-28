/**
 * 
 */
package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.core.model.cms.Suburb;
import com.proptiger.core.pojo.Selector;

/**
 * @author mandeep
 * 
 */
public interface SuburbCustomDao {
    public List<Suburb> getSuburbs(Selector selector);

    public Suburb getSuburb(int subUrbId);
}
