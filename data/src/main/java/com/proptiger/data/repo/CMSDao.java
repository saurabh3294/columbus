/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.repo;

import com.google.gson.Gson;
import com.proptiger.data.util.HMAC_Client;
import com.sun.jndi.toolkit.url.Uri;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.UriBuilder;
import org.mortbay.util.ajax.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import sun.security.provider.SHA;

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
                
        return restTemplate.getForObject(url, Object.class, null);
    }
    
    
}
