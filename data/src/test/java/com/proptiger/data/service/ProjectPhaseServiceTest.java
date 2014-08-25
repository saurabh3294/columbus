package com.proptiger.data.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.enums.Status;
import com.proptiger.data.model.Listing;
import com.proptiger.data.model.ProjectPhase;
import com.proptiger.data.model.ProjectSupply;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.SecondaryPrice;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.ProjectPhaseDao;

/**
 * 
 * @author azi
 * 
 */

public class ProjectPhaseServiceTest extends AbstractTest {

    @Autowired
    private ProjectPhaseService projectPhaseService;

   @Test(enabled = false)
    public void testGetPhasePrices() {
        ProjectPhaseDao originalDao = projectPhaseService.getProjectPhaseDao();

        FIQLSelector selector = new FIQLSelector();
        selector.addAndConditionToFilter("phaseId==1008416").addAndConditionToFilter("status==" + Status.Active)
                .addAndConditionToFilter("version==" + DataVersion.Website)
                .addAndConditionToFilter("projectId==504235");

        ProjectPhaseDao projectPhaseDao = mock(ProjectPhaseDao.class);
        when(projectPhaseDao.getFilteredPhases(selector)).thenReturn(getMockPhaseListForTestPhasePrices());

        projectPhaseService.setProjectPhaseDao(projectPhaseDao);

        ProjectPhase phase;
        phase = projectPhaseService.getPhaseDetail(504235, 1008416, DataVersion.Website);

        for (Property property : phase.getProperties()) {
            if (property.getPropertyId() == 5010148) {
                assertEquals("PricePerUnitArea Failed", Double.valueOf(4500.0), property.getPricePerUnitArea());
                assertEquals(
                        "ResalePricePerUnitArea Failed",
                        Double.valueOf(5000),
                        property.getResalePricePerUnitArea());
                assertEquals("Budget Failed", Double.valueOf(6754500), property.getBudget());
                assertEquals("ResalePrice Failed", Double.valueOf(7505000), property.getResalePrice());
                assertEquals(
                        "MinResaleOrPrimaryPrice",
                        Double.valueOf(6754500),
                        property.getMinResaleOrPrimaryPrice());
                assertEquals(
                        "MaxResaleOrPrimaryPrice Failed",
                        Double.valueOf(7505000),
                        property.getMaxResaleOrPrimaryPrice());
            }
        }
        projectPhaseService.setProjectPhaseDao(originalDao);
    }

    private List<ProjectPhase> getMockPhaseListForTestPhasePrices() {
        ProjectPhase mockPhase = new ProjectPhase();

        mockPhase.setPhaseId(1008416);
        mockPhase.setProjectId(504235);

        Listing mockListing = new Listing();
        mockListing.setId(21930);
        mockListing.setStatus(Status.Active);
        mockListing.setPropertyId(5010148);
        mockListing.setProjectSupply(new ArrayList<ProjectSupply>());

        mockPhase.setListings(new ArrayList<Listing>(Arrays.asList(mockListing)));

        SecondaryPrice mockSecondaryPrice = new SecondaryPrice();
        mockSecondaryPrice.setId(17010);

        mockPhase.setSecondaryPrices(new ArrayList<SecondaryPrice>(Arrays.asList(mockSecondaryPrice)));

        return new ArrayList<ProjectPhase>(Arrays.asList(mockPhase));
    }
}