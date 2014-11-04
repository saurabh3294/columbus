package com.proptiger.columbus.mvc;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.reflect.TypeToken;
import com.proptiger.columbus.thandlers.URLGenerationConstants;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;

@Controller
@RequestMapping
public class BenchMarkController {
    @Autowired
    HttpRequestUtil httpRequestUtil;
    
    @RequestMapping("/benchmark")
    @ResponseBody
    public APIResponse getTrend() throws Exception {
        String cities = "Agra,Ahmedabad,Aligarh,Allahabad,Almora,Alwar,Ambala,Amritsar,Anand,Anantapura,Arambagh,Aurangabad,Bangalore,Bareilly,Bharuch,Bhavnagar,Bhopal,Bhubaneswar,Bulandshahr,Burdwan,Chandigarh,Chennai,Coimbatore,Dausa,Dehradun,Delhi,Dharwad,Dindigul,Dubai,Durgapur,Ernakulam,Faridabad,Ganjam,Ghaziabad,Goa,Gulbarga,Guntur,Gurgaon,Guwahati,Gwalior,Haridwar,Hyderabad,Indore,Jabalpur,Jaipur,Jalandhar,Jamshedpur,Jhajjar,Jhansi,Jodhpur,Kannur,Kanpur,Karnal,Kochi,Kolhapur,Kolkata,Kottayam,Kozhikode,Krishna,Lucknow,Ludhiana,Madurai,Mandya,Mangalore,Mathura,Meerut,Mehsana,Moradabad,Mumbai,Mysore,Nagpur,Nainital,Nashik,Noida,Ooty,Panipat,Patiala,Patna,Pondicherry,Pune,Puri,Raigad,Ranchi,Ratnagiri,Rayagada,Rishikesh,Rohtak,Salem,Satara,Shimla,Silliguri,Solan,Solapur,Sonepat,Surat,Thiruvannamalai,Thrissur,Tirunelveli,Tirupati,Trichy,Trivandrum,Vadodara,Valsad,Varanasi,Vellore,Vijayawada,Visakhapatnam,Warangal,Wayanad";
        
        String[] cityList = cities.split(",");
        File file = new File("/tmp/api.csv");
        FileWriter writer = new FileWriter(file, false);
        for (String city : cityList) {
            try {
                URI uri = URI.create(UriComponentsBuilder
                        .fromUriString(
                                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_URL) + PropertyReader
                                        .getRequiredPropertyAsString(PropertyKeys.LOCALITY_API_URL)
                                        + "?"
                                        + URLGenerationConstants.Selector
                                        + String.format(URLGenerationConstants.SelectorGetLocalityNamesByCityName, city))
                        .build().encode().toString());

                Date d1 = new Date();
                httpRequestUtil.getInternalApiResultAsTypeListFromCache(
                        uri,
                        Locality.class);
                Date d2 = new Date();
                long diff = d2.getTime() - d1.getTime();
                writer.write(city + "," + diff + "\n");
            }
            catch (Exception e) {
                // TODO: handle exception
            }            
        }
        writer.close();
        return new APIResponse();
    }
}