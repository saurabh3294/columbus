package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ProjectPhase;
import com.proptiger.data.model.b2b.STATUS;
import com.proptiger.data.model.enums.DataVersion;
import com.proptiger.data.model.enums.EntityType;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.ProjectPhaseDao;

/**
 * 
 * @author azi
 * 
 */

@Service
public class ProjectPhaseService {
    @Autowired
    private ProjectPhaseDao projectPhaseDao;

    public List<ProjectPhase> getPhaseDetailsForProject(Integer projectId, DataVersion version) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("projectId==" + projectId).addAndConditionToFilter("version==" + version)
                .addAndConditionToFilter("status==" + STATUS.Active);
        return removeInvalidPhases(projectPhaseDao.getFilteredPhases(selector));
    }

    public ProjectPhase getPhaseDetail(Integer projectId, Integer phaseId, DataVersion version) {
        return null;
    }

    private List<ProjectPhase> removeInvalidPhases(List<ProjectPhase> phases) {

        System.out.println("AAAAAAAAAAAAAAAA " + phases.get(0).getListings());
        List<ProjectPhase> validPhases = new ArrayList<>();
        Set<EntityType> types = new HashSet<>();

        for (ProjectPhase phase : phases) {
            types.add(phase.getPhaseType());
        }
        if (types.size() > 1) {
            for (ProjectPhase phase : phases) {
                if (!phase.getPhaseType().equals(EntityType.Actual)) {
                    validPhases.add(phase);
                }
            }
        }
        else {
            validPhases = phases;
        }
        return validPhases;
    }
}