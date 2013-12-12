/**
 * 
 */
package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.Locality;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortOrder;

/**
 * @author mandeep
 *
 */
public interface LocalityCustomDao {
	public List<Locality> getLocalities(Selector selector);
	public List<Locality> findByLocationOrderByPriority(Object locationId, String locationType, Paging paging, SortOrder sortOrder);
}
