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
import org.springframework.stereotype.Service;

import com.proptiger.data.model.Enquiry;
import com.proptiger.data.util.PropertyReader;

/**
 * @author Rajeev Pandey
 *
 */
@Service
public class LeadGenerationService {
	private static final String AMPERCENT = "&";
	private static final String EQUAL = "=";
	private static Logger logger = LoggerFactory.getLogger(LeadGenerationService.class);
	@Autowired
	private PropertyReader propertyReader;
	private URL url;
	
	@PostConstruct
	protected void init(){
		try {
			url = new URL(propertyReader.getRequiredProperty("lead.page.url"));
		} catch (MalformedURLException e) {
			logger.error("Exception while creating url for lead generation ",e);
		}
	}
	
	public void postLead(Enquiry enquiry){
		String result = "";
		String leadData = createLeadData(enquiry);
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setUseCaches(false);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type",
	                "application/x-www-form-urlencoded");

	        // Send the POST data
	        DataOutputStream dataOut = new DataOutputStream(
	                connection.getOutputStream());
			dataOut.writeBytes(leadData);
	        dataOut.flush();
	        dataOut.close();

	        DataInputStream in = new DataInputStream (connection.getInputStream ());

	        String temp;
	        while ((temp = in.readLine()) != null) {
	            result += temp;
	        }
	        in.close();

	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	    	if(connection != null){
	    		connection.disconnect();
	    	}
	    }
	}

	/*
	 * lead_name=Rajeev+Pandey&lead_email=rajeev.pandey%40proptiger.com&
	 * lead_phone
	 * =9953813699&lead_country=1&lead_query=I+need+more+information
	 * +about+this
	 * +project.&submit=Submit&lead_projectId=1342&lead_projectName
	 * =My+Woods&
	 * lead_cityId=20&lead_cityName=Noida&lead_localityId=&lead_ui_flag
	 * =enquiry
	 * &lead_ui_page=PROJECTDETAIL&lead_ui_php=projects.php&lead_ui_source
	 * =project_detail
	 * &lead_extra_bedrooms=&lead_extra_propertyType=&lead_ui_typeid
	 * =&lead_session_loc
	 * =1268&resaleNlaunchFlg=&formlocationinfo=open-enquiry-right
	 */
	private String createLeadData(Enquiry enquiry) {
		StringBuilder leadData = new StringBuilder();
		leadData.append("lead_name").append(EQUAL).append(enquiry.getName()).append(AMPERCENT);
		leadData.append("lead_email").append(EQUAL).append(enquiry.getEmail()).append(AMPERCENT);
		leadData.append("lead_phone").append(EQUAL).append(enquiry.getPhone()).append(AMPERCENT);
		leadData.append("lead_country").append(EQUAL).append(enquiry.getCountryOfResidence()).append(AMPERCENT);
		leadData.append("lead_query").append(EQUAL).append(enquiry.getQuery()).append(AMPERCENT);
		leadData.append("lead_projectId").append(EQUAL).append(enquiry.getProjectId()).append(AMPERCENT);
		leadData.append("lead_projectName").append(EQUAL).append(enquiry.getProjectName()).append(AMPERCENT);
		leadData.append("lead_cityId").append(EQUAL).append(enquiry.getCityId()).append(AMPERCENT);
		leadData.append("lead_cityName").append(EQUAL).append(enquiry.getCityName()).append(AMPERCENT);
		leadData.append("lead_localityId").append(EQUAL).append(enquiry.getLocalityId()).append(AMPERCENT);
		leadData.append("lead_ui_flag").append(EQUAL).append("").append(AMPERCENT);
		leadData.append("lead_ui_page").append(EQUAL).append("").append(AMPERCENT);
		leadData.append("lead_ui_php").append(EQUAL).append("").append(AMPERCENT);
		leadData.append("lead_ui_source").append(EQUAL).append("").append(AMPERCENT);
		leadData.append("lead_extra_bedrooms").append(EQUAL).append("").append(AMPERCENT);
		leadData.append("lead_extra_propertyType").append(EQUAL).append("").append(AMPERCENT);
		leadData.append("lead_ui_typeid").append(EQUAL).append("").append(AMPERCENT);
		leadData.append("lead_session_loc").append(EQUAL).append("").append(AMPERCENT);
		leadData.append("resaleNlaunchFlg").append(EQUAL).append("").append(AMPERCENT);
		leadData.append("formlocationinfo").append(EQUAL).append("");
		return leadData.toString();
	}
}
