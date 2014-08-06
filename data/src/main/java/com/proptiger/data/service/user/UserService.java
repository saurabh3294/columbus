package com.proptiger.data.service.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.proptiger.data.constants.ResponseCodes;
import com.proptiger.data.constants.ResponseErrorMessages;
import com.proptiger.data.enums.Application;
import com.proptiger.data.enums.AuthProvider;
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
import com.proptiger.data.model.Permission;
import com.proptiger.data.model.ProjectDiscussionSubscription;
import com.proptiger.data.model.SubscriptionPermission;
import com.proptiger.data.model.SubscriptionSection;
import com.proptiger.data.model.UserPreference;
import com.proptiger.data.model.UserSubscriptionMapping;
import com.proptiger.data.model.trend.InventoryPriceTrend;
import com.proptiger.data.model.user.User;
import com.proptiger.data.model.user.UserAuthProviderDetail;
import com.proptiger.data.model.user.UserContactNumber;
import com.proptiger.data.model.user.UserEmail;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.ForumUserTokenDao;
import com.proptiger.data.repo.ProjectDiscussionSubscriptionDao;
import com.proptiger.data.repo.SubscriptionPermissionDao;
import com.proptiger.data.repo.UserSubscriptionMappingDao;
import com.proptiger.data.repo.trend.TrendDao;
import com.proptiger.data.repo.user.UserAuthProviderDetailDao;
import com.proptiger.data.repo.user.UserContactNumberDao;
import com.proptiger.data.repo.user.UserDao;
import com.proptiger.data.repo.user.UserEmailDao;
import com.proptiger.data.service.LocalityService;
import com.proptiger.data.service.mail.MailSender;
import com.proptiger.data.service.mail.TemplateToHtmlGenerator;
import com.proptiger.data.util.Constants;
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
    private static Logger                    logger = LoggerFactory.getLogger(UserService.class);

    @Value("${b2b.price-inventory.max.month}")
    private String                           currentMonth;

    @Value("${enquired.within.days}")
    private Integer                          enquiredWithinDays;

    @Value("${proptiger.url}")
    private String                           proptigerUrl;

    @Autowired
    private EnquiryDao                       enquiryDao;

    @Autowired
    private ForumUserDao                     forumUserDao;

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
    private ForumUserTokenDao                forumUserTokenDao;

    @Autowired
    private TemplateToHtmlGenerator          htmlGenerator;

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

    public FIQLSelector getUserAppSubscriptionFilters(int userId) {

        FIQLSelector selector = new FIQLSelector();

        List<SubscriptionPermission> subscriptionPermissions = getUserAppSubscriptionDetails(userId);

        for (SubscriptionPermission subscriptionPermission : subscriptionPermissions) {

            Permission permission = subscriptionPermission.getPermission();
            int objectTypeId = permission.getObjectTypeId();

            switch (DomainObject.getFromObjectTypeId(objectTypeId)) {

                case city:
                    selector.addOrConditionToFilter("cityId==" + permission.getObjectId());
                    break;

                case locality:
                    selector.addOrConditionToFilter("localityId==" + permission.getObjectId());
                    break;

                default:
                    break;
            }
        }
        return selector;
    }

    /**
     * @param userId
     *            userId for which subscription permissions are needed.
     * @return List of subscriptionPermissions or an empty-list if there are no
     *         permissions installed.
     */
    @Cacheable(value = Constants.CacheName.CACHE)
    private List<SubscriptionPermission> getUserAppSubscriptionDetails(int userId) {
        List<UserSubscriptionMapping> userSubscriptionMappingList = userSubscriptionMappingDao.findAllByUserId(userId);
        if (userSubscriptionMappingList == null) {
            return (new ArrayList<SubscriptionPermission>());
        }

        List<Integer> subscriptionIdList = new ArrayList<Integer>();
        for (UserSubscriptionMapping usm : userSubscriptionMappingList) {
            if (usm.getSubscription().getExpiryTime().getTime() > new Date().getTime()) {
                subscriptionIdList.add(usm.getSubscriptionId());
            }
        }

        if (subscriptionIdList.isEmpty()) {
            return (new ArrayList<SubscriptionPermission>());
        }

        List<SubscriptionPermission> subscriptionPermissions = subscriptionPermissionDao
                .findBySubscriptionIdIn(subscriptionIdList);
        if (subscriptionPermissions == null) {
            return (new ArrayList<SubscriptionPermission>());
        }

        return subscriptionPermissions;
    }

    /*
     * Returns a MultiKeyMap with 2 keys. [K1 = ObjectTypeId, K2 = ObjectId],
     * [Value = Permission Object]
     * 
     * [1]. In case of locality, checking existence of the key should be enough.
     * [2]. For city, a key will exist (with a value null), even if the user has
     * a permission for some locality in the city. To check for full city
     * permission, check if the value mapped to that key is not null.
     */

    public MultiKeyMap getUserSubscriptionMap(int userId) {
        List<SubscriptionPermission> subscriptionPermissions = getUserAppSubscriptionDetails(userId);
        MultiKeyMap userSubscriptionMap = new MultiKeyMap();
        Permission permission;
        int objectTypeId, objectId;
        List<Integer> localityIDList = new ArrayList<Integer>();

        int objTypeIdLocality = DomainObject.locality.getObjectTypeId();
        int objTypeIdCity = DomainObject.city.getObjectTypeId();
        for (SubscriptionPermission sp : subscriptionPermissions) {
            permission = sp.getPermission();

            if (permission != null) {
                objectTypeId = permission.getObjectTypeId();
                objectId = permission.getObjectId();
                userSubscriptionMap.put(objectTypeId, objectId, permission);
                if (objectTypeId == objTypeIdLocality) {
                    localityIDList.add(objectId);
                }
            }
        }

        /*
         * populating psuedo permissions for city if any locality in that city
         * is permitted
         */
        Set<Integer> cityIdList = getCityIdListFromLocalityIdList(localityIDList);
        for (int cityId : cityIdList) {
            userSubscriptionMap.put(objTypeIdCity, cityId, null);
        }

        List<Integer> subscribedBuilders = getSubscribedBuilderList(userId);
        for (Integer builderId : subscribedBuilders) {
            userSubscriptionMap.put(DomainObject.builder.getObjectTypeId(), builderId, builderId);
        }
        return userSubscriptionMap;
    }

    @Async
    public void preloadUserSubscriptionMap(int userId) {
        getUserSubscriptionMap(userId);
    }

    @Cacheable(value = Constants.CacheName.CACHE)
    private List<Integer> getSubscribedBuilderList(int userId) {
        FIQLSelector selector = getUserAppSubscriptionFilters(userId);
        List<Integer> builderList = new ArrayList<>();
        if (selector.getFilters() != null) {
            String builderId = "builderId";
            selector.setFields(builderId);
            selector.setGroup(builderId);

            List<InventoryPriceTrend> list = trendDao.getTrend(selector);
            for (InventoryPriceTrend inventoryPriceTrend : list) {
                builderList.add(inventoryPriceTrend.getBuilderId());
            }
        }
        return builderList;
    }

    private Set<Integer> getCityIdListFromLocalityIdList(List<Integer> localityIDList) {
        Set<Integer> cityIdList = new HashSet<Integer>();
        List<Locality> localiltyList = localityService.findByLocalityIdList(localityIDList).getResults();
        for (Locality locality : localiltyList) {
            cityIdList.add(locality.getSuburb().getCityId());
        }
        return cityIdList;
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
        User user = userDao.findOne(activeUser.getUserIdentifier());
        user.setPassword(changePassword.getNewPassword());
        userDao.save(user);
        SecurityContextUtils.autoLogin(forumUserDao.findByUserId(user.getId()));
    }

    /**
     * Register a new user after data validation
     * 
     * @param register
     * @return
     */
    @Transactional
    public CustomUser register(Register register) {
        RegistrationUtils.validateRegistration(register);
        User user = getUserFromRegister(register);

        user = userDao.save(user);

        manageEmailOnRegistration(user, register);
        manageContactNumberOnRegistration(user, register);

        ForumUser registeredUser = forumUserDao.findByUserId(user.getId());
        MailBody mailBody = htmlGenerator.generateMailBody(MailTemplateDetail.NEW_USER_REGISTRATION, register);
        MailDetails details = new MailDetails(mailBody).setMailTo(register.getEmail()).setFrom(
                propertyReader.getRequiredProperty(PropertyKeys.MAIL_FROM_SUPPORT));
        mailSender.sendMailUsingAws(details);
        /*
         * after registration make user auto login
         */
        SecurityContextUtils.autoLogin(registeredUser);

        return getUserDetails(user.getId());
    }

    private User getUserFromRegister(Register register) {
        User user = userDao.findByPrimaryEmail(register.getEmail());
        if (user == null) {
            user = createFreshUserFromRegister(register);
        }
        else {
            if (user.isRegistered()) {
                throw new BadRequestException(ResponseCodes.BAD_REQUEST, ResponseErrorMessages.EMAIL_ALREADY_REGISTERED);
            }
            else {
                user.copyFieldsFromRegisterToUser(register);
            }
        }
        return user;
    }

    private User createFreshUserFromRegister(Register register) {
        User user = new User();
        user.copyFieldsFromRegisterToUser(register);
        return user;
    }

   

    // manages emails for every registration
    // will be more relevant once we start supporting multiple emails
    private void manageEmailOnRegistration(User user, Register register) {
        if (user.getEmails() == null) {
            String email = register.getEmail();
            UserEmail userEmail = new UserEmail(email, user.getId());
            emailDao.save(userEmail);
        }
    }

    // manages contact numbers for every registration
    // will be more relevant once we start supporting multiple contacts
    private void manageContactNumberOnRegistration(User user, Register register) {
        if (user.getContactNumbers() == null) {
            String contactNumber = register.getContact().toString();
            contactNumberDao.save(new UserContactNumber(contactNumber, user.getId()));
        }
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

    @Transactional
    public User createSocialAuthDetails(
            UserProfile userProfile,
            AuthProvider provider,
            String providerUserId,
            String imageUrl) {
        User user;
        UserAuthProviderDetail authProviderDetail = authProviderDetailDao.findByProviderIdAndProviderUserId(
                provider.getProviderId(),
                providerUserId);
        if (authProviderDetail != null) {
            return userDao.findOne(authProviderDetail.getUserId());
        }
        else {
            authProviderDetail = new UserAuthProviderDetail();
            authProviderDetail.setProviderId(provider.getProviderId());
            authProviderDetail.setProviderUserId(providerUserId);
            authProviderDetail.setImageUrl(imageUrl);

            String email = userProfile.getEmail();
            user = userDao.findByEmail(email);

            if (user == null) {
                user = new User();
                user.setFullName(userProfile.getName());
                user = userDao.save(user);

                int userId = user.getId();
                //TODO uncomment once we create table 
                //createDefaultProjectDiscussionSubscriptionForUser(userId);

                UserEmail userEmail = new UserEmail();
                userEmail.setUserId(userId);
                userEmail.setEmail(email);
                userEmail.setCreatedBy(userId);
                emailDao.save(userEmail);
            }
            authProviderDetail.setUserId(user.getId());
            authProviderDetailDao.save(authProviderDetail);
        }
        return userDao.findOne(user.getId());
    }

    private ProjectDiscussionSubscription createDefaultProjectDiscussionSubscriptionForUser(int userId) {
        ProjectDiscussionSubscription discussionSubscription = new ProjectDiscussionSubscription();
        discussionSubscription.setUserId(userId);
        return discussionSubscriptionDao.save(discussionSubscription);
    }

    // Take care of duplicacy of client here
    public User createUser(User user) {
        if (exists(user)) {
//            patchUser(user);
        }
        else {
            userDao.save(user);
        }
        
        // TODO Auto-generated method stub
        return null;
    }

    private boolean exists(User user) {
        // TODO Auto-generated method stub
        return false;
    }
}
