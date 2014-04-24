package com.proptiger.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.proptiger.data.model.ProjectSecondaryPrice;
import com.proptiger.data.model.enums.UnitType;

@Repository
public interface ProjectSecondaryPriceDao extends PagingAndSortingRepository<ProjectSecondaryPrice, Integer> {
    @Query(
            nativeQuery = true,
            value = "select unit_type unitType, avg((min+max)/2) pricePerUnitArea from  (select unit_type, broker_id, substring_index(group_concat(MIN_PRICE order by EFFECTIVE_DATE desc), ',', 1) min, substring_index(group_concat(MAX_PRICE order by EFFECTIVE_DATE desc), ',', 1) max from cms.project_secondary_price where PHASE_ID = ?1 group by UNIT_TYPE, BROKER_ID) t group by unit_Type")
    List<SecondaryPriceForUnitType> getSecondaryPriceForPhase(Integer phaseId);

    public static class SecondaryPriceForUnitType {
        private Integer  pricePerUnitArea;
        private UnitType unitType;

        public Integer getPricePerUnitArea() {
            return pricePerUnitArea;
        }

        public void setPricePerUnitArea(Integer pricePerUnitArea) {
            this.pricePerUnitArea = pricePerUnitArea;
        }

        public UnitType getUnitType() {
            return unitType;
        }

        public void setUnitType(UnitType unitType) {
            this.unitType = unitType;
        }
    }
}
