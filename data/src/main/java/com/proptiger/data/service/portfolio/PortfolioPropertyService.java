package com.proptiger.data.service.portfolio;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.model.portfolio.OverallReturn;
import com.proptiger.data.model.portfolio.Portfolio;
import com.proptiger.data.model.portfolio.PortfolioProperty;
import com.proptiger.data.model.portfolio.ReturnType;
import com.proptiger.data.model.resource.NamedResource;
import com.proptiger.data.model.resource.Resource;
import com.proptiger.data.repo.portfolio.PortfolioPropertyDao;
import com.proptiger.exception.ConstraintViolationException;
import com.proptiger.exception.DuplicateNameResourceException;
import com.proptiger.exception.ResourceNotAvailableException;

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
	
	/**
	 * Get portfolio object for a particular user id
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public Portfolio getPortfolioByUserId(Integer userId){
		Portfolio portfolio = new Portfolio();
		List<PortfolioProperty> properties = portfolioPropertyDao.findByUserId(userId);
		updatePriceInfoInPortfolio(portfolio, properties);
		portfolio.setProperties(properties);
		return portfolio;
	}
	/**
	 * Updates price information in Portfolio object
	 * @param portfolio
	 * @param properties
	 */
	private void updatePriceInfoInPortfolio(Portfolio portfolio,
			List<PortfolioProperty> properties) {
		double originalValue = 0.0D;
		double currentValue = 0.0D;
		if(properties != null){
			for(PortfolioProperty property: properties){
				originalValue += property.getTotalPrice();
				property.setCurrentPrice(property.getProjectType().getSize() * property.getProjectType().getPricePerUnitArea());
				currentValue += property.getCurrentPrice();
			}
		}
		portfolio.setCurrentValue(currentValue);
		portfolio.setOriginalVaue(originalValue);
		OverallReturn overallReturn = getOverAllReturn(originalValue,
				currentValue);
		portfolio.setOverallReturn(overallReturn );
	}
	/**
	 * Calculates the overall return 
	 * @param originalValue
	 * @param currentValue
	 * @return
	 */
	private OverallReturn getOverAllReturn(double originalValue,
			double currentValue) {
		OverallReturn overallReturn = new OverallReturn();
		double changeAmt = currentValue - originalValue;
		overallReturn.setChangeAmount(Math.abs(changeAmt));
		double changePercent = (Math.abs(changeAmt)/originalValue)*100;
		overallReturn.setChangePercent(changePercent);
		if(changeAmt < 0){
			overallReturn.setReturnType(ReturnType.DECLINE);
		}
		else if(changeAmt > 0){
			overallReturn.setReturnType(ReturnType.APPRECIATION);
		}
		else{
			overallReturn.setReturnType(ReturnType.NOCHANGE);
		}
		return overallReturn;
	}
	/**
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<PortfolioProperty> getAllProperties(Integer userId){
		List<PortfolioProperty> properties = portfolioPropertyDao.findByUserId(userId);
		if(properties != null){
			for(PortfolioProperty property: properties){
				property.setCurrentPrice(property.getProjectType().getSize() * property.getProjectType().getPricePerUnitArea());
			}
		}
		return properties;
	}
	/**
	 * Get a PortfolioProperty for particular user id and PortfolioProperty id
	 * @param userId
	 * @param propertyId
	 * @return
	 */
	@Transactional(readOnly = true)
	public PortfolioProperty getPropertyByUserIdAndPropertyId(Integer userId, Integer propertyId){
		PortfolioProperty property = portfolioPropertyDao.findByUserIdAndId(userId, propertyId);
		if(property == null){
			logger.error("Dashboard id {} not found for userid {}",propertyId, userId);
			throw new ResourceNotAvailableException("Resource not available");
		}
		property.setCurrentPrice(property.getProjectType().getSize() * property.getProjectType().getPricePerUnitArea());
		return property;
	}

	@Override
	protected <T extends Resource & NamedResource> void preProcessCreate(T resource) {
		super.preProcessCreate(resource);
		PortfolioProperty toCreate = (PortfolioProperty) resource;
		PortfolioProperty propertyPresent = portfolioPropertyDao.findByUserIdAndName(toCreate.getUserId(), toCreate.getName());
		if(propertyPresent != null){
			logger.error("Duplicate resource id {} and name {}",propertyPresent.getId(), propertyPresent.getName());
			throw new DuplicateNameResourceException("Resource with same name exist");
		}
	}
	
	/**
	 * Creates a PortfolioProperty
	 * @param userId
	 * @param property
	 * @return
	 */
	@Transactional(rollbackFor = ConstraintViolationException.class)
	public PortfolioProperty createPortfolio(Integer userId, PortfolioProperty property){
		property.setUserId(userId);
		return create(property);
	}
	
	/**
	 * Updated an existing PortfolioProperty
	 * @param userId
	 * @param propertyId
	 * @param property
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public PortfolioProperty updatePortfolio(Integer userId, Integer propertyId, PortfolioProperty property){
		property.setUserId(userId);
		property.setId(propertyId);
		return update(property);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Resource> T create(T resource) {
		PortfolioProperty toCreate = (PortfolioProperty) resource;
		preProcessCreate(toCreate);
		PortfolioProperty created = null;
		try{
			created = portfolioPropertyDao.save(toCreate);
		}catch(Exception exception){
			throw new ConstraintViolationException(exception.getMessage(), exception);
		}
		logger.debug("Created PortfolioProperty id {} for userid {}",created.getId(),created.getUserId());
		return (T) created;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Resource> T update(T resource) {
		PortfolioProperty toUpdate = (PortfolioProperty) resource;
		PortfolioProperty resourcePresent = preProcessUpdate(toUpdate);
		PortfolioProperty resourceWithSameName = portfolioPropertyDao.findByUserIdAndName(toUpdate.getUserId(), toUpdate.getName());
		if(resourceWithSameName != null){
			logger.error("Duplicate resource id {} and name {}",resourceWithSameName.getId(), resourceWithSameName.getName());
			throw new DuplicateNameResourceException("Resource with same name exist");
		}
		resourcePresent.update(toUpdate);
		return (T) resourcePresent;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Resource> T preProcessUpdate(T resource) {
		super.preProcessUpdate(resource);
		PortfolioProperty toUpdate = (PortfolioProperty) resource;
		PortfolioProperty resourcePresent = portfolioPropertyDao.findOne(toUpdate.getId());
		if(resourcePresent == null){
			logger.error("PortfolioProperty id {} not found",toUpdate.getId());
			throw new ResourceNotAvailableException("Resource "+toUpdate.getId()+" not available");
		}
		return (T) resourcePresent;
	}

	/**
	 * Deletes PortfolioProperty for provided user id and property id
	 * @param userId
	 * @param propertyId
	 * @return
	 */
	@Transactional(rollbackFor = ResourceNotAvailableException.class)
	public PortfolioProperty deleteProperty(Integer userId, Integer propertyId){
		PortfolioProperty propertyPresent = getPropertyByUserIdAndPropertyId(userId, propertyId);
		portfolioPropertyDao.delete(propertyPresent);
		return propertyPresent;
	}
	
}
