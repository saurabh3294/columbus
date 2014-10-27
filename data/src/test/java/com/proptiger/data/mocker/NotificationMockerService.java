package com.proptiger.data.mocker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.proptiger.data.model.user.User;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeUserStrategy;
import com.proptiger.data.notification.enums.Tokens;
import com.proptiger.data.notification.model.NotificationMedium;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeConfig;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
import com.proptiger.data.notification.model.payload.NotificationMessagePayload;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.notification.processor.DefaultNotificationMessageProcessor;

/**
 * 
 * @author sahil
 * 
 */
@Service
public class NotificationMockerService {

    private Random                   random             = new Random();

    public static final List<String> NOTIFICATION_TYPES = new ArrayList<String>();

    static {
        NOTIFICATION_TYPES.add("mockNotificationTypeName");
    }

    public NotificationTypeGenerated getMockNotificationTypeGenerated() {
        NotificationTypeGenerated ntGenerated = new NotificationTypeGenerated();
        ntGenerated.setId(98732);
        ntGenerated.setNotificationType(getMockNotificationType());
        ntGenerated.setNotificationTypePayload(getMockNotificationTypePayload());
        return ntGenerated;
    }

    public List<User> getMockUserList() {
        User user = new User();
        user.setId(53453);

        List<User> userList = new ArrayList<User>();
        userList.add(user);
        return userList;
    }

    public List<NotificationType> getMockNotificationTypes() {
        List<NotificationType> notificationTypes = new ArrayList<NotificationType>();
        notificationTypes.add(getMockNotificationType(NOTIFICATION_TYPES.get(0)));
        return notificationTypes;
    }

    public NotificationType getMockNotificationType() {
        return getMockNotificationType(NOTIFICATION_TYPES.get(random.nextInt(NOTIFICATION_TYPES.size())));
    }

    public List<PortfolioListing> getMockPortfolioListings(Integer propertyId) {
        PortfolioListing portfolioListing = new PortfolioListing();
        portfolioListing.setUserId(56453);
        portfolioListing.setProjectName("mockProjectName");
        portfolioListing.setName("mockPropertyName");

        List<PortfolioListing> portfolioListings = new ArrayList<PortfolioListing>();
        portfolioListings.add(portfolioListing);
        return portfolioListings;
    }

    public NotificationMessage getMockNotificationMessage() {
        Integer userId = 241221;
        NotificationType notiType = getMockNotificationType();
        NotificationMessagePayload payload = getMockNotificationMessagePayload();        
        return new NotificationMessage(userId, payload, notiType);
    }

    public NotificationMedium getMockNotificationMedium(MediumType mediumType) {
        NotificationMedium notificationMedium = new NotificationMedium();
        notificationMedium.setId(523);
        notificationMedium.setName(mediumType);
        return notificationMedium;
    }

    public Map<Integer, List<NotificationMedium>> getMockTypeMediumMapping(Integer notificationTypeId) {
        NotificationMedium medium = getMockNotificationMedium(MediumType.Email);
        List<NotificationMedium> mediumList = new ArrayList<NotificationMedium>();
        mediumList.add(medium);

        Map<Integer, List<NotificationMedium>> typeMediumMapping = new HashMap<Integer, List<NotificationMedium>>();
        typeMediumMapping.put(notificationTypeId, mediumList);
        return typeMediumMapping;
    }

    public NotificationType getMockNotificationType(String notificationTypeName) {
        NotificationType notificationType = new NotificationType();
        notificationType.setId(random.nextInt(1000) + 1);
        notificationType.setName(notificationTypeName);
        notificationType.setNotificationTypeConfig(getMockNotificationTypeConfig());
        notificationType.setUserStrategy(NotificationTypeUserStrategy.MinusUnsubscribed);
        return notificationType;
    }

    private NotificationTypeConfig getMockNotificationTypeConfig() {
        NotificationTypeConfig config = new NotificationTypeConfig();
        config.setNotificationTypePayloadObject(new NotificationTypePayload());
        config.setNotificationMessageProcessorObject(new DefaultNotificationMessageProcessor());
        return config;
    }

    private NotificationTypePayload getMockNotificationTypePayload() {
        NotificationTypePayload payload = new NotificationTypePayload();
        payload.setPrimaryKeyValue(53495);
        return payload;
    }

    private NotificationMessagePayload getMockNotificationMessagePayload() {
        Map<String, Object> extraAttributes = new HashMap<String, Object>();
        extraAttributes.put(Tokens.PortfolioProjectUpdates.ProjectName.name(), "dummyProjectName");

        List<String> ccList = new ArrayList<String>();
        ccList.add("cc-email@proptiger.com");
        
        List<String> bccList = new ArrayList<String>();
        bccList.add("bcc-email@proptiger.com");

        String fromEmail = "from-email@proptiger.com";
        
        NotificationMessagePayload payload = new NotificationMessagePayload();
        payload.setNotificationTypePayload(getMockNotificationTypePayload());
        payload.setExtraAttributes(extraAttributes);
        payload.setCcList(ccList);
        payload.setBccList(bccList);
        payload.setFromEmail(fromEmail);
        return payload;
    }
}
