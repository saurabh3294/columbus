/**
 * 
 */
package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.Suburb;
import com.proptiger.data.pojo.Selector;

/**
 * @author mandeep
 *
 */
public interface SuburbCustomDao {
    public List<Suburb> getSuburbs(Selector selector);
}
