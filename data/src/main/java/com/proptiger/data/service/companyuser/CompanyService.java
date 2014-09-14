package com.proptiger.data.service.companyuser;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.ActivationStatus;
import com.proptiger.data.model.Company;
import com.proptiger.data.model.companyuser.CompanyUser;
import com.proptiger.data.repo.CompanyDao;
import com.proptiger.data.repo.companyuser.CompanyUserDao;

@Service
public class CompanyService {
    @Autowired
    private CompanyDao     companyDao;

    @Autowired
    private CompanyUserDao companyUserDao;

    /**
     * finds all broker companies which deal in one of the localities
     * 
     * @param localityIds
     * @return {@link Company} {@link List}
     */
    public List<Company> getBrokersForLocalities(List<Integer> localityIds) {
        return companyDao.findBrokersForLocality(localityIds);
    }

    /**
     * finds all users for a company
     * 
     * @param companyIds
     * @return {@link CompanyUser} {@link List}
     */
    public List<CompanyUser> getCompanyUsersForCompanies(Company company) {
        return companyUserDao.findByCompanyIdAndStatus(company.getId(), ActivationStatus.Active);
    }
}
