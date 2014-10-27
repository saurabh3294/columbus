package com.proptiger.data.mocker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.proptiger.core.model.proptiger.PortfolioListing;
import com.proptiger.core.model.user.User;
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

    public NotificationMessage getMockNotificationMessageForEmail() {
        Integer userId = 1211883;
        NotificationType notiType = getMockNotificationType();
        NotificationMessagePayload payload = getMockNotificationMessagePayloadWithSubjectBody();
        return new NotificationMessage(userId, payload, notiType);
    }

    public NotificationMessage getMockNotificationMessageForTemplate(String template) {
        Integer userId = 1211884;
        NotificationType notiType = getMockNotificationType();
        NotificationMessagePayload payload = getMockNotificationMessagePayloadWithTemplate(template);
        return new NotificationMessage(userId, payload, notiType);
    }

    public NotificationMessage getMockNotificationMessageForTemplateMap(Map<String, Object> templateMap) {
        Integer userId = 1211884;
        NotificationType notiType = getMockNotificationType();
        NotificationMessagePayload payload = getMockNotificationMessagePayloadWithTemplateMap(templateMap);
        return new NotificationMessage(userId, payload, notiType);
    }

    public NotificationMessage getMockNotificationMessageForTemplateMap(
            Map<String, Object> templateMap,
            String fromEmail,
            List<String> ccList,
            List<String> bccList) {
        Integer userId = 1211884;
        NotificationType notiType = getMockNotificationType();
        NotificationMessagePayload payload = getMockNotificationMessagePayloadWithTemplateMap(
                templateMap,
                fromEmail,
                ccList,
                bccList);
        return new NotificationMessage(userId, payload, notiType);
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

    private NotificationMessagePayload getMockNotificationMessagePayloadWithSubjectBody() {
        String subject = "This is a mock email subject for XYZ";
        String body = "This is a mock email body for XYZ";

        Map<String, Object> extraAttributes = new HashMap<String, Object>();
        extraAttributes.put(Tokens.Default.Subject.name(), subject);
        extraAttributes.put(Tokens.Default.Body.name(), body);
        return getMockNotificationMessagePayloadWithTemplateMap(extraAttributes);
    }

    private NotificationMessagePayload getMockNotificationMessagePayloadWithTemplate(String template) {
        Map<String, Object> extraAttributes = new HashMap<String, Object>();
        extraAttributes.put(Tokens.Default.Template.name(), template);
        return getMockNotificationMessagePayloadWithTemplateMap(extraAttributes);
    }

    private NotificationMessagePayload getMockNotificationMessagePayloadWithTemplateMap(Map<String, Object> templateMap) {
        NotificationMessagePayload payload = new NotificationMessagePayload();
        payload.setNotificationTypePayload(getMockNotificationTypePayload());
        payload.setExtraAttributes(templateMap);
        return payload;
    }

    private NotificationMessagePayload getMockNotificationMessagePayloadWithTemplateMap(
            Map<String, Object> templateMap,
            String fromEmail,
            List<String> ccList,
            List<String> bccList) {
        NotificationMessagePayload payload = new NotificationMessagePayload();
        payload.setNotificationTypePayload(getMockNotificationTypePayload());
        payload.setExtraAttributes(templateMap);
        payload.setCcList(ccList);
        payload.setBccList(bccList);
        payload.setFromEmail(fromEmail);
        return payload;
    }

    private NotificationMessagePayload getMockNotificationMessagePayload() {
        Map<String, Object> extraAttributes = new HashMap<String, Object>();
        extraAttributes.put(Tokens.PortfolioProjectUpdates.ProjectName.name(), "dummyProjectName");

        NotificationMessagePayload payload = new NotificationMessagePayload();
        payload.setNotificationTypePayload(getMockNotificationTypePayload());
        payload.setExtraAttributes(extraAttributes);
        return payload;
    }
}
