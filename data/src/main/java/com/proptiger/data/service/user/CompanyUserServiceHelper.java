package com.proptiger.data.service.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.core.exception.ResourceNotAvailableException;
import com.proptiger.core.model.cms.Company;
import com.proptiger.core.model.companyuser.CompanyUser;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.RequestHolderUtil;

/**
 * A helper service over company user service defined in userservice application
 * to fetch company user related data.
 * 
 * @author Rajeev Pandey
 *
 */
@Service
public class CompanyUserServiceHelper {

    @Value("${internal.api.userservice}")
    private String          userServiceModuleInternalApiHost;
    private static String   URL_GET_COMPANY_FOR_LOCALITY_IDS   = "data/v1/entity/company?localityIds=";
    private static String   URL_GET_COMPANY_USERS_IN_COMPANY   = "data/v1/entity/company/{companyId}/company-users";
    private static String   URL_GET_COMANY_USER_OF_ACTIVE_USER = "data/v1/entity/company-users/{userId}";

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    public List<Company> getCompaniesThatDealInOneOfLocalities(List<Integer> localityIds) {
        if (localityIds == null || localityIds.isEmpty()) {
            return new ArrayList<Company>();
        }
        StringBuilder stringUrl = new StringBuilder(userServiceModuleInternalApiHost)
                .append(URL_GET_COMPANY_FOR_LOCALITY_IDS);
        boolean first = Boolean.TRUE;
        for (Integer id : localityIds) {
            if (!first) {
                stringUrl.append(",");
            }
            stringUrl.append(id);
            first = Boolean.FALSE;
        }
        List<Company> list = httpRequestUtil.getInternalApiResultAsTypeList(
                URI.create(stringUrl.toString()),
                Company.class);
        return list;
    }

    public List<Company> getListOfCompanyOfUsersIds(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<Company>();
        }
        StringBuilder stringUrl = new StringBuilder(userServiceModuleInternalApiHost)
                .append(URL_GET_COMPANY_FOR_LOCALITY_IDS);
        boolean first = Boolean.TRUE;
        for (Integer id : userIds) {
            if (!first) {
                stringUrl.append(",");
            }
            stringUrl.append("userIds=" + id);
            first = Boolean.FALSE;
        }
        List<Company> list = httpRequestUtil.getInternalApiResultAsTypeList(
                URI.create(stringUrl.toString()),
                Company.class);
        return list;
    }

    public List<CompanyUser> getCompanyUsersInCompany(int companyId) {
        String stringUrl = userServiceModuleInternalApiHost + URL_GET_COMPANY_USERS_IN_COMPANY
                .replace("{companyId}", String.valueOf(companyId));
        List<CompanyUser> list = httpRequestUtil.getInternalApiResultAsTypeList(
                URI.create(stringUrl),
                CompanyUser.class);
        return list;
    }

    public CompanyUser getCompanyUserOfUserId(Integer userId) {
        String stringUrl = userServiceModuleInternalApiHost + URL_GET_COMANY_USER_OF_ACTIVE_USER
                .replace("{userId}", String.valueOf(userId));
        CompanyUser companyUser = httpRequestUtil.getInternalApiResultAsType(
                URI.create(stringUrl), null,
                CompanyUser.class);
        if(companyUser == null) {
            throw new ResourceNotAvailableException(ResourceType.COMPANY_USER, ResourceTypeAction.GET);
        }
        return companyUser;
    }

    private HttpHeaders createJsessionIdHeader() {
        HttpHeaders requestHeaders = null;
        String jsessionId = RequestHolderUtil.getJsessionIdFromRequestCookie();
        if (jsessionId != null && !jsessionId.isEmpty()) {
            requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", Constants.Security.COOKIE_NAME_JSESSIONID + "=" + jsessionId);
        }
        return requestHeaders;
    }
}
