package com.proptiger.data.service.companyuser;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.model.companyuser.CompanyUser;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.companyuser.CompanyUserDao;
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
    //@Cacheable(value = Constants.CacheName.AGENT, key = "#agentId", unless = "#result != null")
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
