package com.proptiger.data.service.portfolio;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.portfolio.Portfolio;
import com.proptiger.data.model.portfolio.PortfolioProperty;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.data.repo.portfolio.PortfolioPropertyDao;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateResourceException;

/**
 * This class provides CRUD operations over a property that is a addressable entity
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class PortfolioPropertyService extends AbstractService{

	private static Logger logger = LoggerFactory.getLogger(PortfolioPropertyService.class);
	@Autowired
	private PortfolioPropertyDao portfolioPropertyDao;
	
	@Transactional(readOnly = true)
	public Portfolio getPortfolioByUserId(Integer userId){
		Portfolio portfolio = new Portfolio();
		List<PortfolioProperty> properties = portfolioPropertyDao.findByUserId(userId);
		
		double originalValue = getTotalOriginalValue(properties);
		portfolio.setProperties(properties);
		portfolio.setOriginalVaue(originalValue);
		
		return portfolio;
	}
	
	private double getTotalOriginalValue(List<PortfolioProperty> properties) {
		double value = 0.0D;
		if(properties != null){
			for(PortfolioProperty property: properties){
				value += property.getTotalPrice();
			}
		}
		return value;
	}
	@Transactional(readOnly = true)
	public PortfolioProperty getPropertyByUserIdAndPropertyId(Integer userId, Integer propertyId){
		PortfolioProperty property = portfolioPropertyDao.findByUserIdAndId(userId, propertyId);
		return property;
	}

	@Override
	protected <T extends Resource> void preProcessCreate(T resource) {
		super.preProcessCreate(resource);
		PortfolioProperty toCreate = (PortfolioProperty) resource;
		PortfolioProperty propertyPresent = portfolioPropertyDao.findByUserIdAndName(toCreate.getUserId(), toCreate.getName());
		if(propertyPresent != null){
			logger.error("Duplicate resource id {} and name {}",propertyPresent.getId(), propertyPresent.getName());
			throw new DuplicateResourceException("Resource with same name exist");
		}
	}
	@Override
	protected <T extends Resource> T create(T resource) {
		PortfolioProperty toCreate = (PortfolioProperty) resource;
		preProcessCreate(toCreate);
		PortfolioProperty created = null;
		try{
			created = portfolioPropertyDao.save(toCreate);
		}catch(Exception exception){
			throw new ConstraintViolationException(exception.getMessage(), exception);
		}
		return (T) created;
	}

	@Override
	protected <T extends Resource> T update(T resource) {
		// TODO Auto-generated method stub
		return null;
	}

}
