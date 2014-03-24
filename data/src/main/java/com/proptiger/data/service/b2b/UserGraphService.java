package com.proptiger.data.service.b2b;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.Graph;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.b2b.GraphDao;

@Service
public class UserGraphService {
    @Autowired
    private GraphDao graphDao;

    public Graph createGraph(Graph graph, UserInfo userInfo) {
        graph.setUserId(userInfo.getUserIdentifier());
        try {
            return graphDao.save(graph);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public List<Graph> getGraph(FIQLSelector fiqlSelector) {
        return graphDao.getFilteredGraphs(fiqlSelector);
    }

    public Graph updateGraph(Graph graph, UserInfo userInfo) {
        return null;
    }
}
