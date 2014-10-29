package com.proptiger.data.notification.model.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.proptiger.core.model.BaseModel;
import com.proptiger.core.model.user.User;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeEnum;

public class NotificationCreatorServiceRequest extends BaseModel {

    private static final long    serialVersionUID = 1156368440678315380L;

    @NotNull
    private NotificationTypeEnum notificationType;

    @Size(min=1)
    private List<User>           users            = new ArrayList<User>();

    private EmailAttributes      emailAttributes  = new EmailAttributes();

    private String               template;

    private Map<String, Object>  payloadMap       = new HashMap<String, Object>();

    @Size(min=1)
    private List<MediumType>     mediumTypes      = new ArrayList<MediumType>();

    public NotificationCreatorServiceRequest() {

    }

    /**
     * For sending email notification of default type with specific subject and
     * body to a specific user
     * 
     * @param userId
     * @param subject
     * @param body
     */
    public NotificationCreatorServiceRequest(int userId, String subject, String body) {
        User user = new User();
        user.setId(userId);
        this.users.add(user);
        this.emailAttributes.setSubject(subject);
        this.emailAttributes.setBody(body);
        this.mediumTypes.add(MediumType.Email);
        this.notificationType = NotificationTypeEnum.Default;
    }

    /**
     * For sending notification of specific type with specific template to a
     * specific user on multiple mediumTypes
     * 
     * @param notificationType
     * @param userId
     * @param template
     * @param mediumTypes
     */
    public NotificationCreatorServiceRequest(
            NotificationTypeEnum notificationType,
            int userId,
            String template,
            List<MediumType> mediumTypes) {
        if (notificationType != null) {
            this.notificationType = notificationType;
        }
        User user = new User();
        user.setId(userId);
        this.users.add(user);
        this.template = template;
        if (mediumTypes != null) {
            this.mediumTypes.addAll(mediumTypes);
        }
    }

    /**
     * For sending notification of specific type with given token values to a
     * specific user on multiple mediumTypes with an appropriate template
     * 
     * @param notificationType
     * @param userId
     * @param payloadMap
     * @param mediumTypes
     */
    public NotificationCreatorServiceRequest(
            NotificationTypeEnum notificationType,
            int userId,
            Map<String, Object> payloadMap,
            List<MediumType> mediumTypes) {
        if (notificationType != null) {
            this.notificationType = notificationType;
        }

        User user = new User();
        user.setId(userId);
        this.users.add(user);

        if (payloadMap != null) {
            this.payloadMap.putAll(payloadMap);
        }
        if (mediumTypes != null) {
            this.mediumTypes.addAll(mediumTypes);
        }
    }

    /**
     * For sending notification of specific type with given token values and
     * other information to a specific user on multiple mediumTypes with an
     * appropriate template
     * 
     * @param notificationType
     * @param userId
     * @param payloadMap
     * @param fromEmail
     * @param ccList
     * @param bccList
     * @param mediumTypes
     */
    public NotificationCreatorServiceRequest(
            NotificationTypeEnum notificationType,
            int userId,
            Map<String, Object> payloadMap,
            String fromEmail,
            List<String> ccList,
            List<String> bccList,
            List<MediumType> mediumTypes) {
        if (notificationType != null) {
            this.notificationType = notificationType;
        }

        User user = new User();
        user.setId(userId);
        this.users.add(user);

        if (payloadMap != null) {
            this.payloadMap.putAll(payloadMap);
        }

        this.emailAttributes.setFromEmail(fromEmail);

        if (ccList != null) {
            this.emailAttributes.getCcList().addAll(ccList);
        }
        if (bccList != null) {
            this.emailAttributes.getBccList().addAll(bccList);
        }
        if (mediumTypes != null) {
            this.mediumTypes.addAll(mediumTypes);
        }
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public EmailAttributes getEmailAttributes() {
        return emailAttributes;
    }

    public void setEmailAttributes(EmailAttributes emailAttributes) {
        this.emailAttributes = emailAttributes;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, Object> getPayloadMap() {
        return payloadMap;
    }

    public void setPayloadMap(Map<String, Object> payloadMap) {
        this.payloadMap = payloadMap;
    }

    public List<MediumType> getMediumTypes() {
        return mediumTypes;
    }

    public void setMediumTypes(List<MediumType> mediumTypes) {
        this.mediumTypes = mediumTypes;
    }

}
