package com.proptiger.data.service.companyuser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.CompanyCoverage;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.data.enums.resource.ResourceType;
import com.proptiger.data.enums.resource.ResourceTypeAction;
import com.proptiger.data.model.companyuser.CompanyUser;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.companyuser.CompanyUserDao;
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
}
