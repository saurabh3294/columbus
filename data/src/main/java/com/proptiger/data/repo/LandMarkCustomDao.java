package com.proptiger.data.repo;

import java.util.List;
import java.util.Map;

import com.proptiger.core.model.cms.LandMark;
import com.proptiger.core.pojo.Selector;

public interface LandMarkCustomDao {
    public List<LandMark> getLocalityAmenitiesOnSelector(Selector selector);
    public Map<String, Integer> getAmenitiesTypeCount(Selector selector);
    public List<LandMark> getAmenityListByGroupSelector(Selector selector);
}
