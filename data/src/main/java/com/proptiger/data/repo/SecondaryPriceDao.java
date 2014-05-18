package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.ProjectPhase.CustomCurrentPhaseSecondaryPrice;
import com.proptiger.data.model.SecondaryPrice;

/**
 * 
 * @author azi
 * 
 */

public interface SecondaryPriceDao extends PagingAndSortingRepository<SecondaryPrice, Integer> {
    public List<CustomCurrentPhaseSecondaryPrice> getSecondaryPriceFromPhaseIds(List<Integer> phaseIds);
}
