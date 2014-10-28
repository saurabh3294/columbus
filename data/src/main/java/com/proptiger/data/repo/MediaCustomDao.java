package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.core.model.proptiger.Media;
import com.proptiger.core.pojo.FIQLSelector;

public interface MediaCustomDao {
    public List<Media> getFilteredMedia(FIQLSelector selector);
}
