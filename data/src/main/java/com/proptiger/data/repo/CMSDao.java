package com.proptiger.data.repo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.proptiger.data.external.dto.ProjectPriceHistoryDetail;
import com.proptiger.data.util.HMAC_Client;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.ResourceType;
import com.proptiger.data.util.ResourceTypeAction;
import com.proptiger.exception.ResourceNotAvailableException;

/**
 * 
 * @author mukand
 * @author Rajeev Pandey
 */
@Component
public class CMSDao {

    private static final String CMS_API_ERROR                   = "0";

    private static Logger       logger                          = LoggerFactory.getLogger(CMSDao.class);

    private static final String ANALYTICS_APIS_PRICE_TREND_JSON = "app/v2/locality-price-trend?";       // "analytics/apis/price-trend.json?";
    private static final String APP_V1_PROJECT_PRICE_TREND      = "app/v2/project-price-trend?";
    private static final String TIMESTAMP                       = "timestamp";
    private static final String TOKEN                           = "token";
    private static final String USERNAME                        = "username";

    private RestTemplate        restTemplate;

    @Autowired
    private PropertyReader      propertyReader;

    private final String        CMS_USERNAME                    = "cms_username";
    private final String        CMS_PASSWORD                    = "cms_password";
    private final String        CMS_BASE_URL                    = "cms_base_url";
    private String              securityToken;
    private Long                timeStamp;

    @PostConstruct
    private void init() {
        timeStamp = new Date().getTime() / 1000;
        securityToken = HMAC_Client.calculateHMAC(
                propertyReader.getRequiredProperty(CMS_PASSWORD),
                timeStamp.toString());
        restTemplate = new RestTemplate();
    }

    public Map<String, Object> getPropertyPriceTrends(
            String locationType,
            Integer locationId,
            List<String> unitTypes,
            int lastNumberOfMonths) {

        String queryParams = "username=" + propertyReader.getRequiredProperty(CMS_USERNAME)
                + "&token="
                + securityToken
                + "&"
                + locationType
                + "="
                + locationId
                + "&duration="
                + lastNumberOfMonths;
        queryParams += "&timestamp=" + timeStamp;
        for (String unitType : unitTypes) {
            queryParams += "&unittype[]=" + unitType;
        }

        String url = propertyReader.getRequiredProperty(CMS_BASE_URL) + ANALYTICS_APIS_PRICE_TREND_JSON + queryParams;

        logger.debug("getPropertyPriceTrends url {}", url);
        try {
            Map<String, Map<String, Object>> response = (Map<String, Map<String, Object>>) restTemplate.getForObject(
                    url,
                    Object.class);
            Map<String, Object> priceTrends = response.get("price_trend");
            if (priceTrends != null) {
                boolean flag = false;
                Map<String, Object> unitTypeData;
                for (String unitType : unitTypes) {
                    unitTypeData = (Map<String, Object>) priceTrends.get(unitType);
                    flag |= (unitTypeData != null && unitTypeData.size() > 0);
                }
                if (flag == true)
                    return priceTrends;
            }
        }
        catch (RestClientException e) {
            return null;
        }

        return null;
    }

    public ProjectPriceHistoryDetail getProjectPriceHistory(Set<Integer> projectIdList, Integer noOfMonths) {
        StringBuilder queryParam = new StringBuilder();
        boolean afterFirst = false;
        for (Integer projectId : projectIdList) {
            if (afterFirst) {
                queryParam.append("&");
            }
            queryParam.append("project_ids[]").append("=").append(projectId);
            afterFirst = true;
        }

        if (noOfMonths != null && noOfMonths > 0) {
            queryParam.append("&");
            queryParam.append("duration").append("=").append(noOfMonths);
        }
        ProjectPriceHistoryDetail responce = getResponseFromCms(
                APP_V1_PROJECT_PRICE_TREND,
                queryParam.toString(),
                ProjectPriceHistoryDetail.class);
        if (responce.getStatus().equals(CMS_API_ERROR)) {
            logger.error("Error in CMS API: " + responce.getMessage());
            throw new ResourceNotAvailableException(ResourceType.PRICE_TREND, ResourceTypeAction.GET);
        }
        return responce;
    }

    /**
     * This method calls cms API and return the result as specified java type
     * 
     * @param subUrl
     * @param queryParams
     * @param javaTypeResponse
     * @return
     */
    public <T> T getResponseFromCms(String subUrl, String queryParams, Class<T> javaTypeResponse) {
        Long timeStamp = new Timestamp(new Date().getTime() / 1000).getTime();

        String token = HMAC_Client
                .calculateHMAC(propertyReader.getRequiredProperty(CMS_PASSWORD), timeStamp.toString());

        StringBuilder url = new StringBuilder(propertyReader.getRequiredProperty(CMS_BASE_URL));
        url.append(subUrl);
        url.append(USERNAME).append("=").append(propertyReader.getRequiredProperty(CMS_USERNAME));
        url.append("&").append(TOKEN).append("=").append(token);
        url.append("&").append(TIMESTAMP).append("=").append(timeStamp);
        url.append("&").append(queryParams);
        logger.debug("CMS API url - " + url.toString());
        T response = restTemplate.getForObject(url.toString(), javaTypeResponse);
        return response;
    }
}
