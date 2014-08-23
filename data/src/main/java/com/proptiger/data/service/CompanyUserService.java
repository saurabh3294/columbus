package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mockito.internal.util.reflection.Fields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.model.seller.CompanyUser;
import com.proptiger.data.model.seller.ProjectAssignmentRule;
import com.proptiger.data.model.seller.RuleAgentMapping;
import com.proptiger.data.model.seller.RuleLocalityMapping;
import com.proptiger.data.model.seller.RuleProjectMapping;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.seller.CompanyUserDao;
import com.proptiger.data.repo.seller.ProjectAssignmentRuleDao;
import com.proptiger.data.repo.seller.RuleAgentMappingDao;
import com.proptiger.data.repo.seller.RuleLocalityMappingDao;
import com.proptiger.data.repo.seller.RuleProjectMappingDao;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * Service class to get agent related details
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class CompanyUserService {
    @Autowired
    private CompanyUserDao companyUserDao;

    /**
     * Get a company user
     * 
     * @param companyUserId
     * @return
     */
    @Cacheable(value = Constants.CacheName.AGENT, key = "#agentId", unless = "#result != null")
    public CompanyUser getAgent(Integer companyUserId, FIQLSelector selector) {
        CompanyUser companyUser = companyUserDao.findOne(companyUserId);

        if (companyUser == null) {
            throw new ResourceNotAvailableException(ResourceType.COMPANY_USER, ResourceTypeAction.GET);
        }

        Set<String> fields = selector.getFieldSet();
        if (fields.contains("localities")) {
            companyUser.setLocalities(companyUserDao.findLocalitiesByUserId(companyUserId));
        }

        return companyUser;
    }
}
