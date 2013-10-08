package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.proptiger.data.model.portfolio.PortfolioProperty;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * This class provides CRUD operations over a property that is a addressable entity
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class PortfolioPropertyService {

	private List<PortfolioProperty> propertyList;

	@PostConstruct
	public void init() {
		createDummyProperties(20);
	}
	
	public List<PortfolioProperty> getProperties(Integer propertyId){
		if(propertyId == null){
			return propertyList;
		}
		PortfolioProperty proptigerProperty = null;
		for(PortfolioProperty property: propertyList){
			if(propertyId.intValue() == property.getId()){
				proptigerProperty = property;
				break;
			}
		}
		if(proptigerProperty == null){
			throw new ResourceNotAvailableException("Resource id "+propertyId+" not available");
		}
		List<PortfolioProperty> list = new ArrayList<>();
		list.add(proptigerProperty);
		return list;
	}
	
	private void createDummyProperties(int count) {
		propertyList = new ArrayList<>();
		int id = 10000;
		int tower = 1;
		for (int i = 1; i <= count; i++) {
			PortfolioProperty proptigerProperty = new PortfolioProperty();
			proptigerProperty.setId(id + i);
			proptigerProperty.setPurchaseDate(new Date());
			proptigerProperty.setTower(tower++);
			propertyList.add(proptigerProperty);
		}
	}
}
