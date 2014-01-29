package com.proptiger.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.proptiger.data.model.Bank;
import com.proptiger.data.model.enums.DomainObject;
import com.proptiger.data.repo.BankDao;
import com.proptiger.data.repo.ProjectBanksDao;
import com.proptiger.data.util.IdConverterForDatabase;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * @author Rajeev Pandey
 *
 */
@Component
public class BankService {

	private static Logger logger = LoggerFactory.getLogger(BankService.class);
	
	@Autowired
	private BankDao bankDao;
	
	@Autowired
	private ProjectBanksDao projectBanksDao;
	
	@Transactional(readOnly = true)
	public List<Bank> getBanks(){
		return bankDao.findAll();
	}
	
	@Transactional(readOnly = true)
	public Bank getBank(Integer bankId){
		Bank bank = bankDao.findOne(bankId);
		if(bank == null){
			logger.error("Bank id {} not found",bankId);
			throw new ResourceNotAvailableException(ResourceType.BANK,bankId, ResourceTypeAction.GET);
		}
		return bank;
	}
	
	/**
	 * Get list of bank that are providing home loan for project
	 * 
	 * @param projectId
	 * @return
	 */
	public List<Bank> getBanksProvidingLoanOnProject(Integer projectId) {
		Integer cmsProjectId = IdConverterForDatabase.getCMSDomainIdForDomainTypes(
				DomainObject.project, projectId);
		List<Integer> bankIds = projectBanksDao
				.findBankIdByProjectId(cmsProjectId);
		Iterable<Bank> bankDetailsList = bankDao.findAll(bankIds);
		return Lists.newArrayList(bankDetailsList);
	}
}
