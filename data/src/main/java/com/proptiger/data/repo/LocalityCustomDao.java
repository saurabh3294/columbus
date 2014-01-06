/**
 * 
 */
package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.Locality;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.SortOrder;
import com.proptiger.data.service.pojo.SolrServiceResponse;

/**
 * @author mandeep
 *
 */
public interface LocalityCustomDao {
	public SolrServiceResponse<List<Locality>> getLocalities(Selector selector);
	public List<Locality> findByLocationOrderByPriority(Object locationId, String locationType, Paging paging, SortOrder sortOrder);
	public SolrServiceResponse<List<Locality>> findByLocalityIds(List<Integer> localityIds, Selector propertySelector);
	public List<Locality> getPopularLocalities(Integer cityId, Integer suburbId, Long enquiryCreationTimeStamp);
}
