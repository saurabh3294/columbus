package com.proptiger.data.service.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.PostConstruct;

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
import com.proptiger.data.model.ProjectDiscussionSubscription;
import com.proptiger.data.model.SubscriptionPermission;
import com.proptiger.data.model.SubscriptionSection;
import com.proptiger.data.model.UserPreference;
import com.proptiger.data.model.UserSubscriptionMapping;
import com.proptiger.data.model.user.User;
import com.proptiger.data.model.user.UserAttribute;
import com.proptiger.data.model.user.UserAuthProviderDetail;
import com.proptiger.data.model.user.UserContactNumber;
import com.proptiger.data.pojo.Selector;
import com.proptiger.data.repo.EnquiryDao;
import com.proptiger.data.repo.ForumUserDao;
import com.proptiger.data.repo.ForumUserTokenDao;
import com.proptiger.data.repo.ProjectDiscussionSubscriptionDao;
import com.proptiger.data.repo.SubscriptionPermissionDao;
import com.proptiger.data.repo.UserSubscriptionMappingDao;
import com.proptiger.data.repo.trend.TrendDao;
import com.proptiger.data.repo.user.UserAttributeDao;
import com.proptiger.data.repo.user.UserAuthProviderDetailDao;
import com.proptiger.data.repo.user.UserContactNumberDao;
import com.proptiger.data.repo.user.UserDao;
import com.proptiger.data.repo.user.UserEmailDao;
import com.proptiger.data.service.B2BAttributeService;
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

    @Autowired
    private UserAttributeDao                 userAttributeDao;

    @PostConstruct
    private void initialize() {
        currentMonth = b2bAttributeService.getAttributeByName(currentMonthDbLabel);
    }

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

        user = userDao.saveAndFlush(user);

        createDefaultProjectDiscussionSubscriptionForUser(user.getId());

        manageContactNumberOnRegistration(user, register);

        /*
         * send mail only if user registers
         */
        if (user.isRegistered()) {
            ForumUser registeredUser = forumUserDao.findByUserId(user.getId());
            MailBody mailBody = htmlGenerator.generateMailBody(MailTemplateDetail.NEW_USER_REGISTRATION, register);
            MailDetails details = new MailDetails(mailBody).setMailTo(register.getEmail()).setFrom(
                    propertyReader.getRequiredProperty(PropertyKeys.MAIL_FROM_SUPPORT));
            mailSender.sendMailUsingAws(details);
            SecurityContextUtils.autoLogin(registeredUser);
        }

        /*
         * after registration make user auto login
         */
        return getUserDetails(user.getId());
    }

    private User getUserFromRegister(Register register) {
        User user = userDao.findByEmail(register.getEmail());
        if (user == null) {
            user = createFreshUserFromRegister(register);
        }
        else {
            if (!register.getRegisterMe() || user.isRegistered()) {
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
        user.setEmail(register.getEmail());
        user.copyFieldsFromRegisterToUser(register);
        return user;
    }

    // manages contact numbers for every registration
    // will be more relevant once we start supporting multiple contacts
    private void manageContactNumberOnRegistration(User user, Register register) {
        String primaryContactNumber = register.getContact().toString();
        UserContactNumber savedContactNumber = user.getContactByContactNumber(primaryContactNumber);

        if (savedContactNumber == null) {
            contactNumberDao.incrementPriorityForUser(user.getId());
            UserContactNumber userContactNumber = new UserContactNumber(primaryContactNumber, user.getId());
            contactNumberDao.save(userContactNumber);
        }
        else {
            if (savedContactNumber.getPriority() != UserContactNumber.primaryContactPriority) {
                contactNumberDao.incrementPriorityForUser(user.getId());
                savedContactNumber.setPriority(UserContactNumber.primaryContactPriority);
                contactNumberDao.save(savedContactNumber);
            }
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
        ForumUser forumUser = forumUserDao.findRegisteredUserByEmail(email);
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

    private void updateContactNumbers(User user) {
        List<UserContactNumber> contactNumbers = user.getContactNumbers();


        if (contactNumbers == null || contactNumbers.isEmpty()) {
            return;
        }

        UserContactNumber userContactNumber = contactNumbers.get(0);
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
     * 
     * @param email
     * @param contactNumber
     *            get user on the basis of email or contact_numbers
     * @return
     */
    public User getUser(String email) {
        return userDao.findByEmail(email);
    }

    public Map<Integer, User> getUsers(List<Integer> clientIds) {
        Map<Integer, User> users = new HashMap<>();
        for (User user : userDao.findAll(clientIds)) {
            users.put(user.getId(), user);
        }

        return users;
    }

    public Map<Integer, List<UserContactNumber>> getUserContactNumbers(List<Integer> clientIds) {
        List<UserContactNumber> userContactNumbers = contactNumberDao.getContactNumbersByUserId(clientIds);
        Map<Integer, List<UserContactNumber>> contactNumbersOfUser = new HashMap<>();

        for (UserContactNumber userContactNumber : userContactNumbers) {
            if (!contactNumbersOfUser.containsValue(userContactNumber.getUserId())) {
                contactNumbersOfUser.put(userContactNumber.getUserId(), new ArrayList<UserContactNumber>());
            }
            contactNumbersOfUser.get(userContactNumber.getUserId()).add(userContactNumber);
        }
        return contactNumbersOfUser;
    }

    public User getUserWithContactNumberById(int Id) {
        return userDao.findById(Id);
    }

    public User getUserById(int userId) {
        return userDao.findOne(userId);
    }

    public UserContactNumber getTopPriorityContact(int userId) {
        List<UserContactNumber> contacts = contactNumberDao.findByUserIdOrderByPriorityAsc(userId);
        if (contacts.isEmpty()) {
            return null;
        }
        return contacts.get(0);
    }
    
    /**
     * This method will return the attributes of the user based on the attribute value provided.
     * @param userId
     * @param attributeValue
     * @return
     */
    public UserAttribute checkUserAttributesByAttributeValue(int userId, String attributeValue) {
        return userAttributeDao.findByUserIdAndAttributeValue(userId, attributeValue);
    }

    public void enrichUserDetails(User user) {
        user.setContactNumbers(contactNumberDao.findByUserIdOrderByPriorityAsc(user.getId()));
        user.setAttributes(userAttributeDao.findByUserId(user.getId()));
    }
}
