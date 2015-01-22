package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.MailTemplateDetail;
import com.proptiger.core.enums.ProcessingStatus;
import com.proptiger.core.enums.SalesType;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.internal.dto.mail.MailBody;
import com.proptiger.core.internal.dto.mail.MailDetails;
import com.proptiger.core.model.cms.City;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.cms.Project;
import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.core.model.proptiger.Enquiry.LeadEnquiryResponse;
import com.proptiger.core.model.user.User;
import com.proptiger.core.model.user.UserContactNumber;
import com.proptiger.core.service.mail.MailSender;
import com.proptiger.core.service.mail.TemplateToHtmlGenerator;
import com.proptiger.core.service.security.SecurityUtilService;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.SecurityContextUtils;
import com.proptiger.data.internal.dto.mail.LeadSubmitMail;
import com.proptiger.data.model.EnquiryAttributes;
import com.proptiger.data.repo.EnquiryAttributesDao;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.LocalityDao;
import com.proptiger.data.repo.ProjectDaoNew;
import com.proptiger.data.service.user.UserServiceHelper;
import com.proptiger.data.util.lead.LeadCookiesHandler;
import com.proptiger.data.util.lead.LeadGACookiesHandler;
import com.proptiger.data.util.lead.LeadValidator;

@Service
public class EnquiryService {

    private static final int        FORCE_RESALE_VALUE  = 1;

    private static final int        FORCE_PRIMARY_VALUE = 2;

    @Autowired
    LocalityService                 localityService;

    @Autowired
    CityService                     cityService;

    @Autowired
    CountryService                  countryService;

    @Autowired
    BeanstalkService                beanstalkService;

    @Autowired
    SecurityUtilService             securityUtilService;

    @Autowired
    CookiesService                  cookieService;

    @Autowired
    EnquiryDao                      enquiryDao;

    @Autowired
    EnquiryAttributesDao            enquiryAttributesDao;

    @Autowired
    ProjectDaoNew                   projectDaoNew;

    @Autowired
    LocalityDao                     localityDao;

    @Autowired
    private TemplateToHtmlGenerator mailBodyGenerator;

    @Autowired
    private MailSender              mailSender;

    @Autowired
    private PropertyReader          propertyReader;
    
    @Autowired
    private UserServiceHelper userServiceHelper;
    private static Logger                    logger = LoggerFactory.getLogger(EnquiryService.class);

    @Transactional
    public Object createLeadEnquiry(Enquiry enquiry, HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> requestCookiesMap = cookieService.setCookies(request, response);
        HashMap<String, String> leadInvalidations = new HashMap<String, String>();

        if (enquiry.getCountryId() != null) {
            enquiry.setCountry(countryService.getCountryOnId(enquiry.getCountryId()).getLabel());
        }
        if (enquiry.getEmail() != null) {
            enquiry.setEmail(enquiry.getEmail().toLowerCase());
        }

        LeadValidator leadValidator = new LeadValidator();
        leadInvalidations = leadValidator.validateLead(enquiry);

        LeadEnquiryResponse leadResponse = null;
        if (!leadInvalidations.isEmpty()) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, leadInvalidations.toString());
        }
        else {
            List<String> projectNames = new ArrayList<String>();
            List<Long> enquiryIds = new ArrayList<Long>();

            LeadCookiesHandler cookies = new LeadCookiesHandler();
            LeadGACookiesHandler gaCookies = new LeadGACookiesHandler();

            enrichLeadQuery(enquiry);
            cookies.setCookies(enquiry, request, requestCookiesMap);
            gaCookies.setGACookies(enquiry, requestCookiesMap);

            if (enquiry.getMultipleProjectIds() != null && !enquiry.getMultipleProjectIds().isEmpty()) {

                for (Integer projectId : enquiry.getMultipleProjectIds()) {
                    Enquiry enquiryNew = enquiry;
                    enquiryNew.setProjectId(projectId);
                    enquiryNew = generateAndWriteLead(enquiryNew, request);
                    projectNames.add(enquiryNew.getProjectName());
                    enquiryIds.add(enquiryNew.getId());
                }
            }
            else {
                enquiry = generateAndWriteLead(enquiry, request);
                enquiryIds.add(enquiry.getId());
                updateUserDetails(enquiry);
            }

            if (enquiry.getPpc()) {
                enquiry.setTrackingFlag(true);
            }

            leadResponse = new LeadEnquiryResponse(enquiry, enquiryIds);

            sendEmailRequest(enquiry, projectNames);
        }

        createAutofillCookie(enquiry, response, request);
        return leadResponse;
    }

    private void createAutofillCookie(Enquiry enquiry, HttpServletResponse response, HttpServletRequest request) {

        Map<String, Object> enquiryMap = new HashMap<String, Object>();
        enquiryMap.put("name", enquiry.getName());
        enquiryMap.put("email", enquiry.getEmail());
        enquiryMap.put("phone", enquiry.getPhone());
        enquiryMap.put("country", enquiry.getCountryId());

        Gson gson = new Gson();
        String enquiryJson = gson.toJson(enquiryMap);

        Long currentTime = System.currentTimeMillis() / 1000L;
        Cookie enquiryCookie = new Cookie("enquiry_info", enquiryJson);
        enquiryCookie.setMaxAge(currentTime.intValue() + (3600 * 24 * 7));
        enquiryCookie.setPath("/");
        response.addCookie(enquiryCookie);
    }

    private void setRedirectUrl(Enquiry enquiry, HttpServletRequest request) {

        if (enquiry.getPageName().contains("GOOGLE")) {

            if (enquiry.getProject() != null) {
                enquiry.setRedirectUrl(request.getScheme() + "://"
                        + request.getServerName()
                        + "/"
                        + enquiry.getProject().getURL());
            }
            else {
                enquiry.setRedirectUrl(request.getScheme() + "://" + request.getServerName());
            }
        }
        else {
            enquiry.setRedirectUrl(enquiry.getHttpReferer());
        }

    }

    private void updateUserDetails(Enquiry enquiry) {
        User user = null;
        try {
            user = userServiceHelper.getUserByEmail_CallerNonLogin(enquiry.getEmail());
        }
        catch (Exception e) {
            logger.error("User with email id {} not found from userservice", enquiry.getEmail());;
        }

        if ((user != null && user.getUserAuthProviderDetails() != null) && !user.getUserAuthProviderDetails().isEmpty()) {
            User newUser = new User();
            UserContactNumber userContactNumber = new UserContactNumber();
            userContactNumber.setContactNumber(enquiry.getPhone());
            Set<UserContactNumber> contactNumbers = new HashSet<UserContactNumber>();
            contactNumbers.add(userContactNumber);
            newUser.setContactNumbers(contactNumbers);
            newUser.setId(user.getId());
            newUser.setEmail(user.getEmail());
            userServiceHelper.createOrPatchUser_CallerNonLogin(newUser);
        }
    }

    private Enquiry generateAndWriteLead(Enquiry enquiry, HttpServletRequest request) {

        generateLeadData(enquiry);
        setRedirectUrl(enquiry, request);
        Enquiry savedEnquiry = enquiryDao.saveAndFlush(enquiry);
        enquiry.setId(savedEnquiry.getId());
        enquiry.setCreatedDate(savedEnquiry.getCreatedDate());

        if (((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) || enquiry
                .getBuySell() != "sell") {
            Boolean beanstalkResponse = beanstalkService.writeToBeanstalk(enquiry);
            if (!beanstalkResponse) {
                enquiry.setProcessingStatus(ProcessingStatus.unsuccessful);
                enquiry = enquiryDao.saveAndFlush(enquiry);
            }
        }
        return enquiry;
    }

    private void sendEmailRequest(Enquiry enquiry, List<String> projectNames) {

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

            if (!enquiry.getCityName().isEmpty() && !checkIfServingCity(enquiry)) {
                dataForTemplate.setLeadMailFlag("non_serving_cities");
            }

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

    private boolean checkIfServingCity(Enquiry enquiry) {
        City city = cityService.getCityByName(enquiry.getCityName());

        return city.getIsServing();
    }

    private LeadSubmitMail generateDataToMail(Enquiry enquiry) {
        LeadSubmitMail leadMailData = new LeadSubmitMail();
        leadMailData.setEnquiry(enquiry);

        if ((enquiry.getMultipleProjectIds() != null) && !enquiry.getMultipleProjectIds().isEmpty()) {
            List<Project> projects = projectDaoNew.getProjectsOnId(enquiry.getMultipleProjectIds());
            leadMailData.setProjects(projects);
            leadMailData.setLeadMailFlag("leadAcceptanceResale");
            return leadMailData;
        }

        if (enquiry.getBuySell() != null && enquiry.getBuySell().equals("sell")
                && enquiry.getProjectName() != null
                && enquiry.getProjectName() != "") {

            if (enquiry.getProject() == null) {
                Project projectDetails = projectDaoNew.getProjectOnId(enquiry.getProjectId());
                leadMailData.getEnquiry().setProject(projectDetails);
            }
            leadMailData.setLeadMailFlag("google_buy_sell_client");
        }

        else if (enquiry.getProjectName() != null && enquiry.getProjectName() != "") {

            if (enquiry.getProject() == null) {
                Project projectDetails = projectDaoNew.getProjectOnId(enquiry.getProjectId());
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
            if (enquiry.getCity() == null && !enquiry.getCityName().isEmpty()) {
                City city = cityService.getCityByName(enquiry.getCityName());
                leadMailData.getEnquiry().setCity(city);
            }
        }
        return leadMailData;
    }

    public void enrichLeadQuery(Enquiry enquiry) {

        StringBuilder leadQuery = new StringBuilder();
        leadQuery.append(enquiry.getQuery());

        if (enquiry.getBedrooms() != null && enquiry.getBedrooms() != "") {
            leadQuery.append(" : [ Bedrooms = " + enquiry.getBedrooms() + " ]");
        }
        if (enquiry.getBudget() != null && enquiry.getBudget() != "") {
            leadQuery.append(" : [ Budget = " + enquiry.getBudget() + " ]");
        }
        if (enquiry.getPropertyType() != null && enquiry.getPropertyType() != "") {
            leadQuery.append(" : [ PropertyType = " + enquiry.getPropertyType() + " ]");
        }
        if (enquiry.getHomeLoanType() != null && enquiry.getHomeLoanType() != "") {
            leadQuery.append(" : [ Home Loan Purpose = " + enquiry.getHomeLoanType() + " ]");
        }
        if (enquiry.getLocalityId() != null && enquiry.getPageName() != null
                && enquiry.getPageName().equals("RESALE PAGE")) {
            String locName = localityDao.getLocalityOnId(enquiry.getLocalityId()).getLabel();
            leadQuery.append(" : [ Enquiry for resale property in " + locName + " ]");
        }

        enquiry.setQuery(leadQuery.toString());
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
            projectInfo = projectDaoNew.getProjectOnId(enquiry.getProjectId());
            if (projectInfo != null) {
                enquiry.setProjectName(projectInfo.getName());
                enquiry.setLocalityId(projectInfo.getLocalityId());
                enquiry.setCityId(projectInfo.getLocality().getSuburb().getCity().getId());
                enquiry.setCityName(projectInfo.getLocality().getSuburb().getCity().getLabel());
                enquiry.setProject(projectInfo);
            }
        }
        else if (enquiry.getLocalityName() != null && enquiry.getLocalityName() != "" && enquiry.getCityName() != "") {

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
        ActiveUser user = SecurityContextUtils.getActiveUser();
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
        else if (enquiry.getHomeLoanTypeFlag() != null && enquiry.getHomeLoanTypeFlag()) {
            enquiry.setSalesType(SalesType.homeloan);
        }
        else {
            if (project != null) {
                if (Boolean.TRUE.equals(project.isResaleEnquiry())) {
                    enquiry.setSalesType(SalesType.resale);
                }
                else {
                    enquiry.setSalesType(SalesType.primary);
                }
            }
        }
    }

    public Enquiry updateLeadEnquiry(Enquiry enquiry, Long enquiryId, HttpServletRequest request) {

        Enquiry savedEnquiry = enquiryDao.findOne(enquiryId);
        if (enquiry.getMultipleProjectIds() != null && !enquiry.getMultipleProjectIds().isEmpty()) {
            savedEnquiry.setMultipleProjectIds(enquiry.getMultipleProjectIds());
            for (Integer projectId : savedEnquiry.getMultipleProjectIds()) {
                Enquiry enquiryNew = savedEnquiry;
                enquiryNew.setProjectId(projectId);
                enquiryNew.setId(0);
                enquiryNew = generateAndWriteLead(enquiryNew, request);
            }
        }

        if (enquiry.getMultipleTypeIds() != null && !enquiry.getMultipleTypeIds().isEmpty()) {
            savedEnquiry.setMultipleTypeIds(enquiry.getMultipleTypeIds());
            for (Integer typeId : savedEnquiry.getMultipleTypeIds()) {
                EnquiryAttributes enquiryAttributes = new EnquiryAttributes();
                enquiryAttributes.setEnquiryId(savedEnquiry.getId());
                enquiryAttributes.setTypeId(typeId);
                enquiryAttributesDao.saveAndFlush(enquiryAttributes);
            }
            savedEnquiry = enquiryDao.saveAndFlush(savedEnquiry);

            if (savedEnquiry.getProjectId() != 0) {
                savedEnquiry.setProject(projectDaoNew.getProjectOnId(enquiry.getProjectId()));
            }
            beanstalkService.writeToBeanstalk(savedEnquiry);
        }

        return savedEnquiry;
    }

    public List<Enquiry> getEnquiriesForProjectIdInLastMonth(Integer projectId) {
        Date date = new Date();
        date = DateUtils.addMonths(date, -1);
        date = DateUtils.truncate(date, Calendar.MONTH);
        return enquiryDao.findEnquiryByProjectIdAndCreatedDateGreaterThanOrderByCreatedDateDesc(projectId, date);
    }
}
