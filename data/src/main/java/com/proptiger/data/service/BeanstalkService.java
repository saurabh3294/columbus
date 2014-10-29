package com.proptiger.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.core.model.proptiger.BeanstalkEnquiry;
import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClientImpl.ClientImpl;

@Service
public class BeanstalkService {
    
    private static Logger             logger = LoggerFactory.getLogger(BeanstalkService.class);

    @Autowired
    private PropertyReader          propertyReader;

    public boolean writeToBeanstalk(Enquiry enquiry) {
        ObjectMapper mapper = new ObjectMapper();

        BeanstalkEnquiry beanstalkEnquiry = enquiry.createBeanstalkEnquiryObj();
        // serialize enquiry to JSON format
        String enquiryJson = null;
        try {
            enquiryJson = mapper.writeValueAsString(beanstalkEnquiry);
        }
        catch (JsonProcessingException e) {
            return false;
        }

        System.out.println("JSON string: " + enquiryJson);

        Integer beanstalkPort = propertyReader.getRequiredPropertyAsType(PropertyKeys.BEANSTALK_PORT, Integer.class);
        String beanstalkQueue = propertyReader.getRequiredProperty(PropertyKeys.BEANSTALK_QUEUE_NAME);
        // TODO // change to production
        String beanstalkHost = propertyReader.getRequiredProperty(PropertyKeys.BEANSTALK_INTERNAL_SERVER);
        
        try {
            Client client = new ClientImpl(beanstalkHost, beanstalkPort);
            client.useTube(beanstalkQueue);
            long jobId = client.put(1024, 0, 60, enquiryJson.getBytes());

            if (jobId > 1) {
                return true;
            }
            else {
                logger.debug("Failed to write to Beanstalk");
                return false;
            }
        }
        catch (Exception e) {
            logger.debug("Failed to write to Beanstalk");
            return false;
        }

    }

}
