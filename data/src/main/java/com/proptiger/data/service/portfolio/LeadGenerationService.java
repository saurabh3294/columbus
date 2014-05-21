package com.proptiger.data.service.portfolio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.proptiger.data.enums.portfolio.LeadPageName;
import com.proptiger.data.enums.portfolio.LeadSaleType;
import com.proptiger.data.model.Enquiry;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.LeadPostException;

/**
 * Lead generation service, using lead.php to submit leads
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class LeadGenerationService {
    private static final String AMPERCEND = "&";
    private static final String EQUAL     = "=";
    private static Logger       logger    = LoggerFactory.getLogger(LeadGenerationService.class);
    @Autowired
    private PropertyReader      propertyReader;
    private URL                 url;

    @PostConstruct
    protected void init() {
        try {
            url = new URL(propertyReader.getRequiredProperty(PropertyKeys.LEAD_PAGE_URL));
        }
        catch (MalformedURLException e) {
            logger.error("Exception while creating url for lead generation ", e);
        }
    }

    /**
     * Creating connection with lead.php and submitting the lead request
     * 
     * @param enquiry
     */
    @Async
    public String postLead(Enquiry enquiry, LeadSaleType leadSaleType, LeadPageName leadPageName) {
        String result = "";
        String leadData = createLeadData(enquiry, leadSaleType, leadPageName);
        logger.debug("Posting a lead {}", leadData);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
            dataOut.writeBytes(leadData);
            dataOut.flush();
            dataOut.close();
            DataInputStream in = new DataInputStream(connection.getInputStream());
            String temp;
            while ((temp = in.readLine()) != null) {
                result += temp;
            }
            in.close();
            logger.error("Result from lead post=" + result);
        }
        catch (IOException e) {
            throw new LeadPostException("Lead could not be posted", e);
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * Creating lead data
     * 
     * @param enquiry
     * @param leadSaleType
     * @param leadPageName
     * @return
     */
    private String createLeadData(Enquiry enquiry, LeadSaleType leadSaleType, LeadPageName leadPageName) {
        StringBuilder leadData = new StringBuilder();
        leadData.append("lead_name").append(EQUAL).append(enquiry.getName()).append(AMPERCEND);
        leadData.append("lead_email").append(EQUAL).append(enquiry.getEmail()).append(AMPERCEND);
        leadData.append("lead_phone").append(EQUAL).append(enquiry.getPhone()).append(AMPERCEND);
        leadData.append("lead_country").append(EQUAL).append(enquiry.getCountryOfResidence()).append(AMPERCEND);
        leadData.append("lead_query").append(EQUAL).append(enquiry.getQuery()).append(AMPERCEND);
        leadData.append("lead_projectId").append(EQUAL).append(enquiry.getProjectId()).append(AMPERCEND);
        leadData.append("lead_projectName").append(EQUAL).append(enquiry.getProjectName()).append(AMPERCEND);
        leadData.append("lead_cityId").append(EQUAL).append(enquiry.getCityId()).append(AMPERCEND);
        leadData.append("lead_cityName").append(EQUAL).append(enquiry.getCityName()).append(AMPERCEND);
        leadData.append("lead_localityId").append(EQUAL).append(enquiry.getLocalityId()).append(AMPERCEND);
        leadData.append("lead_ui_flag").append(EQUAL).append("").append(AMPERCEND);
        leadData.append("lead_ui_page").append(EQUAL).append(leadPageName.getName()).append(AMPERCEND);
        leadData.append("lead_ui_php").append(EQUAL).append("").append(AMPERCEND);
        leadData.append("lead_ui_source").append(EQUAL).append("").append(AMPERCEND);
        leadData.append("lead_extra_bedrooms").append(EQUAL).append("").append(AMPERCEND);
        leadData.append("lead_extra_propertyType").append(EQUAL).append("").append(AMPERCEND);
        leadData.append("lead_ui_typeid").append(EQUAL).append("").append(AMPERCEND);
        leadData.append("lead_session_loc").append(EQUAL).append("").append(AMPERCEND);
        leadData.append("resaleNlaunchFlg").append(EQUAL).append(leadSaleType.getType()).append(AMPERCEND);
        leadData.append("formlocationinfo").append(EQUAL).append("");

        return leadData.toString();
    }
}
