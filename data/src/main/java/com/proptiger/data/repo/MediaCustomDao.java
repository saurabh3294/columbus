package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.Media;
import com.proptiger.data.pojo.FIQLSelector;

public interface MediaCustomDao {
    public List<Media> getFilteredMedia(FIQLSelector selector);
}
