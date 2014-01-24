/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 *
 * @author mukand
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("fieldFilter")
public class ProjectSpecification implements BaseModel{
	
	private static enum SpecificationTypes{
		FLOORING_MASTER_BEDROOM( new String[]{"flooring", "MasterBedroom"} ),
		FLOORING_OTHER_BEDROOM( new String[]{"flooring", "OtherBedroom"} ),
		FLOORING_LIVING_DINING(new String[]{"flooring", "LivingDining"}),
		FLOORING_KITCHEN(new String[]{"flooring", "kitchen"}),
		FLOORING_TOILETS(new String[]{"flooring", "Toilets"}),
		FLOORING_BALCONY(new String[]{"flooring", "Balcony"}),
		WALLS_INTERIOR(new String[]{"walls", "Interior"}),
		WALLS_EXTERIOR(new String[]{"walls", "Exterior"}),
		WALLS_KITCHEN(new String[]{"walls", "Kitchen"}),
		DOORS_MAIN(new String[]{"doors", "Main"}),		
		DOORS_INTERNAL(new String[]{"doors", "Internal"}),
		WINDOWS(new String[]{"windows"}),
		ELECTRICAL_FITTINGS(new String[]{"electricalFittings"}),
		FITTINGS_AND_FIXTURES_TOILETS(new String[]{"fittingsAndFixtures", "Toilets"}),
		FITTINGS_AND_FIXTURES_KITCHEN(new String[]{"fittingsAndFixtures", "Kitchen"}),
		OTHERS(new String[]{"others"}),
		WALLS_TOILETS(new String[]{"walls", "Toilets"});
		
		String[] specificationSplits; 
		private SpecificationTypes(String[] specificationSplits) {
			this.specificationSplits = specificationSplits;
		}
		
		public String[] getSpecificationSplits() {
			return specificationSplits;
		}		
	}		
	
	@Transient
	private Map<String, Object> specifications;
	
	public ProjectSpecification(){
		
	}
	
	public ProjectSpecification(List<TableAttributes> tableAttributesList){
		this.specifications = new HashMap<String, Object>();
		
		for(TableAttributes tableAttributes: tableAttributesList){
			try{
				SpecificationTypes specificationTypes = ProjectSpecification.SpecificationTypes.valueOf(tableAttributes.getAttributeName());
				String specificationSplit[] = specificationTypes.getSpecificationSplits();
				addSpecificationSplitsInMap(this.specifications, specificationSplit, 0, tableAttributes.getAttributeValue());
			}
			catch(IllegalArgumentException e){
				
			}
		}
	}
	
	private Object addSpecificationSplitsInMap(Map<String, Object> map, String[] speciStrings, int index, Object Value){
		if(index >= speciStrings.length)
			return Value;
		
		if(map == null)
			map = new HashMap<String, Object>();
		
		map.put(speciStrings[index], addSpecificationSplitsInMap( (Map<String, Object>) map.get(speciStrings[index]), speciStrings, index+1, Value) );
		
		return map;
	}

	public Map<String, Object> getSpecifications() {
		return specifications;
	}

}
