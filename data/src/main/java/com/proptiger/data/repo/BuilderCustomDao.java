package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.core.model.cms.Builder;
import com.proptiger.core.pojo.Selector;

public interface BuilderCustomDao {
    public Builder getBuilderById(int builderId);

    public List<Builder> getBuildersByIds(List<Integer> builderIds);
    
    public List<Builder> getBuilders(Selector selector);

}
