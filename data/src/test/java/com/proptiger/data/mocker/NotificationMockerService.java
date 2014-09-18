package com.proptiger.data.mocker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.user.portfolio.PortfolioListing;
import com.proptiger.data.notification.enums.NotificationTypeUserStrategy;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.model.NotificationTypeConfig;
import com.proptiger.data.notification.model.NotificationTypeGenerated;
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

    public List<ForumUser> getMockUserList() {
        ForumUser user = new ForumUser();
        user.setUserId(53453);

        List<ForumUser> userList = new ArrayList<ForumUser>();
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

    private NotificationType getMockNotificationType(String notificationTypeName) {
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
}
