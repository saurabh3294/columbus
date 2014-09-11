package com.proptiger.data.service.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.enums.Application;
import com.proptiger.data.enums.DomainObject;
import com.proptiger.data.enums.mail.MailTemplateDetail;
import com.proptiger.data.external.dto.CustomUser;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail.CustomCity;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail.CustomLocality;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail.UserAppSubscription;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.internal.dto.ChangePassword;
import com.proptiger.data.internal.dto.Register;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.internal.dto.mail.ResetPasswordTemplateData;
import com.proptiger.data.model.CompanySubscription;
import com.proptiger.data.model.Enquiry;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.ForumUser.WhoAmIDetail;
import com.proptiger.data.model.ForumUserToken;
import com.proptiger.data.model.Locality;
import com.proptiger.data.model.SubscriptionPermission;
import com.proptiger.data.model.SubscriptionSection;
import com.proptiger.data.model.UserPreference;
import com.proptiger.data.model.UserSubscriptionMapping;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.ForumUserTokenDao;
import com.proptiger.data.repo.trend.TrendDao;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.util.DateUtil;
import com.proptiger.data.util.PasswordUtils;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.RegistrationUtils;
import com.proptiger.data.util.SecurityContextUtils;
import com.proptiger.data.util.UtilityClass;
import com.proptiger.exception.BadRequestException;
import com.proptiger.exception.UnauthorizedException;

/**
 * Service class to get if user have already enquired about an entity
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class UserService {
    private static Logger              logger = LoggerFactory.getLogger(UserService.class);

    @Value("${b2b.price-inventory.max.month}")
    private String                     currentMonth;

    @Value("${enquired.within.days}")
    private Integer                    enquiredWithinDays;

    @Value("${proptiger.url}")
    private String                     proptigerUrl;

    @Autowired
    private EnquiryDao                 enquiryDao;

    @Autowired
    private ForumUserDao               forumUserDao;

    @Autowired
    private UserPreferenceService      preferenceService;

    @Autowired
    private LocalityService            localityService;

    @Autowired
    private TrendDao                   trendDao;

    @Autowired
    private AuthenticationManager      authManager;

    @Autowired
    private PropertyReader             propertyReader;

    @Value("${cdn.image.url}")
    private String                     cdnImageBase;

    @Autowired
    private MailSender                 mailSender;

    @Autowired
    private ForumUserTokenDao          forumUserTokenDao;

    @Autowired
    private TemplateToHtmlGenerator    htmlGenerator;

    public boolean isRegistered(String email) {
        if (forumUserDao.findByEmail(email) != null) {
            return true;
        }

        return false;
    }

    /**
     * Returns details forum user object for a user including all his dashboards
     * and preferences and subscription details
     * 
     * @param userInfo
     * @return {@link ForumUser}
     */
    @Transactional
    public CustomUser getUserDetails(int userId) {
        ForumUser user = forumUserDao.findByUserId(userId);
        CustomUser customUser = new CustomUser();
        customUser.setId(user.getUserId());
        customUser.setEmail(user.getEmail());
        customUser.setFirstName(user.getUsername());
        customUser.setContactNumber(Long.toString(user.getContact()));
        customUser.setProfileImageUrl(user.getFbImageUrl());
        customUser.setCreatedDate(user.getCreatedDate());
        Hibernate.initialize(user.getDashboards());
        customUser.setDashboards(user.getDashboards());

        setAppDetails(customUser, user);
        return customUser;
    }

    /**
     * Sets app specific details for user object
     * 
     * @param user
     * @return {@link ForumUser}
     */
    private CustomUser setAppDetails(CustomUser customUser, ForumUser user) {
        HashMap<Application, UserAppDetail> appDetailsMap = new HashMap<>();

        for (UserPreference preference : preferenceService.getUserPreferences(user.getUserId())) {
            UserAppDetail appDetail = new UserAppDetail();
            appDetail.setPreference(preference);
            appDetailsMap.put(preference.getApp(), appDetail);
        }

        List<UserAppSubscription> subscriptions = new ArrayList<>();
        for (UserSubscriptionMapping mapping : user.getUserSubscriptionMappings()) {
            CompanySubscription subscription = mapping.getSubscription();
            customUser.getCompanyIds().add(subscription.getCompanyId());

            if (subscription.getExpiryTime().getTime() < new Date().getTime()) {
                continue;
            }

            UserAppSubscription appSubscription = new UserAppSubscription();
            for (SubscriptionSection section : subscription.getSections()) {
                appSubscription.getSections().add(section.getSection());
            }
            appSubscription.setExpiryDate(subscription.getExpiryTime());

            Hibernate.initialize(subscription.getCompany());
            appSubscription.setCompany(subscription.getCompany());

            setUserAppSubscriptionDetails(subscription.getPermissions(), appSubscription);
            appSubscription.setDataUpdationDate(DateUtil.parseYYYYmmddStringToDate(currentMonth));
            subscriptions.add(appSubscription);
        }

        if (!appDetailsMap.containsKey(Application.B2B)) {
            appDetailsMap.put(Application.B2B, new UserAppDetail());
        }
        appDetailsMap.get(Application.B2B).setSubscriptions(subscriptions);
        customUser.setAppDetails(appDetailsMap);
        return customUser;
    }

    /**
     * 
     * @param subscriptionPermissions
     * @param userAppSubscription
     * @return
     */
    private UserAppSubscription setUserAppSubscriptionDetails(
            List<SubscriptionPermission> subscriptionPermissions,
            UserAppSubscription userAppSubscription) {
        List<Integer> subscribedIds = new ArrayList<>();
        for (SubscriptionPermission subscriptionPermission : subscriptionPermissions) {
            subscribedIds.add(subscriptionPermission.getPermission().getObjectId());
        }

        if (!subscribedIds.isEmpty()) {
            userAppSubscription.setUserType(DomainObject.getFromObjectTypeId(
                    subscriptionPermissions.get(0).getPermission().getObjectTypeId()).toString());

            String json = "{\"filters\":{\"and\":[{\"equal\":{\"" + userAppSubscription.getUserType()
                    + "Id\":["
                    + StringUtils.join(subscribedIds, ',')
                    + "]}}]},\"paging\":{\"start\":0,\"rows\":9999}}";

            List<Locality> localities = localityService.getLocalities(new Gson().fromJson(json, Selector.class))
                    .getResults();

            @SuppressWarnings("unchecked")
            Map<Integer, List<Locality>> cityGroupedLocalities = (Map<Integer, List<Locality>>) UtilityClass
                    .groupFieldsAsPerKeys(localities, Arrays.asList("cityId"));

            for (Integer cityId : cityGroupedLocalities.keySet()) {
                userAppSubscription.setCityCount(userAppSubscription.getCityCount() + 1);
                List<Locality> cityLocalities = cityGroupedLocalities.get(cityId);
                CustomCity city = new CustomCity();
                city.setId(cityId);
                city.setName(cityLocalities.get(0).getSuburb().getCity().getLabel());

                List<CustomLocality> cityCustomLocalities = new ArrayList<>();
                for (Locality locality : cityLocalities) {
                    userAppSubscription.setLocalityCount(userAppSubscription.getLocalityCount() + 1);

                    if (locality.getProjectCount() != null) {
                        userAppSubscription.setProjectCount(userAppSubscription.getProjectCount() + locality
                                .getProjectCount());
                    }

                    CustomLocality cityLocality = new CustomLocality();
                    cityLocality.setId(locality.getLocalityId());
                    cityLocality.setName(locality.getLabel());
                    cityCustomLocalities.add(cityLocality);
                }
                city.setLocalities(cityCustomLocalities);
                userAppSubscription.getCities().add(city);
            }
        }
        return userAppSubscription;
    }

    /**
     * Get if user have already enquired a entity
     * 
     * @param projectId
     * @param userId
     * @return
     */
    public AlreadyEnquiredDetails hasEnquired(Integer projectId, Integer userId) {
        String email = forumUserDao.findEmailByUserId(userId);
        Enquiry enquiry = null;
        AlreadyEnquiredDetails alreadyEnquiredDetails = new AlreadyEnquiredDetails(null, false, enquiredWithinDays);
        if (projectId != null) {
            List<Enquiry> enquiries = enquiryDao.findEnquiryByEmailAndProjectIdOrderByCreatedDateDesc(email, new Long(
                    projectId));
            if (enquiries != null && !enquiries.isEmpty()) {
                enquiry = enquiries.get(0);
            }

            if (enquiry != null) {
                alreadyEnquiredDetails.setLastEnquiryDate(enquiry.getCreatedDate());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -enquiredWithinDays);
                Date date = cal.getTime();
                if (enquiry.getCreatedDate().compareTo(date) >= 0) {
                    alreadyEnquiredDetails.setHasValidEnquiry(true);
                }
            }
        }
        return alreadyEnquiredDetails;
    }

    public static class AlreadyEnquiredDetails {
        // last enquiry date
        private Date    lastEnquiryDate;
        // true if user enquired with last {enquiredWithinDays} number of days
        private boolean hasValidEnquiry = false;
        // number of days within which enquiry is done
        private int     enquiryValidityPeriod;

        public AlreadyEnquiredDetails(Date lastEnquiredOn, boolean enquiredWithinTimeLimit, int enquiredWithinDays) {
            super();
            this.lastEnquiryDate = lastEnquiredOn;
            this.hasValidEnquiry = enquiredWithinTimeLimit;
            this.enquiryValidityPeriod = enquiredWithinDays;
        }

        public Date getLastEnquiryDate() {
            return lastEnquiryDate;
        }

        public void setLastEnquiryDate(Date lastEnquiryDate) {
            this.lastEnquiryDate = lastEnquiryDate;
        }

        public boolean isHasValidEnquiry() {
            return hasValidEnquiry;
        }

        public void setHasValidEnquiry(boolean hasValidEnquiry) {
            this.hasValidEnquiry = hasValidEnquiry;
        }

        public int getEnquiryValidityPeriod() {
            return enquiryValidityPeriod;
        }

        public void setEnquiryValidityPeriod(int enquiryValidityPeriod) {
            this.enquiryValidityPeriod = enquiryValidityPeriod;
        }

    }

    /**
     * Get minimal details needed for active user as whoami. In case user is not
     * logged in then throws UnauthorizedException
     * 
     * @param userIdentifier
     * @return
     */
    public WhoAmIDetail getWhoAmIDetail() {
        ActiveUser activeUser = SecurityContextUtils.getLoggedInUser();
        if (activeUser == null) {
            throw new UnauthorizedException();
        }
        WhoAmIDetail whoAmIDetail = forumUserDao.getWhoAmIDetail(activeUser.getUserIdentifier());
        if (whoAmIDetail.getImageUrl() == null || whoAmIDetail.getImageUrl().isEmpty()) {
            whoAmIDetail.setImageUrl(cdnImageBase + propertyReader.getRequiredProperty(PropertyKeys.AVATAR_IMAGE_URL));
        }
        return whoAmIDetail;
    }

    /**
     * Change password of active user after old and new password validation.
     * Updating principle in SecurityContextHolder after password change.
     * 
     * @param activeUser
     * @param changePassword
     */
    public void changePassword(ActiveUser activeUser, ChangePassword changePassword) {

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(activeUser.getUsername(), changePassword
                    .getOldPassword()));
        }
        catch (AuthenticationException e) {
            throw new BadRequestException(ResponseCodes.BAD_CREDENTIAL, ResponseErrorMessages.BAD_CREDENTIAL);
        }
        PasswordUtils.validateChangePasword(changePassword);
        logger.debug("Changing password for user {}", activeUser.getUsername());
        ForumUser forumUser = forumUserDao.findOne(activeUser.getUserIdentifier());
        forumUser.setPassword(changePassword.getNewPassword());
        forumUser = forumUserDao.save(forumUser);

        SecurityContextUtils.autoLogin(forumUser);
    }

    /**
     * Register a new user after data validation
     * 
     * @param register
     * @return
     */
    @Transactional
    public ForumUser register(Register register) {
        RegistrationUtils.validateRegistration(register);
        ForumUser userPresent = forumUserDao.findByEmail(register.getEmail());
        if (userPresent != null) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.EMAIL_ALREADY_REGISTERED);
        }
        ForumUser savedUser = forumUserDao.save(register.createForumUserObject());
        MailBody mailBody = htmlGenerator.generateMailBody(MailTemplateDetail.NEW_USER_REGISTRATION, register);
        MailDetails details = new MailDetails(mailBody).setMailTo(register.getEmail()).setFrom(
                propertyReader.getRequiredProperty(PropertyKeys.MAIL_FROM_SUPPORT));
        mailSender.sendMailUsingAws(details);
        /*
         * after registration make user auto login
         */
        SecurityContextUtils.autoLogin(savedUser);
        return savedUser;
    }

    /**
     * This method verifies the user by email from database, if registered then
     * send a password recovery mail
     * 
     * @param email
     * @return
     */
    public String resetPassword(String email) {
        ForumUser forumUser = forumUserDao.findByEmailAndProvider(email, "");
        if (forumUser == null) {
            return ResponseErrorMessages.EMAIL_NOT_REGISTERED;
        }
        // token valid for 1 month
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        String token = PasswordUtils.generateTokenBase64Encoded();
        token = PasswordUtils.encode(token);
        String encodedEmail = PasswordUtils.base64Encode(email);
        ForumUserToken forumUserToken = new ForumUserToken();
        forumUserToken.setToken(token);
        forumUserToken.setExpirationDate(calendar.getTime());
        forumUserTokenDao.save(forumUserToken);
        String retrivePasswordLink = proptigerUrl + "/forgotpass.php?token=" + token + "&id=" + encodedEmail;
        ResetPasswordTemplateData resetPassword = new ResetPasswordTemplateData(
                forumUser.getUsername(),
                retrivePasswordLink);
        MailBody mailBody = htmlGenerator.generateMailBody(MailTemplateDetail.RESET_PASSWORD, resetPassword);
        MailDetails details = new MailDetails(mailBody).setMailTo(email);
        mailSender.sendMailUsingAws(details);
        return ResponseErrorMessages.PASSWORD_RECOVERY_MAIL_SENT;
    }

}
