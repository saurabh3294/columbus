package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.LandMark;
import com.proptiger.data.pojo.Selector;

public interface LandMarkCustomDao {
    public List<LandMark> getLocalityAmenitiesOnSelector(Selector selector);
}
