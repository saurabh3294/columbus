package com.proptiger.data.service.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
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
import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.enums.Application;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.enums.MailTemplateDetail;
import com.proptiger.core.enums.ResourceType;
import com.proptiger.core.enums.ResourceTypeAction;
import com.proptiger.core.exception.BadRequestException;
import com.proptiger.core.exception.ResourceNotAvailableException;
import com.proptiger.core.exception.UnauthorizedException;
import com.proptiger.core.internal.dto.mail.MailBody;
import com.proptiger.core.internal.dto.mail.MailDetails;
import com.proptiger.core.model.cms.Company;
import com.proptiger.core.model.cms.Locality;
import com.proptiger.core.model.proptiger.CompanySubscription;
import com.proptiger.core.model.proptiger.Dashboard;
import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.core.model.proptiger.Permission;
import com.proptiger.core.model.proptiger.SubscriptionPermission;
import com.proptiger.core.model.proptiger.SubscriptionSection;
import com.proptiger.core.model.proptiger.UserSubscriptionMapping;
import com.proptiger.core.model.user.User;
import com.proptiger.core.model.user.User.WhoAmIDetail;
import com.proptiger.core.model.user.UserAttribute;
import com.proptiger.core.model.user.UserAuthProviderDetail;
import com.proptiger.core.model.user.UserContactNumber;
import com.proptiger.core.model.user.UserHierarchy;
import com.proptiger.core.model.user.UserPreference;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.Selector;
import com.proptiger.core.service.mail.MailSender;
import com.proptiger.core.service.mail.TemplateToHtmlGenerator;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.DateUtil;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.core.util.SecurityContextUtils;
import com.proptiger.core.util.UtilityClass;
import com.proptiger.data.enums.AuthProvider;
import com.proptiger.data.external.dto.CustomUser;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail.CustomCity;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail.CustomLocality;
import com.proptiger.data.external.dto.CustomUser.UserAppDetail.UserAppSubscription;
import com.proptiger.data.internal.dto.ChangePassword;
import com.proptiger.data.internal.dto.RegisterUser;
import com.proptiger.data.internal.dto.mail.ResetPasswordTemplateData;
import com.proptiger.data.model.ForumUserToken;
import com.proptiger.data.model.ProjectDiscussionSubscription;
import com.proptiger.data.model.companyuser.CompanyUser;
import com.proptiger.data.model.user.UserDetails;
import com.proptiger.data.repo.CompanyDao;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ProjectDiscussionSubscriptionDao;
import com.proptiger.data.repo.SubscriptionPermissionDao;
import com.proptiger.data.repo.UserSubscriptionMappingDao;
import com.proptiger.data.repo.companyuser.CompanyUserDao;
import com.proptiger.data.repo.trend.TrendDao;
import com.proptiger.data.repo.user.UserAttributeDao;
import com.proptiger.data.repo.user.UserAuthProviderDetailDao;
import com.proptiger.data.repo.user.UserContactNumberDao;
import com.proptiger.data.repo.user.UserDao;
import com.proptiger.data.repo.user.UserEmailDao;
import com.proptiger.data.service.B2BAttributeService;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.service.companyuser.CompanyUserService;
import com.proptiger.data.util.PasswordUtils;
import com.proptiger.data.util.RegistrationUtils;

/**
 * Service class to get if user have already enquired about an entity
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class UserService {
    private static final String TYPE = "type";

    public enum UserCommunicationType {
        email, contact
    };

    private static final Object              TOKEN  = "token";

    private static Logger                    logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private B2BAttributeService              b2bAttributeService;

    @Value("${b2b.price-inventory.max.month.dblabel}")
    private String                           currentMonthDbLabel;

    private String                           currentMonth;

    @Value("${enquired.within.days}")
    private Integer                          enquiredWithinDays;

    @Value("${proptiger.url}")
    private String                           proptigerUrl;

    @Autowired
    private EnquiryDao                       enquiryDao;

    @Autowired
    private UserDao                          userDao;

    @Autowired
    private UserEmailDao                     emailDao;

    @Autowired
    private UserContactNumberDao             contactNumberDao;

    @Autowired
    private UserAuthProviderDetailDao        authProviderDetailDao;

    @Autowired
    private ProjectDiscussionSubscriptionDao discussionSubscriptionDao;

    @Autowired
    private UserPreferenceService            preferenceService;

    @Autowired
    private LocalityService                  localityService;

    @Autowired
    private UserSubscriptionMappingDao       userSubscriptionMappingDao;

    @Autowired
    private SubscriptionPermissionDao        subscriptionPermissionDao;

    @Autowired
    private TrendDao                         trendDao;

    @Autowired
    private AuthenticationManager            authManager;

    @Autowired
    private PropertyReader                   propertyReader;

    @Value("${cdn.image.url}")
    private String                           cdnImageBase;

    @Autowired
    private MailSender                       mailSender;

    @Autowired
    private TemplateToHtmlGenerator          htmlGenerator;

    @Autowired
    private DashboardService                 dashboardService;

    @Autowired
    private UserAttributeDao                 userAttributeDao;

    @Autowired
    private CompanyDao companyDao;
    
    @Autowired
    private UserRolesService userRolesService;
    
    @Autowired
    private CompanyUserDao companyUserDao;
    
    @Autowired
    private UserTokenService userTokenService;
    
    @Autowired
    private CompanyUserService companyUserService;
    
    @PostConstruct
    private void initialize() {
        currentMonth = b2bAttributeService.getAttributeByName(currentMonthDbLabel);
    }

    public boolean isRegistered(String email) {
        User user = userDao.findByEmail(email);
        if (user != null && user.isRegistered()) {
            return true;
        }

        return false;
    }

    /**
     * Returns details forum user object for a user including all his dashboards
     * and preferences and subscription details
     * 
     * @param userInfo
     * @return {@link CustomUser}
     */
    @Transactional
    public CustomUser getUserDetails(Integer userId, Application application, boolean needDashboards) {
        User user = userDao.findByUserIdWithContactAndAuthProviderDetails(userId);
        CustomUser customUser = createCustomUserObj(user, application, needDashboards);
        return customUser;
    }

    private CustomUser createCustomUserObj(User user, Application application, boolean needDashboards) {
        CustomUser customUser = new CustomUser();
        customUser.setId(user.getId());
        customUser.setEmail(user.getEmail());
        customUser.setFirstName(user.getFullName());
        customUser.setCountryId(user.getCountryId());
        UserContactNumber priorityContact = getTopPriorityContact(user.getId());
        if(priorityContact != null){
            customUser.setContactNumber(priorityContact.getContactNumber());
        }
        else{
            customUser.setContactNumber("");
        }
        String profileImage = getUserProfileImageUrl(user.getId());
        customUser.setProfileImageUrl(profileImage);
        if (needDashboards) {
            List<Dashboard> dashboards = dashboardService.getAllByUserIdAndType(user.getId(), new FIQLSelector());
            customUser.setDashboards(dashboards);
        }

        if (application.equals(Application.B2B)) {
            updateAppDetails(customUser, user);
        }
        List<String> roles = userRolesService.getUserRolesName(user.getId());
        if(!roles.isEmpty()){
            customUser.setRoles(roles);
        }
        return customUser;
    }

    /**
     * Get first non null non empty image url else null
     * @param userId
     * @return
     */
    private String getUserProfileImageUrl(int userId) {
        List<UserAuthProviderDetail> authProviderDetails = authProviderDetailDao.findByUserId(userId);
        if(!authProviderDetails.isEmpty()){
            for(UserAuthProviderDetail detail: authProviderDetails){
                if (detail.getImageUrl() != null && !detail.getImageUrl().isEmpty()) {
                    return detail.getImageUrl();
                }
            }
        }
        //return default avatar image url
        return (cdnImageBase + propertyReader.getRequiredProperty(PropertyKeys.AVATAR_IMAGE_URL));
    }

    @Transactional
    public CustomUser getUserDetailsByEmail(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new BadRequestException(ResponseCodes.RESOURCE_NOT_FOUND, ResponseErrorMessages.User.EMAIL_NOT_REGISTERED);
        }
        CustomUser customUser = createCustomUserObj(user, Application.DEFAULT, false);
        return customUser;
    }

    /**
     * Sets app specific details for user object
     * 
     * @param user
     * @return {@link CustomUser}
     */
    private void updateAppDetails(CustomUser customUser, User user) {
        HashMap<Application, UserAppDetail> appDetailsMap = new HashMap<>();

        for (UserPreference preference : preferenceService.getUserPreferences(user.getId())) {
            UserAppDetail appDetail = new UserAppDetail();
            appDetail.setPreference(preference);
            appDetailsMap.put(preference.getApp(), appDetail);
        }
        List<UserSubscriptionMapping> userSubscriptions = userSubscriptionMappingDao.findAllByUserId(user.getId());
        List<UserAppSubscription> subscriptions = new ArrayList<>();
        for (UserSubscriptionMapping mapping : userSubscriptions) {
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

            Company c = companyDao.findOne(subscription.getCompanyId());
            appSubscription.setCompany(c);

            setUserAppSubscriptionDetails(subscription.getPermissions(), appSubscription);
            appSubscription.setDataUpdationDate(DateUtil.parseYYYYmmddStringToDate(currentMonth));
            subscriptions.add(appSubscription);
        }

        if (!appDetailsMap.containsKey(Application.B2B)) {
            appDetailsMap.put(Application.B2B, new UserAppDetail());
        }
        appDetailsMap.get(Application.B2B).setSubscriptions(subscriptions);
        customUser.setAppDetails(appDetailsMap);
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
        List<Integer> subscribedIdsCity = new ArrayList<>();
        List<Integer> subscribedIdsLocality = new ArrayList<>();
        Permission permission = null;
        int objectTypeId = 0;
        for (SubscriptionPermission subscriptionPermission : subscriptionPermissions) {
            permission = subscriptionPermission.getPermission();
            objectTypeId = permission.getObjectTypeId();
            if (objectTypeId == DomainObject.city.getObjectTypeId()) {
                subscribedIdsCity.add(permission.getObjectId());
            }
            else if (objectTypeId == DomainObject.locality.getObjectTypeId()) {
                subscribedIdsLocality.add(permission.getObjectId());
            }
        }

        /* Populating UserType */

        if (subscribedIdsCity.isEmpty() && subscribedIdsLocality.isEmpty()) {
            return userAppSubscription;
        }
        else if (subscribedIdsCity.isEmpty()) {
            userAppSubscription.setUserType(DomainObject.locality.toString());
        }
        else {
            userAppSubscription.setUserType(DomainObject.city.toString());
        }

        /* Populating Locality List */

        List<Locality> localityList = getLocalityListByCityIdList(subscribedIdsCity);

        if (!subscribedIdsLocality.isEmpty()) {
            localityList.addAll(localityService.findByLocalityIdList(subscribedIdsLocality).getResults());
        }

        if (!localityList.isEmpty()) {

            @SuppressWarnings("unchecked")
            Map<Integer, List<Locality>> cityGroupedLocalities = (Map<Integer, List<Locality>>) UtilityClass
                    .groupFieldsAsPerKeys(localityList, Arrays.asList("cityId"));

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

    private List<Locality> getLocalityListByCityIdList(List<Integer> subscribedIdsCity) {

        if (subscribedIdsCity.isEmpty()) {
            return new ArrayList<Locality>();
        }

        String json = "{\"filters\":{\"and\":[{\"equal\":{\"" + "cityId\":["
                + StringUtils.join(subscribedIdsCity, ',')
                + "]}}]},\"paging\":{\"start\":0,\"rows\":9999}}";

        return (localityService.getLocalities(new Gson().fromJson(json, Selector.class)).getResults());
    }

    /**
     * Get if user have already enquired a entity
     * 
     * @param projectId
     * @param userId
     * @return
     */
    public AlreadyEnquiredDetails hasEnquired(Integer projectId, Integer userId) {
        String email = userDao.findByUserIdWithContactAndAuthProviderDetails(userId).getEmail();
        Enquiry enquiry = null;
        AlreadyEnquiredDetails alreadyEnquiredDetails = new AlreadyEnquiredDetails(null, false, enquiredWithinDays);
        if (projectId != null) {
            List<Enquiry> enquiries = enquiryDao.findEnquiryByEmailAndProjectIdOrderByCreatedDateDesc(email, projectId);
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
        ActiveUser activeUser = SecurityContextUtils.getActiveUser();
        if (activeUser == null) {
            throw new UnauthorizedException();
        }
        User user = userDao.findByUserIdWithContactAndAuthProviderDetails(activeUser.getUserIdentifier());
        WhoAmIDetail whoAmIDetail = new WhoAmIDetail(user.getId(), user.getFullName(), getUserProfileImageUrl(user.getId()));;
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
            throw new BadRequestException(ResponseCodes.BAD_CREDENTIAL, ResponseErrorMessages.User.BAD_CREDENTIAL);
        }
        PasswordUtils.validateChangePasword(changePassword);
        logger.debug("Changing password for user {}", activeUser.getUsername());
        User user = userDao.findByIdWithRoles(activeUser.getUserIdentifier());
        user.setPassword(changePassword.getNewPassword());
        userDao.save(user);
        SecurityContextUtils.autoLogin(user);
    }

    /**
     * Register a new user after data validation
     * 
     * @param register
     * @return
     */
    @Transactional
    public Integer register(RegisterUser register, Application applicationType) {
        register.setUserAuthProviderDetails(null);
        RegistrationUtils.validateRegistration(register);
        register.setRegistered(true);
        User user = userDao.findByEmail(register.getEmail());
        User toCreate = register.createUserObj();
        if (user == null) {
            user = userDao.saveAndFlush(toCreate);
        }
        else if (user.isRegistered()) {
            throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.User.EMAIL_ALREADY_REGISTERED);
        }
        else if (!user.isRegistered()) {
            user.setRegistered(true);
            user = userDao.saveAndFlush(user);
        }
        toCreate.setId(user.getId());
        createDefaultProjectDiscussionSubscriptionForUser(user.getId());
        patchUser(toCreate);
        /*
         * send mail only if user registers
         */
        if (user.isRegistered()) {
            /*
             * mails should be sent for non b2b users only
             */
            if(!applicationType.equals(Application.B2B)){
                /*
                
                Removing this as we are not sending validation link in mailer
                TODO should be enabled once we figure out proper mailers from product team
                 
                ForumUserToken userToken = createForumUserToken(user.getId());
                MailBody mailBody = htmlGenerator
                        .generateMailBody(
                                MailTemplateDetail.NEW_USER_REGISTRATION,
                                new UserRegisterMailTemplate(
                                        user.getFullName(),
                                        user.getEmail(),
                                        getEmailValidationLink(userToken)));
                */
                MailBody mailBody = htmlGenerator
                        .generateMailBody(
                                MailTemplateDetail.NEW_USER_REGISTRATION,
                                new UserRegisterMailTemplate(
                                        user.getFullName(),
                                        user.getEmail(),
                                        ""));
                MailDetails details = new MailDetails(mailBody).setMailTo(user.getEmail()).setFrom(
                        propertyReader.getRequiredProperty(PropertyKeys.MAIL_FROM_SUPPORT));
                mailSender.sendMailUsingAws(details);
            }
            SecurityContextUtils.autoLogin(user);
        }
        return user.getId();
    }

    private String getEmailValidationLink(ForumUserToken userToken) {
        StringBuilder emailValidationLink = new StringBuilder(proptigerUrl)
                .append(Constants.Security.USER_VALIDATE_API).append("?").append(TOKEN).append("=")
                .append(userToken.getToken()).append("&").append(TYPE).append("=")
                .append(UserCommunicationType.email.name());
        return emailValidationLink.toString();
    }

    /**
     * Process reset password request and send an eamil containing the instructions to reset password
     * @param email
     * @return
     */
    public String processResetPasswordRequest(String email){
        User user = userDao.findByEmail(email);
        if (user == null || !user.isRegistered()) {
            throw new BadRequestException(ResponseErrorMessages.User.EMAIL_NOT_REGISTERED);
        }
        ForumUserToken forumUserToken = userTokenService.createTokenForUser(user.getId());
        StringBuilder retrivePasswordLink = new StringBuilder(proptigerUrl).append(
                PropertyReader.getRequiredPropertyAsString(PropertyKeys.PROPTIGER_RESET_PASSWORD_PAGE)).append(
                "?token=" + forumUserToken.getToken());
        ResetPasswordTemplateData resetPassword = new ResetPasswordTemplateData(
                user.getFullName(),
                retrivePasswordLink.toString());
        MailBody mailBody = htmlGenerator.generateMailBody(MailTemplateDetail.RESET_PASSWORD, resetPassword);
        MailDetails details = new MailDetails(mailBody).setMailTo(email);
        mailSender.sendMailUsingAws(details);
        return ResponseErrorMessages.User.PASSWORD_RECOVERY_MAIL_SENT;
    
    }
    
    /**
     * This method verifies the user by email from database, if registered then
     * send a password recovery mail
     * 
     * @param changePassword 
     * @param token 
     * @return
     */
    public Object resetPasswordUsingToken(String token, ChangePassword changePassword) {
        ForumUserToken forumUserToken = userTokenService.getTokenDetailsAfterValidation(token);
        String encodedPass = PasswordUtils.validateNewAndConfirmPassword(
                changePassword.getNewPassword(),
                changePassword.getConfirmNewPassword());
        User user = userDao.findOne(forumUserToken.getUserId());
        user.setPassword(encodedPass);
        userDao.save(user);
        user = userDao.findByIdWithRoles(forumUserToken.getUserId());
        SecurityContextUtils.autoLogin(user);
        userTokenService.deleteTokenAsync(forumUserToken);
        return getUserDetails(user.getId(), Application.DEFAULT, false);
    }

    /**
     * 
     * creates social auth details... creates login details if not already
     * there... updates it otherwise
     * 
     * @param userProfile
     * @param provider
     * @param providerUserId
     * @param imageUrl
     * @return {@link User}
     */
    @Transactional
    public User createSocialAuthDetails(
            String email,
            String userName,
            AuthProvider provider,
            String providerUserId,
            String imageUrl) {

        User user = userDao.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setRegistered(true);
            user.setFullName(userName);
            user = userDao.save(user);
        }

        int userId = user.getId();
        createDefaultProjectDiscussionSubscriptionForUser(userId);

        UserAuthProviderDetail authProviderDetail = authProviderDetailDao.findByUserIdAndProviderId(
                userId,
                provider.getProviderId());
        if (authProviderDetail == null) {
            authProviderDetail = new UserAuthProviderDetail();
            authProviderDetail.setProviderId(provider.getProviderId());
            authProviderDetail.setProviderUserId(providerUserId);
            authProviderDetail.setImageUrl(imageUrl);
            authProviderDetail.setUserId(userId);
            authProviderDetail = authProviderDetailDao.save(authProviderDetail);
        }
        else {
            authProviderDetail.setProviderUserId(providerUserId);
            authProviderDetail.setImageUrl(imageUrl);
            authProviderDetailDao.save(authProviderDetail);
        }
        return user;
    }

    private ProjectDiscussionSubscription createDefaultProjectDiscussionSubscriptionForUser(int userId) {
        ProjectDiscussionSubscription discussionSubscription = discussionSubscriptionDao.findOne(userId);
        if (discussionSubscription == null) {
            discussionSubscription = new ProjectDiscussionSubscription();
            discussionSubscription.setUserId(userId);
        }
        discussionSubscription.setSubscribed(true);
        return discussionSubscriptionDao.save(discussionSubscription);
    }

    public User createUser(User user) {
        String email = user.getEmail();
        User userInDB = userDao.findByEmail(email);

        if (userInDB != null) {
            user.setId(userInDB.getId());
            String fullName = user.getFullName();
            if (fullName != null && !fullName.isEmpty()) {
                userInDB.setFullName(fullName);
                userDao.save(userInDB);
            }
        }
        else {
            user.setId(userDao.save(user).getId());
        }

        patchUser(user);
        return user;
    }
    
    public User findOrCreateUser(User user) {
        if (user.getId() != 0) {
            return userDao.findOne(user.getId());
        } else {
            return createUser(user);
        }
    }

    /**
     * 
     * @param user
     * @param clientId
     * @param priority
     *            merges email and phone number in user_emails table and
     *            user_contact_numbers also set clientid and priority
     * @return
     */
    private void patchUser(User user) {

        // checking and creating user attributes.
        createUserAttributes(user);
        updateContactNumbers(user);
    }

    public void updateContactNumbers(User user) {
        Set<UserContactNumber> contactNumbers = user.getContactNumbers();

        if (contactNumbers == null || contactNumbers.isEmpty()) {
            return;
        }
        Iterator<UserContactNumber> it = contactNumbers.iterator();

        UserContactNumber userContactNumber = it.next();
        String contactNumber = userContactNumber.getContactNumber();

        if (contactNumber != null && !contactNumber.isEmpty()) {
            int userId = user.getId();
            userContactNumber.setUserId(userId);
            userContactNumber.setCreatedBy(userId);

            User userByPhone = userDao.findByPhone(contactNumber, userId);
            if (userByPhone == null) {
                contactNumberDao.incrementPriorityForUser(userId);
                userContactNumber.setPriority(UserContactNumber.primaryContactPriority);
                contactNumberDao.saveAndFlush(userContactNumber);
            }
            else {
                user.setId(userContactNumber.getUserId());
            }
        }
    }

    /**
     * This method will take the user attributes from the user object. It will
     * check whether these attributes exists in the database for a user. If it
     * does not exists then it will create them else update them with new value.
     * 
     * @param user
     */
    private void createUserAttributes(User user) {
        List<UserAttribute> attributeList = user.getAttributes();
        if (attributeList == null || attributeList.isEmpty()) {
            return;
        }

        int userId = user.getId();
        ListIterator<UserAttribute> it = attributeList.listIterator();

        while (it.hasNext()) {
            UserAttribute userAttribute = it.next();
            String attributeName = userAttribute.getAttributeName();
            String attributeValue = userAttribute.getAttributeValue();

            /*
             * If attribute value or name is invalid then it will remove those
             * attributes.
             */
            if (attributeName == null || attributeValue == null || attributeName.isEmpty() || attributeValue.isEmpty()) {
                it.remove();
                continue;
            }

            UserAttribute savedAttribute = userAttributeDao.findByUserIdAndAttributeName(userId, attributeName);
            /**
             * 1: attribute Name does not exists 2: attribute Name exists but
             * its value has been changed.
             */
            if (savedAttribute == null || !savedAttribute.getAttributeValue().equals(userAttribute.getAttributeValue())) {
                userAttribute.setUserId(userId);
                // Attribute value has been changed. Hence setting the primary
                // key to make a update.
                if (savedAttribute != null) {
                    userAttribute.setId(savedAttribute.getId());
                }
                /*
                 * It will fire the insert query or update query.
                 */
                savedAttribute = userAttributeDao.saveAndFlush(userAttribute);

            }
            // replacing the current object with the database object.
            it.remove();
            it.add(savedAttribute);

        }

        user.setAttributes(attributeList);
    }

    /**
     * @param email
     * @return
     */
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public User getUserByEmailWithRoles(String email){
        return userDao.findByEmailWithRoles(email);
    }
    
    public User getUserByIdWithRoles(int userId){
        return userDao.findByIdWithRoles(userId);
    }
    
    public Map<Integer, Set<UserContactNumber>> getUserContactNumbers(Set<Integer> clientIds) {
        List<UserContactNumber> userContactNumbers = contactNumberDao.getContactNumbersByUserId(clientIds);
        Map<Integer, Set<UserContactNumber>> contactNumbersOfUser = new HashMap<>();

        for (UserContactNumber userContactNumber : userContactNumbers) {
            if (!contactNumbersOfUser.containsValue(userContactNumber.getUserId())) {
                contactNumbersOfUser.put(userContactNumber.getUserId(), new HashSet<UserContactNumber>());
            }
            contactNumbersOfUser.get(userContactNumber.getUserId()).add(userContactNumber);
        }
        return contactNumbersOfUser;
    }

    public User getUserWithContactNumberById(int Id) {
        return userDao.findByUserIdWithContactAndAuthProviderDetails(Id);
    }

    public User getUserById(int userId) {
        return userDao.findOne(userId);
    }

    public Map<Integer, User> getUsers(Set<Integer> userIds) {
        Map<Integer, User> usersMap = new HashMap<>();
        if (userIds != null && !userIds.isEmpty()) {
            List<User> users = userDao.findByIdIn(userIds);
            for (User u : users) {
                usersMap.put(u.getId(), u);
            }
        }
        return usersMap;
    }

    public UserContactNumber getTopPriorityContact(int userId) {
        List<UserContactNumber> contacts = contactNumberDao.findByUserIdOrderByPriorityAsc(userId);
        if (contacts.isEmpty()) {
            return null;
        }
        return contacts.get(0);
    }

    /**
     * This method will return the attributes of the user based on the attribute
     * value provided.
     * 
     * @param userId
     * @param attributeValue
     * @return
     */
    public UserAttribute checkUserAttributesByAttributeValue(int userId, String attributeValue) {
        return userAttributeDao.findByUserIdAndAttributeValue(userId, attributeValue);
    }

    public void enrichUserDetails(User user) {
        user.setContactNumbers(new HashSet<UserContactNumber>(contactNumberDao.findByUserIdOrderByPriorityAsc(user
                .getId())));
        user.setAttributes(userAttributeDao.findByUserId(user.getId()));
    }

    /**
     * Mark user communication type as verified for valid token and email
     * 
     * @param type
     * @param token
     */
    public void validateUserCommunicationDetails(UserCommunicationType type, String token) {
        ForumUserToken userToken = userTokenService.getTokenDetailsAfterValidation(token);
        User user = userDao.findByUserIdWithContactAndAuthProviderDetails(userToken.getUserId());
        switch (type) {
            case email:
                user.setVerified(true);
                break;
            case contact:
                // do nothing
                break;
            default:
                // do nothing
                break;
        }
        // set verified as true
        userDao.save(user);
        // delete the token
        userTokenService.deleteTokenAsync(userToken);
    }

    public static class UserRegisterMailTemplate {
        private String fullName;
        private String email;
        private String emailValidationLink;

        public UserRegisterMailTemplate(String userName, String email, String validationLink) {
            this.fullName = userName;
            this.email = email;
            this.emailValidationLink = validationLink;
        }

        public String getFullName() {
            return fullName;
            
        }

        public String getEmail() {
            return email;
        }

        public String getEmailValidationLink() {
            return emailValidationLink;
        }
    }

    /**
     * Updates user detail
     * @param user
     * @param activeUser
     * @return
     */
    @Transactional
    public User updateUserDetails(UserDetails user, ActiveUser activeUser) {
        boolean isAdmin = SecurityContextUtils.isAdmin(activeUser);
        if(isAdmin){
            //admin is trying to update details of a user whose user id must be defined in UserDetails object
            if(user.getId() < 0){
                throw new BadRequestException("Invalid user id specified");
            }
            else if(user.getId() == 0){
                //user trying to update his data
                user.setId(activeUser.getUserIdentifier());
            }
        }
        else{
            //normal user is trying to update his details
            user.setId(activeUser.getUserIdentifier()); 
        }
        
        User originalUser = getUserById(user.getId());
        if(originalUser == null){
            throw new ResourceNotAvailableException(ResourceType.USER, ResourceTypeAction.UPDATE);
        }
        
        if (user.getFullName() != null) {
            originalUser.setFullName(user.getFullName());
        }
        if (user.getOldPassword() != null && user.getPassword() != null && user.getConfirmPassword() != null) {
            ChangePassword changePassword = new ChangePassword();
            changePassword.setOldPassword(user.getOldPassword());
            changePassword.setNewPassword(user.getPassword());
            changePassword.setConfirmNewPassword(user.getConfirmPassword());

            changePassword(activeUser, changePassword);

            originalUser.setPassword(changePassword.getNewPassword());
        }
        if (user.getCountryId() != null) {
            originalUser.setCountryId(user.getCountryId());
        }
        if (user.getContactNumbers() != null && !user.getContactNumbers().isEmpty()) {
            updateContactNumbers(user);
        }
        originalUser = userDao.saveAndFlush(originalUser);
        if(isAdmin){
            companyUserService.updateParentDetail(user);
            userRolesService.updateRolesOfUser(user.getRoles(), originalUser.getId(), activeUser.getUserIdentifier());
        }
        return originalUser;
    }


    /**
     * Get all child of active user and create create a tree data structure
     * @param activeUser
     * @return
     */
    @Transactional
    public UserHierarchy getChildHeirarchy(ActiveUser activeUser) {
        UserHierarchy root = new UserHierarchy();
        root.setUserId(activeUser.getUserIdentifier());
        root.setUserName(activeUser.getFullName());
        CompanyUser companyUser = companyUserDao.findByUserId(activeUser.getUserIdentifier());
        if(companyUser == null){
            return root;
        }
        List<CompanyUser> companyUsers = companyUserDao.getCompanyUsersInLeftRightRangeInCompany(
                companyUser.getLeft(),
                companyUser.getRight(),
                companyUser.getCompanyId());
        root.setId(companyUser.getId());

        Set<Integer> userIds = new HashSet<Integer>();
        for(CompanyUser u: companyUsers){
            userIds.add(u.getUserId());
        }
        Map<Integer, User> userDetailsMap = getUsers(userIds);
        
        Map<Integer, List<UserHierarchy>> parentIdToUserMap = new HashMap<Integer, List<UserHierarchy>>();
        for(CompanyUser u: companyUsers){
            if(parentIdToUserMap.get(u.getParentId()) == null){
                parentIdToUserMap.put(u.getParentId(), new ArrayList<UserHierarchy>());
            }
            UserHierarchy hierarchy = new UserHierarchy();
            hierarchy.setId(u.getParentId());
            hierarchy.setUserId(u.getUserId());
            //user id should be found in users database, in any scenario if not found set user name as NA.
            hierarchy.setUserName(userDetailsMap.get(u.getUserId()) != null ? userDetailsMap.get(u.getUserId()).getFullName() :"NA");
            parentIdToUserMap.get(u.getParentId()).add(hierarchy);
        }
        updateChild(root, parentIdToUserMap);
        return root;
    }

    /**
     * Recursive call to update list of children in root and so on using
     * inverted map of data of parent id to list of children
     * 
     * @param root
     * @param parentIdToUserMap
     */
    private void updateChild(UserHierarchy root, Map<Integer, List<UserHierarchy>> parentIdToUserMap) {
        if(root == null || parentIdToUserMap.get(root.getUserId()) == null){
            return;
        }
        root.setChild(parentIdToUserMap.get(root.getUserId()));
        for(UserHierarchy u: root.getChild()){
            updateChild(u, parentIdToUserMap);
        }
    }
    
}
