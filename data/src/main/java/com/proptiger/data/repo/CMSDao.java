/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.proptiger.data.util.HMAC_Client;

/**
 *
 * @author mukand
 */
@Component
public class CMSDao {
    RestTemplate restTemplate;
    
    //@TODO to move it to configuration file.
    private final String CMS_USERNAME = "proptiger";
        private final String CMS_PASSWORD = "PropTiger@123!";
    private final String CMS_URL = "http://cms.proptiger.com/";
    private String token;
    Long currentTime;
        
    public CMSDao() throws NoSuchAlgorithmException{
        currentTime = new Date().getTime()/1000;
        token = HMAC_Client.calculateHMAC(CMS_PASSWORD, currentTime.toString());
        restTemplate = new RestTemplate();
    }
    
    public Object getPropertyPriceTrends(String locationType, Long locationId, String[] unitTypes){
        
        String queryParams = "username="+CMS_USERNAME+"&token="+token+"&locationType="+locationId;
        queryParams += "&timestamp="+currentTime;
        for(int i=0; i<unitTypes.length; i++)
        {
            queryParams += "&unittype[]="+unitTypes[i];
        }
        
        String url = CMS_URL+"analytics/apis/price-trend.json?"+queryParams;
                
        return restTemplate.getForObject(url, Object.class);
    }
    
    
}
