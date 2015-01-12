package com.proptiger.data.service.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.core.model.cms.Company;
import com.proptiger.core.model.companyuser.CompanyUser;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

/**
 * A helper service over company user service defined in userservice application
 * to fetch company user related data.
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class CompanyUserServiceHelper {

    private static String   URL_GET_COMPANY_FOR_LOCALITY_IDS = "data/v1/entity/company?localityIds=";
    private static String   URL_GET_COMPANY_USERS_IN_COMPANY = "data/v1/entity/company/{companyId}/company-users";

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    public List<Company> getCompaniesThatDealInOneOfLocalities(List<Integer> localityIds) {
        if (localityIds == null || localityIds.isEmpty()) {
            return new ArrayList<Company>();
        }
        StringBuilder stringUrl = new StringBuilder(
                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL))
                .append(URL_GET_COMPANY_FOR_LOCALITY_IDS);
        boolean first = Boolean.TRUE;
        for (Integer id : localityIds) {
            if(!first){
                stringUrl.append(",");
            }
            stringUrl.append("localityIds="+id);
            first = Boolean.FALSE;
        }
        List<Company> list = httpRequestUtil.getInternalApiResultAsTypeList(URI.create(stringUrl.toString()), Company.class);
        return list;
    }
    
    public List<Company> getListOfCompanyOfUsersIds(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<Company>();
        }
        StringBuilder stringUrl = new StringBuilder(
                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL))
                .append(URL_GET_COMPANY_FOR_LOCALITY_IDS);
        boolean first = Boolean.TRUE;
        for (Integer id : userIds) {
            if(!first){
                stringUrl.append(",");
            }
            stringUrl.append("userIds="+id);
            first = Boolean.FALSE;
        }
        List<Company> list = httpRequestUtil.getInternalApiResultAsTypeList(URI.create(stringUrl.toString()), Company.class);
        return list;
    }

    public List<CompanyUser> getCompanyUsersInCompany(int companyId) {
        String stringUrl = PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + URL_GET_COMPANY_USERS_IN_COMPANY
                .replace("{companyId}", String.valueOf(companyId));
        List<CompanyUser> list = httpRequestUtil.getInternalApiResultAsTypeList(
                URI.create(stringUrl),
                CompanyUser.class);
        return list;
    }

}
