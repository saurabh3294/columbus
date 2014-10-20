package com.proptiger.data.repo;

import java.util.List;
import java.util.Map;

import com.proptiger.data.model.LandMark;
import com.proptiger.data.pojo.Selector;

public interface LandMarkCustomDao {
    public List<LandMark> getLocalityAmenitiesOnSelector(Selector selector);
    public Map<String, Integer> getAmenitiesTypeCount(Selector selector);
    public List<LandMark> getAmenityListByGroupSelector(Selector selector);
}
