package com.proptiger.columbus.mvc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.proptiger.columbus.thandlers.URLGenerationConstants;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.pojo.response.APIResponse;
import com.proptiger.core.util.CorePropertyKeys;
import com.proptiger.core.util.HttpRequestUtil;
import com.proptiger.core.util.PropertyReader;

@Controller
@RequestMapping
public class BenchMarkController {
    @Autowired
    HttpRequestUtil       httpRequestUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchMarkController.class);

    @RequestMapping("/benchmark")
    @ResponseBody
    public APIResponse getTrend() {
        String cities = "Agra,Ahmedabad,Aligarh,Allahabad,Almora,Alwar,Ambala,Amritsar,Anand,Anantapura,Arambagh,Aurangabad,Bangalore,Bareilly,Bharuch,Bhavnagar,Bhopal,Bhubaneswar,Bulandshahr,Burdwan,Chandigarh,Chennai,Coimbatore,Dausa,Dehradun,Delhi,Dharwad,Dindigul,Dubai,Durgapur,Ernakulam,Faridabad,Ganjam,Ghaziabad,Goa,Gulbarga,Guntur,Gurgaon,Guwahati,Gwalior,Haridwar,Hyderabad,Indore,Jabalpur,Jaipur,Jalandhar,Jamshedpur,Jhajjar,Jhansi,Jodhpur,Kannur,Kanpur,Karnal,Kochi,Kolhapur,Kolkata,Kottayam,Kozhikode,Krishna,Lucknow,Ludhiana,Madurai,Mandya,Mangalore,Mathura,Meerut,Mehsana,Moradabad,Mumbai,Mysore,Nagpur,Nainital,Nashik,Noida,Ooty,Panipat,Patiala,Patna,Pondicherry,Pune,Puri,Raigad,Ranchi,Ratnagiri,Rayagada,Rishikesh,Rohtak,Salem,Satara,Shimla,Silliguri,Solan,Solapur,Sonepat,Surat,Thiruvannamalai,Thrissur,Tirunelveli,Tirupati,Trichy,Trivandrum,Vadodara,Valsad,Varanasi,Vellore,Vijayawada,Visakhapatnam,Warangal,Wayanad";

        String[] cityList = cities.split(",");
        List<String> benchmarkList = new ArrayList<String>();
        
        for (String city : cityList) {
            try {
                URI uri = URI
                        .create(UriComponentsBuilder
                                .fromUriString(
                                        PropertyReader.getRequiredPropertyAsString(CorePropertyKeys.PROPTIGER_URL) + PropertyReader
                                                .getRequiredPropertyAsString(CorePropertyKeys.LOCALITY_API_URL)
                                                + "?"
                                                + URLGenerationConstants.SELECTOR
                                                + String.format(
                                                        URLGenerationConstants.SELECTOR_GET_LOCALITYNAMES_BY_CITYNAME,
                                                        city)).build().encode().toString());

                Date d1 = new Date();
                httpRequestUtil.getInternalApiResultAsTypeListFromCache(uri, Locality.class);
                Date d2 = new Date();
                long diff = d2.getTime() - d1.getTime();
                benchmarkList.add(city + "," + diff + "\n");
            }
            catch (Exception e) {
                LOGGER.warn("Caught exception while getting locality by cityname : " + city, e);
            }
        }
        
        File file = new File("/tmp/api.csv");
        try {
            FileUtils.writeLines(file, benchmarkList);
        }
        catch (IOException e) {
            throw new ProAPIException("Unable to write to file : " + file.getPath(), e);
        }
        
        return new APIResponse();
    }
}