package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.ConstructionStatus;
import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.enums.EntityType;
import com.proptiger.data.enums.Status;
import com.proptiger.data.enums.UnitType;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ListingPrice.CustomCurrentListingPrice;
import com.proptiger.data.model.ProjectPhase;
import com.proptiger.data.model.ProjectPhase.CustomCurrentPhaseSecondaryPrice;
import com.proptiger.data.model.Property;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.ListingPriceDao;
import com.proptiger.data.repo.ProjectAvailabilityDao;
import com.proptiger.data.repo.ProjectDao;
import com.proptiger.data.repo.ProjectPhaseDao;
import com.proptiger.data.repo.SecondaryPriceDao;
import com.proptiger.data.util.UtilityClass;
import com.proptiger.exception.ResourceNotFoundException;

/**
 * 
 * @author azi
 * 
 */

@Service
public class ProjectPhaseService {
    @Autowired
    private ProjectDao             projectDao;

    @Autowired
    private ProjectPhaseDao        projectPhaseDao;

    @Autowired
    private ProjectAvailabilityDao projectAvailabilityDao;

    @Autowired
    private PropertyService        propertyService;

    @Autowired
    ListingPriceDao                listingPriceDao;

    @Autowired
    SecondaryPriceDao              secondaryPriceDao;

    public void setProjectPhaseDao(ProjectPhaseDao projectPhaseDao) {
        this.projectPhaseDao = projectPhaseDao;
    }

    public ProjectPhaseDao getProjectPhaseDao() {
        return this.projectPhaseDao;
    }

    public ProjectPhase getPhaseDetail(Integer projectId, Integer phaseId, DataVersion version, String endMonth) {
        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("phaseId==" + phaseId);
        List<ProjectPhase> phases = getPhaseDetailsFromFiql(selector, projectId, version, endMonth);
        return phases.get(0);
    }

    public List<ProjectPhase> getPhaseDetailsForProject(Integer projectId, DataVersion version, String endMonth) {
        FIQLSelector selector = new FIQLSelector();
        return getPhaseDetailsFromFiql(selector, projectId, version, endMonth);
    }

    public List<ProjectPhase> getPhaseDetailsFromFiql(FIQLSelector selector, Integer projectId, DataVersion version, String endMonth) {
        List<ProjectPhase> phases = populatePhaseMetaAttributes(populateSecondaryPrice(populatePrimaryPrice(populateProperties(populateAvailabilities(removeInvalidPhases(projectPhaseDao
                .getFilteredPhases(selector.addAndConditionToFilter("status==" + Status.Active)
                        .addAndConditionToFilter("version==" + version)
                        .addAndConditionToFilter("projectId==" + projectId))), endMonth)))));
        if (phases.size() == 0) {
            throw new ResourceNotFoundException("PhaseId Not Found");
        }
        return phases;
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

    private List<ProjectPhase> populateAvailabilities(List<ProjectPhase> phases, String endMonth) {
        for (ProjectPhase phase : phases) {
            Set<Integer> supplyIds = phase.getSupplyIdsForActiveListing();
            phase.setSumAvailability(projectAvailabilityDao.getSumCurrentAvailabilityFromSupplyIds(supplyIds, endMonth));
        }
        return phases;
    }

    private List<ProjectPhase> populateProperties(List<ProjectPhase> phases) {
        if (phases.size() > 0) {
            List<Property> properties = removeProjectFromProperties(propertyService.getPropertiesForProject(phases.get(
                    0).getProjectId()));
            for (ProjectPhase phase : phases) {
                List<Property> phaseProperties = new ArrayList<>();
                Set<Integer> phasePropertyIds = phase.getPropertyIdsForActiveListing();
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

    private List<Property> removeProjectFromProperties(List<Property> properties) {
        for (Property property : properties) {
            property.setProject(null);
        }
        return properties;
    }

    private List<ProjectPhase> populatePrimaryPrice(List<ProjectPhase> phases) {
        List<Integer> listingIds = getActiveListingIdsForPhases(phases);
        List<CustomCurrentListingPrice> listingPrices = listingPriceDao.getPrices(listingIds);

        Map<Integer, List<CustomCurrentListingPrice>> mappedListingPrices = (Map<Integer, List<CustomCurrentListingPrice>>) UtilityClass
                .groupFieldsAsPerKeys(listingPrices, Arrays.asList("listingId"));
        for (ProjectPhase phase : phases) {
            List<Property> properties = phase.getProperties();
            List<Listing> listings = phase.getListings();
            Map<Integer, List<Listing>> mappedListings = (Map<Integer, List<Listing>>) UtilityClass
                    .groupFieldsAsPerKeys(listings, Arrays.asList("propertyId"));

            for (Property property : properties) {
                Integer listingId = mappedListings.get(property.getPropertyId()).get(0).getId();
                if (mappedListingPrices.containsKey(listingId)) {
                    populatePropertyAttributesForPhase(property, mappedListingPrices.get(listingId).get(0));
                }
            }
        }
        return phases;
    }

    private void populatePropertyAttributesForPhase(Property property, CustomCurrentListingPrice listingPrice) {
       // Added NULL checks
        // TODO: Azitabh to add test cases for the same
        if (listingPrice != null && listingPrice.getPricePerUnitArea() != null) {
            property.setPricePerUnitArea(listingPrice.getPricePerUnitArea().doubleValue());

            if (property.getSize() != null) {
                property.setBudget(property.getSize() * listingPrice.getPricePerUnitArea());
            }
        }
        property.populateMinResaleOrPrimaryPrice();
        property.populateMaxResaleOrPrimaryPrice();
    }

    private List<ProjectPhase> populateSecondaryPrice(List<ProjectPhase> phases) {
        List<Integer> phaseIds = new ArrayList<>();
        for (ProjectPhase phase : phases) {
            phaseIds.add(phase.getPhaseId());
        }

        Map<Integer, Map<UnitType, List<CustomCurrentPhaseSecondaryPrice>>> mappedPhaseSecondaryPrices = (Map<Integer, Map<UnitType, List<CustomCurrentPhaseSecondaryPrice>>>) UtilityClass
                .groupFieldsAsPerKeys(
                        secondaryPriceDao.getSecondaryPriceFromPhaseIds(phaseIds),
                        Arrays.asList("phaseId", "unitType"));

        for (ProjectPhase phase : phases) {
            int phaseId = phase.getPhaseId();
            List<Property> properties = phase.getProperties();
            for (Property property : properties) {
                UnitType unitType = UnitType.valueOf(property.getUnitType());
                if (mappedPhaseSecondaryPrices.get(phaseId) != null && mappedPhaseSecondaryPrices.get(phaseId).get(
                        unitType) != null) {
                    populatePropertySecondaryPriceAttributes(
                            property,
                            mappedPhaseSecondaryPrices.get(phaseId).get(unitType).get(0));
                }
            }
        }
        return phases;
    }

    private List<ProjectPhase> populatePhaseMetaAttributes(List<ProjectPhase> phases) {
        if (phases.size() > 0) {
            ConstructionStatus constructionStatus = ConstructionStatus.fromStringStatus(projectDao
                    .findProjectByProjectId(phases.get(0).getProjectId()).getProjectStatus());

            for (ProjectPhase phase : phases) {
                phase.populatePrimaryStatus(constructionStatus);
                phase.populateResaleStatus(constructionStatus);
                phase.populateSoldStatus();
            }
        }

        return phases;
    }

    private void populatePropertySecondaryPriceAttributes(
            Property property,
            CustomCurrentPhaseSecondaryPrice phaseSecondaryPrice) {
        property.setResalePricePerUnitArea(phaseSecondaryPrice.getSecondaryPrice().doubleValue());
        property.populateResalePrice();
    }

    private List<Integer> getActiveListingIdsForPhases(List<ProjectPhase> phases) {
        List<Integer> listingIds = new ArrayList<>();
        for (ProjectPhase phase : phases) {
            listingIds.addAll(phase.getActiveListingIds());
        }
        return listingIds;
    }
}