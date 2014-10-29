package com.proptiger.data.repo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.proptiger.core.util.HMAC_Client;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

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
                propertyReader.getRequiredProperty(PropertyKeys.CMS_PASSWORD),
                timeStamp.toString());
        restTemplate = new RestTemplate();
    }

    public Map<String, Object> getPropertyPriceTrends(
            String locationType,
            Integer locationId,
            List<String> unitTypes,
            int lastNumberOfMonths) {

        String queryParams = "username=" + propertyReader.getRequiredProperty(PropertyKeys.CMS_USERNAME)
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

        String url = propertyReader.getRequiredProperty(PropertyKeys.CMS_BASE_URL) + ANALYTICS_APIS_PRICE_TREND_JSON
                + queryParams;
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
}