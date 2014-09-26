package com.proptiger.data.service;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.enums.lead.ProcessingStatus;
import com.proptiger.data.enums.lead.SalesType;
import com.proptiger.data.enums.mail.MailTemplateDetail;
import com.proptiger.data.handler.LeadValidator;
import com.proptiger.data.internal.dto.mail.LeadSubmitMail;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.BeanstalkEnquiry;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Enquiry;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.LeadDao;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.ProjectDatabaseDao;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ProAPIException;
import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClientImpl.ClientImpl;

@Service
public class LeadService {

    @Autowired
    LocalityService                 localityService;

    @Autowired
    CityService                     cityService;

    @Autowired
    CountryService                  countryService;

    @Autowired
    LeadDao                         leadDao;

    @Autowired
    ProjectDatabaseDao              projectDatabaseDao;

    @Autowired
    LocalityDao                     localityDao;

    @Autowired
    private TemplateToHtmlGenerator mailBodyGenerator;

    @Autowired
    private MailSender              mailSender;

    @Autowired
    private PropertyReader          propertyReader;

    HashMap<String, String>         leadInvalidations = new HashMap<String, String>();
    HashMap<String, String>         cookieMap         = new HashMap<String, String>();
    List<String>                    projectNames      = new ArrayList<String>();

    public String createLeadEnquiry(Enquiry enquiry, HttpServletRequest request, BindingResult result) {

        String leadresponse = null;

        if (enquiry.getCountryId() != null) {
            enquiry.setCountry(countryService.getCountryOnId(enquiry.getCountryId()).getLabel());
        }
        if (enquiry.getEmail() != null) {
            enquiry.setEmail(enquiry.getEmail().toLowerCase());
        }

        // negative Ids
        LeadValidator leadValidator = new LeadValidator();
        leadValidator.validate(enquiry, result);
        StringBuilder strbuilder = new StringBuilder();

        if (result.hasErrors()) {
            List<ObjectError> errors = result.getGlobalErrors();
            strbuilder.append("Lead : Server-Side validation failed");
            for (ObjectError error : errors) {
                strbuilder.append(error.getDefaultMessage());
            }
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new StringReader(strbuilder.toString()));
            reader.setLenient(true);
            Object finalJson = gson.fromJson(reader, Object.class);

            // TODO to return json
            throw new ProAPIException(ResponseCodes.BAD_REQUEST, strbuilder.toString());
        }
        else {
            enrichLeadQuery(enquiry);
            setCookieInLead(enquiry, request);

                readGACookies(enquiry, request);
            List<Long> enquiryIds = new ArrayList<Long>();

            if (enquiry.getMultipleProjectIds() != null && !enquiry.getMultipleProjectIds().isEmpty()) {

                for (Integer projectId : enquiry.getMultipleProjectIds()) {

                    Enquiry enquiryNew = enquiry;
                    enquiryNew.setProjectId(projectId);
                    projectNames.add(enquiryNew.getProjectName());
                    generateAndWriteLead(enquiryNew);
                    enquiryIds.add(enquiryNew.getId());
                }
            }
            else {
                generateAndWriteLead(enquiry);
                enquiryIds.add(enquiry.getId());
            }
            // eqnruiyId going zero
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            if (enquiry.getPpc()) {
                map.put("status", "success");
                map.put("ppc", "TRUE");
                map.put("enquiryid", enquiryIds);
                Gson gson = new Gson();
                leadresponse = gson.toJson(map);
            }
            else {
                map.put("status", "success");
                map.put("ppc", "FALSE");
                map.put("enquiryid", enquiryIds);
                Gson gson = new Gson();
                leadresponse = gson.toJson(map);
            }

            SendEmailRequest(enquiry);
        }
        // returning here might leave beanstalk failing
        return leadresponse;
    }

    private void generateAndWriteLead(Enquiry enquiry) {

        generateLeadData(enquiry);

        if ((enquiry.getMultipleProjectIds() == null || enquiry.getMultipleProjectIds().isEmpty()) && enquiry
                .getBuySell() != null && enquiry.getBuySell().equals("sell")) {
            enquiry.setProcessingStatus(ProcessingStatus.processed);
        }
        else {
            enquiry.setProcessingStatus(ProcessingStatus.processing);
        }
        enquiry.setGaPpc(1);
        enquiry = leadDao.saveAndFlush(enquiry);
         try {
         enquiry = leadDao.saveAndFlush(enquiry);
         }
         catch (Exception exception) {
         throw new ProAPIException(
         ResponseCodes.DATABASE_CONNECTION_ERROR,
         "Lead : All validations passed but still unable to insert into enquiry table");
         }

        if (((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) || enquiry
                .getBuySell() != "sell") {
            if (!writeToBeanstalk(enquiry)) {
                enquiry.setProcessingStatus(ProcessingStatus.unsuccessful);
            }
        }
    }

    private void SendEmailRequest(Enquiry enquiry) {

        LeadSubmitMail dataForTemplate = null;
        String emailReceiver = null;
        MailBody mailBody = null;
        MailDetails mailDetails = null;

        // Separate Mail Logic in case of multiple ProjectIds
        if ((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) {

            // If project names(details) not present, do not send mail
            if (projectNames.isEmpty()) {
                return;
            }

            dataForTemplate = generateDataToMail(enquiry);
            emailReceiver = enquiry.getEmail();
            mailBody = mailBodyGenerator.generateMailBody(MailTemplateDetail.LEAD_GENERATION, dataForTemplate);
            mailDetails = new MailDetails(mailBody).setMailTo(emailReceiver);
            mailSender.sendMailUsingAws(mailDetails);
            return;
        }

        // Send mail if lead is not from Contact Us
        if ((enquiry.getPageName() != null) && !enquiry.getPageName().equals("CONTACT US")) {
            dataForTemplate = generateDataToMail(enquiry);
            emailReceiver = enquiry.getEmail();
            mailBody = mailBodyGenerator.generateMailBody(MailTemplateDetail.LEAD_GENERATION, dataForTemplate);
            mailDetails = new MailDetails(mailBody).setMailTo(emailReceiver);
            mailSender.sendMailUsingAws(mailDetails);
        }

        // Send Internal mail to Proptiger in case of sell lead
        if (enquiry.getBuySell() != null && enquiry.getBuySell().equals("sell")
                && enquiry.getProjectName() != null
                && enquiry.getProjectName() != "") {
            dataForTemplate.setLeadMailFlag("google_buy_sell");
            mailBody = mailBodyGenerator.generateMailBody(MailTemplateDetail.LEAD_GENERATION, dataForTemplate);
            emailReceiver = propertyReader.getRequiredProperty(PropertyKeys.MAIL_LEAD_INTERNAL_RECIPIENT);
            mailDetails = new MailDetails(mailBody).setMailTo(emailReceiver);
            mailSender.sendMailUsingAws(mailDetails);
        }
    }

    private LeadSubmitMail generateDataToMail(Enquiry enquiry) {
        LeadSubmitMail leadMailData = new LeadSubmitMail();

        if ((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) {
            List<Project> projects = projectDatabaseDao.getProjectsOnId(enquiry.getMultipleProjectIds());

            StringBuilder projectDetail = new StringBuilder();
            for (Project project : projects) {
                projectDetail.append("<a href='http://www.proptiger.com/" + project.getURL()
                        + "' style='text-decoration:none;'>"
                        + project.getBuilder().getName()
                        + " "
                        + project.getName()
                        + "</a>, ");
            }
            String projectDetailString = projectDetail.toString().substring(0, projectDetail.toString().length() - 2);
            leadMailData.setProjectsDetail(projectDetailString);
            leadMailData.setLeadMailFlag("leadAcceptanceResale");
            leadMailData.setEnquiry(enquiry);
            return leadMailData;
        }

        if (enquiry.getBuySell() != null && enquiry.getBuySell().equals("sell")
                && enquiry.getProjectName() != null
                && enquiry.getProjectName() != "") { // empty to null

            Project projectDetails = projectDatabaseDao.getProjectOnId(enquiry.getProjectId());

            // TODO set projecturl
            leadMailData.setProject(projectDetails);
            leadMailData.setEnquiry(enquiry);
            leadMailData.setLeadMailFlag("google_buy_sell_client");
        }

        else if (enquiry.getProjectName() != null && enquiry.getProjectName() != "") {

            Project projectDetails = projectDatabaseDao.getProjectOnId(enquiry.getProjectId());

            leadMailData.setProject(projectDetails);
            leadMailData.setEnquiry(enquiry);
            leadMailData.setLeadMailFlag("leadAcceptance");
        }

        else if (enquiry.getPageName().equals("HOMELOAN")) {

            City city = cityService.getCityByName(enquiry.getCityName());

            leadMailData.setLeadMailFlag("homeloan");
            leadMailData.setCity(city);
            leadMailData.setEnquiry(enquiry);
        }

        if ((enquiry.getProjectName() == null || enquiry.getProjectId() == null) && enquiry.getPageName().equals(
                "GOOGLE 4")
                || enquiry.getPageName().equals("GOOGLE 8")) {

            leadMailData.setLeadMailFlag("googlepage-withoutproject");
            leadMailData.setEnquiry(enquiry);
        }
        else if (enquiry.getPageName().toLowerCase().equals("portfolio")) {

            leadMailData.setLeadMailFlag("my-portfolio");
            leadMailData.setEnquiry(enquiry);
        }

        else {

            Locality localityInfo = localityDao.getLocalityOnLocAndCity(
                    enquiry.getLocalityName(),
                    enquiry.getCityName());

            leadMailData.setLeadMailFlag("leadAcceptancewithoutproject");
            leadMailData.setLocality(localityInfo);
            leadMailData.setEnquiry(enquiry);
        }
        return leadMailData;
    }

    private boolean writeToBeanstalk(Enquiry enquiry) {
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
        // enquiry id not setting
        String beanstalkHost = propertyReader.getRequiredProperty(PropertyKeys.BEANSTALK_INTERNAL_SERVER);

        try {
            Client client = new ClientImpl(beanstalkHost, beanstalkPort);
            client.useTube(beanstalkQueue);
            long jobId = client.put(1024, 0, 60, enquiryJson.getBytes());

            if (jobId > 1) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }

    }

    public void enrichLeadQuery(Enquiry enquiry) {

        StringBuilder leadQuery = new StringBuilder();
        leadQuery.append(enquiry.getQuery());

        if (enquiry.getBedrooms() != null) {
            leadQuery.append(" : [ Bedrooms = " + enquiry.getBedrooms() + " ]");
        }
        if (enquiry.getBudget() != null) {
            leadQuery.append(" : [ Budget = " + enquiry.getBudget() + " ]");
        }
        if (enquiry.getPropertyType() != null) {
            leadQuery.append(" : [ PropertyType = " + enquiry.getPropertyType() + " ]");
        }
        if (enquiry.getHomeLoanType() != null) {
            leadQuery.append(" : [ Home Loan Purpose = " + enquiry.getHomeLoanType() + " ]");
        }
        if (enquiry.getLocalityId() != null && enquiry.getPageName() != null
                && enquiry.getPageName().equals("RESALE PAGE")) {
            String locName = localityDao.getLocalityOnId(enquiry.getLocalityId()).getLabel();
            leadQuery.append(" : [ Enquiry for resale property in " + locName + " ]");
        }

        enquiry.setQuery(leadQuery.toString());
    }

    public void setCookieInLead(Enquiry enquiry, HttpServletRequest request) {
        Cookie[] requestCookies = request.getCookies();
        Enumeration<String> aEnumeration = request.getHeaderNames();
        System.out.println(aEnumeration);
        enquiry.setHttpReferer(request.getHeader("Referer")); // TODO
        enquiry.setIp(request.getRemoteAddr());

        if (requestCookies != null) {
            for (Cookie c : requestCookies) {
                System.out.println(c.getName());
                System.out.println(c.getValue() + '\n');
                try {
                    cookieMap.put(c.getName(), URLDecoder.decode(c.getValue(), "UTF-8"));
                    c.setValue(URLDecoder.decode(c.getValue(), "UTF-8"));
                    System.out.println(c.getValue() + '\n');
                    System.out.println(c.getName() + c.getName() + c.getName());
                }
                catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (c.getName().equals("LANDING_PAGE")) {
                    enquiry.setLandingPage(c.getValue());
                }
                else if (c.getName().equals("USER_CAMPAIGN")) {
                    enquiry.setCampaign(c.getValue());
                }
                else if (c.getName().equals("USER_ADGROUP")) {
                    enquiry.setAdGrp(c.getValue());
                }
                else if (c.getName().equals("USER_KEYWORD")) {
                    enquiry.setKeywords(c.getValue());
                }
                else if (c.getName().equals("USER_FROM")) {
                    enquiry.setSource(c.getValue());
                }
                else if (c.getName().equals("USER_ID")) {
                    enquiry.setUser(c.getValue());
                }
                else if (c.getName().equals("USER_MEDIUM")) {
                    enquiry.setUserMedium(c.getValue());
                }
                // TODO
                // registerflag left
            }
        }
    }

    private void readGACookies(Enquiry enquiry, HttpServletRequest request) {

        String campaignSource = null;
        String campaignName = null;
        String campaignMedium = null;
        String campaignTerm = null;
        String campaignContent = null;
        String randomid = null;
        
        if (cookieMap.containsKey("__utma") && cookieMap.containsKey("__utmz")) {

            // Parse __utmz cookie
        String[] utmzCookies = cookieMap.get("__utmz").split("\\.", 5);
        String campaignData = utmzCookies[4];

        Map<String, String> fbParam = new HashMap<String, String>();

        if (campaignData.contains("|")) {
            String[] pairs = campaignData.split("\\|");
            for (String pair : pairs) {
                String[] keyval = pair.split("=");
                fbParam.put(keyval[0], keyval[1]);
            }

            /*
             * You should tag you campaigns manually to have a full view of your
             * adwords campaigns data.
             */
            campaignSource = fbParam.get("utmcsr");
            campaignName = fbParam.get("utmccn");
            campaignMedium = fbParam.get("utmcmd");
            campaignTerm = fbParam.get("utmctr");
            campaignContent = fbParam.get("utmcct");

            if (fbParam.containsKey("utmgclid")) {
                campaignSource = "google";
                campaignMedium = "cpc";
                campaignTerm = fbParam.get("utmctr");
            }
        }
        // Parse the __utma Cookie
        String[] utmaCookies = cookieMap.get("__utma").split("\\.");
        randomid = utmaCookies[1];

        DateFormat format = new SimpleDateFormat("Y-m-d H:m:s");
        // format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        long currentTime = System.currentTimeMillis() / 1000l;
        long timeSpent = (currentTime - Long.parseLong(utmaCookies[4])) * 1000;
        String dateString = DurationFormatUtils.formatDuration(timeSpent, "'0-0-'d' 'H':'m':'s");
        enquiry.setGaTimespent(dateString); // wrong
        }

        if (cookieMap.get("USER_NETWORK") != null) {
            enquiry.setGaNetwork(cookieMap.get("USER_NETWORK").toLowerCase().trim());
            // logMsg(TRACE, "in lead.php network is: " .$ga_network);
        }

        enquiry.setGaSource(campaignSource);
        enquiry.setGaMedium(campaignMedium);
        enquiry.setGaKeywords(campaignTerm);
        enquiry.setGaCampaign(campaignName);
        enquiry.setGaUserId(randomid);
        
        if (enquiry.getGaMedium() != null && (enquiry.getGaMedium().toLowerCase().trim().equals("ppc") || enquiry
                .getGaMedium().toLowerCase().trim().equals("cpc")
                || enquiry.getGaMedium().toLowerCase().trim().equals("external mailer")
                || enquiry.getGaMedium().toLowerCase().trim().equals("externalmailer")
                || enquiry.getGaMedium().toLowerCase().trim().equals("mailer external")
                || enquiry.getGaMedium().toLowerCase().trim().equals("mailerexternal")
                || enquiry.getGaMedium().toLowerCase().trim().equals("banner") || !enquiry.getGaSource().toLowerCase()
                .trim().equals("banner_ad"))) {
            enquiry.setPpc(true);
            enquiry.setGaPpc(1);
        }
        else {
            enquiry.setPpc(false);
            enquiry.setGaPpc(0);
        }
        // can be set before
        // setting to ''
        if (campaignSource == null) {
            enquiry.setGaSource(enquiry.getSource());
        }
        if (campaignMedium == null) {
            enquiry.setGaMedium(enquiry.getUserMedium());
        }
    }

    private void generateLeadData(Enquiry enquiry) {

        Locality localityInfo = null;
        Project projectInfo = null;
        if (enquiry.getProjectId() != null) {
            projectInfo = projectDatabaseDao.getProjectOnId(enquiry.getProjectId());
            if (projectInfo != null) {
                enquiry.setProjectName(projectInfo.getName());
                enquiry.setLocalityId(projectInfo.getLocalityId());
                enquiry.setCityId(projectInfo.getLocality().getSuburb().getCity().getId());
                enquiry.setCityName(projectInfo.getLocality().getSuburb().getCity().getLabel());
            }
        }
        else if (enquiry.getLocalityName() != null && enquiry.getCityName() != null) {

            localityInfo = localityDao.getLocalityOnLocAndCity(enquiry.getLocalityName(), enquiry.getCityName());
        }

        else if (enquiry.getLocalityId() != null) {
            localityInfo = localityDao.getLocalityOnId(enquiry.getLocalityId());
        }

        else if (enquiry.getPageName().equals("RESALE PAGE")) {
            localityInfo = localityDao.getLocalityOnId(enquiry.getLocalityId());
            enquiry.setProjectName("Resale");
        }

        else if (enquiry.getPageName().equals("HOMELOAN") && enquiry.getCityName() != null) {
            City city = cityService.getCityByName(enquiry.getCityName());
            if (city != null) {
                enquiry.setCityId(city.getId());
                enquiry.setCityName(city.getLabel());
            }
        }

        else if (enquiry.getPageName().equals("AMENITIES") && (enquiry.getMultipleProjectIds() == null || enquiry
                .getMultipleProjectIds().isEmpty())) {
            localityInfo = localityDao.getLocalityOnLocAndCity(enquiry.getLocalityName(), enquiry.getCityName());
        }

        if (localityInfo != null) {
            enquiry.setLocalityId(localityInfo.getLocalityId());
            enquiry.setCityId(localityInfo.getSuburb().getCity().getId());
            enquiry.setCityName(localityInfo.getSuburb().getCity().getLabel());
        }

        setSalesTypeInEnquiry(enquiry, projectInfo);
    }

    private void setSalesTypeInEnquiry(Enquiry enquiry, Project project) {
        if (((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) || (enquiry
                .getPageName().equals("GOOGLE 9") || enquiry.getPageName().equals("portfolio"))) {
            if (enquiry.getResaleAndLaunchFlag()) {
                enquiry.setSalesType(SalesType.primary);
            }
            else {
                enquiry.setSalesType(SalesType.resale);
            }
        }
        else if (enquiry.getBuySell() != null && enquiry.getBuySell().equals("sell")) {
            enquiry.setSalesType(SalesType.seller);
        }
        else if (enquiry.getHomeLoanType() != null) {
            enquiry.setSalesType(SalesType.homeloan);
        }
        else {
            if (project != null) {
                if (project.isForceResale()) {
                    enquiry.setSalesType(SalesType.resale);
                }
                else {
                    if (project.getProjectStatus() != "Cancelled" && project.getProjectStatus() != "On Hold") {
                        if (project.getDerivedAvailability() == null) {
                            if (project.getProjectStatus() != null && project.getProjectStatus().equals("Completed")) {
                                enquiry.setSalesType(SalesType.primary);
                            }
                            else {
                                enquiry.setSalesType(SalesType.primary);
                            }
                        }
                        else if (project.getDerivedAvailability() == 0) {
                            enquiry.setSalesType(SalesType.resale);
                        }
                        else if (project.getDerivedAvailability() > 0) {
                            enquiry.setSalesType(SalesType.primary);
                        }
                    }
                    else {
                        enquiry.setSalesType(SalesType.primary);
                    }
                }
            }
        }
    }

    // TODO set cookie autofill

    public PaginatedResponse<?> updateLeadEnquiry(Enquiry enquiry, HttpServletRequest request) {
        return null;
    }
}
