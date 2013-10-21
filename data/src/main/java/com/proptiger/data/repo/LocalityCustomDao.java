/**
 * 
 */
package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.Locality;
import com.proptiger.data.pojo.Selector;

/**
 * @author mandeep
 *
 */
public interface LocalityCustomDao {
    public List<Locality> getLocalities(Selector selector);
}
