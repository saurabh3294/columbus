package com.proptiger.data.repo;

import java.util.List;

import com.proptiger.data.model.Builder;

public interface BuilderCustomDao {
    public Builder getBuilderById(int builderId);

    public List<Builder> getBuildersByIds(List<Integer> builderIds);
}
