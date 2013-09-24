package com.proptiger.data.service.portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.proptiger.data.model.Property;
import com.proptiger.data.model.portfolio.PropertyDocument;
import com.proptiger.data.model.portfolio.PropertyPaymentPlan;
import com.proptiger.data.model.portfolio.ProptigerProperty;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * This class provides CRUD operations over a property that is a addressable entity
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class ProptigerPropertyService {

	private List<ProptigerProperty> propertyList;

	@PostConstruct
	public void init() {
		createDummyProperties(20);
	}
	
	public List<ProptigerProperty> getProperties(Integer propertyId){
		if(propertyId == null){
			return propertyList;
		}
		ProptigerProperty proptigerProperty = null;
		for(ProptigerProperty property: propertyList){
			if(propertyId.intValue() == property.getId()){
				proptigerProperty = property;
				break;
			}
		}
		if(proptigerProperty == null){
			throw new ResourceNotAvailableException("Resource id "+propertyId+" not available");
		}
		List<ProptigerProperty> list = new ArrayList<>();
		list.add(proptigerProperty);
		return list;
	}
	
	private void createDummyProperties(int count) {
		propertyList = new ArrayList<>();
		int id = 10000;
		int tower = 1;
		for (int i = 1; i <= count; i++) {
			ProptigerProperty proptigerProperty = new ProptigerProperty();
			proptigerProperty.setDocuments(new ArrayList<PropertyDocument>());
			proptigerProperty.setId(id + i);
			List<PropertyPaymentPlan> paymentPlans = new ArrayList<PropertyPaymentPlan>();
			proptigerProperty.setPaymentPlans(paymentPlans);
			proptigerProperty.setProperty(new Property());
			proptigerProperty.setPurchaseDate(new Date());
			proptigerProperty.setPurchasePrice(750000D);
			proptigerProperty.setTower(tower++);
			propertyList.add(proptigerProperty);
		}
	}
}
