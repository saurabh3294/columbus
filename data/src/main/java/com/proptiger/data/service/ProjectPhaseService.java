package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.model.ProjectPhase;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.b2b.STATUS;
import com.proptiger.data.model.enums.DataVersion;
import com.proptiger.data.model.enums.EntityType;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.ProjectAvailabilityDao;
import com.proptiger.data.repo.ProjectPhaseDao;
import com.proptiger.data.repo.ProjectSecondaryPriceDao;
import com.proptiger.exception.ProAPIException;

/**
 * 
 * @author azi
 * 
 */

@Service
public class ProjectPhaseService {
    @Autowired
    private ProjectPhaseDao          projectPhaseDao;

    @Autowired
    private ProjectAvailabilityDao   projectAvailabilityDao;

    @Autowired
    private ProjectSecondaryPriceDao projectSecondaryPriceDao;

    @Autowired
    private PropertyService          propertyService;

    public ProjectPhase getPhaseDetail(Integer projectId, Integer phaseId, DataVersion version) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("phaseId==" + phaseId).addAndConditionToFilter("projectId==" + projectId)
                .addAndConditionToFilter("version==" + version).addAndConditionToFilter("status==" + STATUS.Active);
        List<ProjectPhase> phases = getPhaseDetailsFromFiql(selector);
        if (phases.size() == 0) {
            System.out.println("AAAAAAAAAAAAAAaa" + phases.get(0).getProjectId());
            throw new ProAPIException();
        }
        return phases.get(0);
    }

    public List<ProjectPhase> getPhaseDetailsForProject(Integer projectId, DataVersion version) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("projectId==" + projectId).addAndConditionToFilter("version==" + version)
                .addAndConditionToFilter("status==" + STATUS.Active);
        return getPhaseDetailsFromFiql(selector);
    }

    public List<ProjectPhase> getPhaseDetailsFromFiql(FIQLSelector selector) {
        return populateProperties(populateAvailabilities(removeInvalidPhases(projectPhaseDao
                .getFilteredPhases(selector))));
    }

    private List<ProjectPhase> removeInvalidPhases(List<ProjectPhase> phases) {
        List<ProjectPhase> validPhases = new ArrayList<>();
        Set<EntityType> types = new HashSet<>();

        for (ProjectPhase phase : phases) {
            types.add(phase.getPhaseType());
        }

        if (types.size() > 1) {
            for (ProjectPhase phase : phases) {
                if (phase.getPhaseType().equals(EntityType.Actual)) {
                    validPhases.add(phase);
                }
            }
        }
        else {
            validPhases = phases;
        }
        return validPhases;
    }

    private List<ProjectPhase> populateAvailabilities(List<ProjectPhase> phases) {
        for (ProjectPhase phase : phases) {
            Set<Integer> supplyIds = phase.getSupplyIds();
            if (supplyIds.size() > 0) {
                phase.setSumAvailability(projectAvailabilityDao.getSumCurrentAvailabilityFromSupplyIds(supplyIds)
                        .intValue());
            }
        }
        return phases;
    }

    private List<ProjectPhase> populateSecondaryPrice(List<ProjectPhase> phases) {
        for (ProjectPhase phase : phases) {
            phase.setSecondaryPrices(projectSecondaryPriceDao.getSecondaryPriceForPhase(phase.getPhaseId()));
        }
        return phases;
    }

    private List<ProjectPhase> populateProperties(List<ProjectPhase> phases) {
        if (phases.size() > 0) {
            List<Property> properties = propertyService.getPropertiesForProject(phases.get(0).getProjectId());
            for (ProjectPhase phase : phases) {
                List<Property> phaseProperties = new ArrayList<>();
                Set<Integer> phasePropertyIds = phase.getPropertyIds();
                for (Property property : properties) {
                    if (phasePropertyIds.contains(property.getPropertyId())) {
                        phaseProperties.add(property);
                    }
                }

                phase.setProperties(phaseProperties);
            }
        }
        return phases;
    }
}