package com.proptiger.data.service.companyuser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.core.exception.ResourceNotAvailableException;
import com.proptiger.core.model.cms.CompanyCoverage;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.data.model.companyuser.CompanyUser;
import com.proptiger.data.repo.companyuser.CompanyUserDao;

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
    public CompanyUser getAgent(Integer companyUserId, FIQLSelector selector) {
        CompanyUser companyUser = companyUserDao.findByUserId(companyUserId);

        if (companyUser == null) {
            throw new ResourceNotAvailableException(ResourceType.COMPANY_USER, ResourceTypeAction.GET);
        }

        Set<String> fields = selector.getFieldSet();
        if (fields.contains("localities")) {
            CompanyUser companyUserFull = companyUserDao.findLocalitiesByUserId(companyUserId);
            List<Locality> localities = new ArrayList<Locality>();
            for (CompanyCoverage companyCoverage : companyUserFull.getCompanyCoverages()) {
                localities.add(companyCoverage.getLocality());
            }
            companyUser.setLocalities(localities);
        }
        return companyUser;
    }
    
    public List<CompanyUser> getCompanyUsers(Integer userId) {
        List<CompanyUser> companyUser = companyUserDao.findCompanyUsersByUserId(userId);
        return companyUser;
    }
}
