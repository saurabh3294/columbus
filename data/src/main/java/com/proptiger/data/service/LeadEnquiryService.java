package com.proptiger.data.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.enums.lead.ProcessingStatus;
import com.proptiger.data.enums.lead.ProjectStatus;
import com.proptiger.data.enums.lead.SalesType;
import com.proptiger.data.enums.mail.MailTemplateDetail;
import com.proptiger.data.init.GACookies;
import com.proptiger.data.init.LeadValidator;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.internal.dto.mail.LeadSubmitMail;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.model.City;
import com.proptiger.data.model.Enquiry;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.Project;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.repo.LeadEnquiryDao;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.ProjectDatabaseDao;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.SecurityContextUtils;
import com.proptiger.exception.BadRequestException;

@Service
public class LeadEnquiryService {

    @Autowired
    LocalityService                 localityService;

    @Autowired
    CityService                     cityService;

    @Autowired
    CountryService                  countryService;

    @Autowired
    BeanstalkService                beanstalkService;

    @Autowired
    LeadEnquiryDao                  leadEnquiryDao;

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

    public Object createLeadEnquiry(Enquiry enquiry, HttpServletRequest request) {

        Map<String, Object> leadResponse = new LinkedHashMap<String, Object>();
        HashMap<String, String> leadInvalidations = new HashMap<String, String>();

        if (enquiry.getCountryId() != null) {
            enquiry.setCountry(countryService.getCountryOnId(enquiry.getCountryId()).getLabel());
        }
        if (enquiry.getEmail() != null) {
            enquiry.setEmail(enquiry.getEmail().toLowerCase());
        }

        LeadValidator leadValidator = new LeadValidator();
        leadInvalidations = leadValidator.validateLead(enquiry);

        if (!leadInvalidations.isEmpty()) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, leadInvalidations.toString());
        }
        else {
            List<String> projectNames = new ArrayList<String>();
            GACookies gaCookies = new GACookies();
            List<Long> enquiryIds = new ArrayList<Long>();

            enrichLeadQuery(enquiry);
            HashMap<String, String> cookieMap = setCookieInLead(enquiry, request);
            gaCookies.setGACookies(enquiry, cookieMap);

            if (enquiry.getMultipleProjectIds() != null && !enquiry.getMultipleProjectIds().isEmpty()) {

                for (Integer projectId : enquiry.getMultipleProjectIds()) {
                    Enquiry enquiryNew = enquiry;
                    enquiryNew.setProjectId(projectId);
                    enquiryNew = generateAndWriteLead(enquiryNew);
                    projectNames.add(enquiryNew.getProjectName());
                    enquiryIds.add(enquiryNew.getId());
                }
            }
            else {
                enquiry = generateAndWriteLead(enquiry);
                enquiryIds.add(enquiry.getId());
            }
            if (enquiry.getPpc()) {
                leadResponse.put("status", "success");
                leadResponse.put("ppc", "TRUE");
                leadResponse.put("enquiryid", enquiryIds);
            }
            else {
                leadResponse.put("status", "success");
                leadResponse.put("ppc", "FALSE");
                leadResponse.put("enquiryid", enquiryIds);
            }

            SendEmailRequest(enquiry, projectNames);
        }
        return leadResponse;
    }

    private Enquiry generateAndWriteLead(Enquiry enquiry) {

        generateLeadData(enquiry);

        Enquiry savedEnquiry = leadEnquiryDao.saveAndFlush(enquiry);
        enquiry.setId(savedEnquiry.getId());
        enquiry.setCreatedDate(savedEnquiry.getCreatedDate());

        if (((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) || enquiry
                .getBuySell() != "sell") {
            Boolean beanstalkResponse = beanstalkService.writeToBeanstalk(enquiry);
            if (!beanstalkResponse) {
                enquiry.setProcessingStatus(ProcessingStatus.unsuccessful);
            }
        }
        return enquiry;
    }

    private void SendEmailRequest(Enquiry enquiry, List<String> projectNames) {

        LeadSubmitMail dataForTemplate = null;
        String emailReceiver = null;
        MailBody mailBody = null;
        MailDetails mailDetails = null;

        // Separate Mail Logic in case of multiple ProjectIds
        if ((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) {
            // If project names not present, do not send mail
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
        leadMailData.setEnquiry(enquiry);

        if ((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) {
            List<Project> projects = projectDatabaseDao.getProjectsOnId(enquiry.getMultipleProjectIds());
            leadMailData.setProjects(projects);
            leadMailData.setLeadMailFlag("leadAcceptanceResale");
            return leadMailData;
        }

        if (enquiry.getBuySell() != null && enquiry.getBuySell().equals("sell")
                && enquiry.getProjectName() != null
                && enquiry.getProjectName() != "") {

            if (enquiry.getProject() == null) {
                Project projectDetails = projectDatabaseDao.getProjectOnId(enquiry.getProjectId());
                leadMailData.getEnquiry().setProject(projectDetails);
            }
            leadMailData.setLeadMailFlag("google_buy_sell_client");
        }

        else if (enquiry.getProjectName() != null && enquiry.getProjectName() != "") {

            if (enquiry.getProject() == null) {
                Project projectDetails = projectDatabaseDao.getProjectOnId(enquiry.getProjectId());
                leadMailData.getEnquiry().setProject(projectDetails);
            }
            leadMailData.setLeadMailFlag("leadAcceptance");
        }

        else if (enquiry.getPageName() != null && enquiry.getPageName().equals("HOMELOAN")) {
            if (enquiry.getCity() == null) {
                City city = cityService.getCityByName(enquiry.getCityName());
                leadMailData.getEnquiry().setCity(city);
            }
            leadMailData.setLeadMailFlag("homeloan");
        }

        else if ((enquiry.getProjectName() == null || enquiry.getProjectId() == null) && enquiry.getPageName() != null
                && (enquiry.getPageName().equals("GOOGLE 4") || enquiry.getPageName().equals("GOOGLE 8"))) {

            leadMailData.setLeadMailFlag("googlepage-withoutproject");
        }
        else if (enquiry.getPageName() != null && enquiry.getPageName().toLowerCase().equals("portfolio")) {

            leadMailData.setLeadMailFlag("my-portfolio");
        }

        else {
            leadMailData.setLeadMailFlag("leadAcceptancewithoutproject");
            if (enquiry.getLocality() == null) {
                Locality localityInfo = localityDao.getLocalityOnLocAndCity(
                        enquiry.getLocalityName(),
                        enquiry.getCityName());
                leadMailData.getEnquiry().setLocality(localityInfo);
            }
        }
        return leadMailData;
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

    public HashMap<String, String> setCookieInLead(Enquiry enquiry, HttpServletRequest request) {

        HashMap<String, String> cookieMap = new HashMap<String, String>();
        Cookie[] requestCookies = request.getCookies();

        if (request.getHeader("Referer") != null) {
            enquiry.setHttpReferer(request.getHeader("Referer"));
        }
        else {
            enquiry.setHttpReferer("");
        }
        if (enquiry.getResaleAndLaunchFlag() == null) {
            enquiry.setResaleAndLaunchFlag(request.getParameter("resaleNlaunchFlg"));
        }

        // Set application source of lead
        if (enquiry.getApplicationType() == null) {
            UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
            ReadableUserAgent agent = parser.parse(request.getHeader("User-Agent"));
            String applicationSource = agent.getDeviceCategory().getName();

            if (!applicationSource.isEmpty() && (applicationSource.toLowerCase().equals("PDA") || applicationSource
                    .toLowerCase().equals("SMARTPHONE"))) {
                enquiry.setApplicationType("Mobile Site");
            }

            else if (!applicationSource.isEmpty() && applicationSource.toLowerCase().equals("TABLET")) {
                enquiry.setApplicationType("Tablet Site");
            }
            else {
                enquiry.setApplicationType("Desktop Site");
            }
        }

        if (requestCookies != null) {
            for (Cookie c : requestCookies) {
                try {
                    cookieMap.put(c.getName(), URLDecoder.decode(c.getValue(), "UTF-8"));
                    c.setValue(URLDecoder.decode(c.getValue(), "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (request.getHeader("IP") != null) {
                    enquiry.setIp(request.getHeader("IP"));
                }
                else {
                    if (c.getName().equals("USER_IP")) {
                        enquiry.setIp(c.getValue());
                    }
                    else {
                        enquiry.setIp(request.getRemoteAddr());
                    }
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
            }
        }
        if (enquiry.getUserMedium() == null) {
            enquiry.setUserMedium("");
        }
        if (enquiry.getUser() == null) {
            enquiry.setUser("");
        }
        if (enquiry.getSource() == null) {
            enquiry.setSource("");
        }
        if (enquiry.getKeywords() == null) {
            enquiry.setKeywords("");
        }
        if (enquiry.getAdGrp() == null) {
            enquiry.setAdGrp("");
        }
        if (enquiry.getLandingPage() == null) {
            enquiry.setLandingPage("");
        }
        if (enquiry.getCampaign() == null) {
            enquiry.setCampaign("");
        }

        return cookieMap;
    }

    private void generateLeadData(Enquiry enquiry) {

        Locality localityInfo = null;
        Project projectInfo = null;

        if (enquiry.getProjectId() == null || enquiry.getProjectId() < 0) {
            enquiry.setProjectId(0);
        }
        if (enquiry.getProjectName() == null) {
            enquiry.setProjectName("");
        }
        if (enquiry.getCityId() == null || enquiry.getCityId() < 0) {
            enquiry.setCityId(0);
        }
        if (enquiry.getCityName() == null) {
            enquiry.setCityName("");
        }
        if (enquiry.getPageName() == null) {
            enquiry.setPageName("");
        }
        if (enquiry.getPageUrl() == null) {
            enquiry.setPageUrl("");
        }
        if ((enquiry.getBudgetFlag() == null) || !enquiry.getBudgetFlag()) {
            enquiry.setBudget("");
        }
        if ((enquiry.getHomeLoanTypeFlag() == null) || !enquiry.getHomeLoanTypeFlag()) {
            enquiry.setHomeLoanType("");
        }
        if (enquiry.getProjectId() != 0) {
            projectInfo = projectDatabaseDao.getProjectOnId(enquiry.getProjectId());
            if (projectInfo != null) {
                enquiry.setProjectName(projectInfo.getName());
                enquiry.setLocalityId(projectInfo.getLocalityId());
                enquiry.setCityId(projectInfo.getLocality().getSuburb().getCity().getId());
                enquiry.setCityName(projectInfo.getLocality().getSuburb().getCity().getLabel());
                enquiry.setProject(projectInfo);
            }
        }
        else if (enquiry.getLocalityName() != null && enquiry.getCityName() != "") {

            localityInfo = localityDao.getLocalityOnLocAndCity(enquiry.getLocalityName(), enquiry.getCityName());
        }

        else if (enquiry.getLocalityId() != null) {
            localityInfo = localityDao.getLocalityOnId(enquiry.getLocalityId());
        }

        else if (enquiry.getPageName().equals("RESALE PAGE")) {
            localityInfo = localityDao.getLocalityOnId(enquiry.getLocalityId());
            enquiry.setProjectName("Resale");
        }

        else if (enquiry.getPageName().equals("HOMELOAN") && enquiry.getCityName() != "") {
            City city = cityService.getCityByName(enquiry.getCityName());
            if (city != null) {
                enquiry.setCityId(city.getId());
                enquiry.setCityName(city.getLabel());
                enquiry.setCity(city);
            }
        }

        else if (enquiry.getPageName().equals("AMENITIES") && (enquiry.getMultipleProjectIds() == null || enquiry
                .getMultipleProjectIds().isEmpty()) && enquiry.getCityName() != "") {
            localityInfo = localityDao.getLocalityOnLocAndCity(enquiry.getLocalityName(), enquiry.getCityName());
        }

        if (localityInfo != null) {
            enquiry.setLocalityId(localityInfo.getLocalityId());
            enquiry.setCityId(localityInfo.getSuburb().getCity().getId());
            enquiry.setCityName(localityInfo.getSuburb().getCity().getLabel());
            enquiry.setLocality(localityInfo);
        }

        if ((enquiry.getMultipleProjectIds() == null || enquiry.getMultipleProjectIds().isEmpty()) && enquiry
                .getBuySell() != null && enquiry.getBuySell().equals("sell")) {
            enquiry.setProcessingStatus(ProcessingStatus.processed);
        }
        else {
            enquiry.setProcessingStatus(ProcessingStatus.processing);
        }

        // set Sales Type
        setSalesTypeInEnquiry(enquiry, projectInfo);

        // Set if user is registered
        ActiveUser user = SecurityContextUtils.getLoggedInUser();
        if (user == null) {
            enquiry.setRegisteredUser("NO");
        }
        else {
            enquiry.setRegisteredUser("YES");
        }

    }

    private void setSalesTypeInEnquiry(Enquiry enquiry, Project project) {
        if (((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) || (enquiry
                .getPageName().equals("GOOGLE 9") || enquiry.getPageName().equals("portfolio"))) {

            if (enquiry.getResaleAndLaunchFlag() != null && enquiry.getResaleAndLaunchFlag().equals("1")) {
                enquiry.setSalesType(SalesType.primary);
            }
            else {
                enquiry.setSalesType(SalesType.resale);
            }
        }
        else if (enquiry.getBuySell() != null && enquiry.getBuySell().equals("sell")) {
            enquiry.setSalesType(SalesType.seller);
        }
        else if ((enquiry.getHomeLoanType() != null) && !enquiry.getHomeLoanType().isEmpty()) {
            enquiry.setSalesType(SalesType.homeloan);
        }
        else {
            if (project != null) {
                if (project.isForceResale()) {
                    enquiry.setSalesType(SalesType.resale);
                }
                else {
                    if (project.getProjectStatus() != null && !project.getProjectStatus().equals(
                            ProjectStatus.CANCELLED)
                            && project.getProjectStatus().equals(ProjectStatus.ONHOLD)) {
                        if (project.getDerivedAvailability() == null) {
                            if (project.getProjectStatus() != null && project.getProjectStatus().equals(
                                    ProjectStatus.COMPLETED)) {
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

    // TODO cookie autofill, facebook, tracker

    public PaginatedResponse<?> updateLeadEnquiry(Enquiry enquiry, HttpServletRequest request) {
        return null;
    }
}
