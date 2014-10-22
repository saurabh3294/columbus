/**
 * 
 */
package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.core.model.cms.Locality;
import com.proptiger.data.enums.SortOrder;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Paging;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.pojo.response.PaginatedResponse;

/**
 * @author mandeep
 * 
 */
public interface LocalityCustomDao {
    public PaginatedResponse<List<Locality>> getLocalities(Selector selector);

    public List<Locality> findByLocationOrderByPriority(
            Object locationId,
            String locationType,
            Paging paging,
            SortOrder sortOrder);

    public PaginatedResponse<List<Locality>> findByLocalityIds(List<Integer> localityIds, Selector propertySelector);

    public List<Locality> getPopularLocalities(
            Integer cityId,
            Integer suburbId,
            String dateString,
            Selector selector);

    public Locality getLocality(int localityId);

    public PaginatedResponse<List<Locality>> getNearLocalitiesByDistance(
            Locality locality,
            int minDistance,
            int maxDistance);
    
    public PaginatedResponse<List<Locality>> getLocalities(FIQLSelector selector);
}
