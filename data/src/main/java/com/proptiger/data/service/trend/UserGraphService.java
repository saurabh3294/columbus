package com.proptiger.data.service.trend;

import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.data.init.ExclusionAwareBeanUtilsBean;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.trend.Graph;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.trend.GraphDao;
import com.proptiger.exception.ResourceAlreadyExistException;

@Service
public class UserGraphService {
    @Autowired
    private GraphDao graphDao;

    public Graph createGraph(Graph graph, ActiveUser userInfo) {
        graph.setUserId(userInfo.getUserIdentifier());
        try {
            return graphDao.save(graph);
        }
        catch (ConstraintViolationException e) {
            throw new IllegalArgumentException(e.getConstraintViolations().iterator().next().getMessage(), e);
        }
        catch (PersistenceException e) {
            throw new ResourceAlreadyExistException(
                    "Graph name " + graph.getName() + " already taken",
                    ResponseCodes.NAME_ALREADY_EXISTS);
        }
    }

    public List<Graph> getGraph(FIQLSelector fiqlSelector) {
        return graphDao.getFilteredGraphs(fiqlSelector);
    }

    public Graph updateGraph(Graph graph, ActiveUser userInfo) {
        Graph updatedGraph = null;

        try {
            updatedGraph = updateExistingGraph(graph, userInfo);
        }
        catch (TransactionSystemException e) {
            if (e.getCause().getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException constraintViolationException = (ConstraintViolationException) e.getCause()
                        .getCause();
                throw new IllegalArgumentException(constraintViolationException.getConstraintViolations().iterator()
                        .next().getMessage(), e);
            }
            else {
                e.printStackTrace();
                throw new RuntimeException("Unexpected Error");
            }
        }
        catch (DataIntegrityViolationException e) {
            if (e.getCause().getCause() instanceof MySQLIntegrityConstraintViolationException) {
                throw new ResourceAlreadyExistException(
                        "Graph name " + graph.getName() + " already taken",
                        ResponseCodes.NAME_ALREADY_EXISTS);
            }
            else {
                e.printStackTrace();
                throw new RuntimeException("Unexpected Error");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected Error");
        }
        return updatedGraph;
    }

    @Transactional
    public Graph updateExistingGraph(Graph graph, ActiveUser userInfo) throws Exception {
        Graph savedGraph = graphDao.findOne(graph.getId());
        if (savedGraph.getUserId().equals(userInfo.getUserIdentifier())) {
            ExclusionAwareBeanUtilsBean exclusionAwareBeanUtilsBean = new ExclusionAwareBeanUtilsBean();
            exclusionAwareBeanUtilsBean.copyProperties(savedGraph, graph);
            savedGraph.setUpdatedAt(new Date());
            return graphDao.save(savedGraph);
        }
        return null;
    }
}
