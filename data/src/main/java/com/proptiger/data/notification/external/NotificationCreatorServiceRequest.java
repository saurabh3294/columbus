package com.proptiger.data.notification.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeEnum;

public class NotificationCreatorServiceRequest {

    private NotificationTypeEnum notificationType = NotificationTypeEnum.Default;

    private List<Integer>        userIds          = new ArrayList<Integer>();

    private String               subject;

    private String               body;

    private String               template;

    private List<String>         ccList           = new ArrayList<String>();

    private List<String>         bccList          = new ArrayList<String>();

    private String               fromEmail;

    private Map<String, Object>  payloadMap       = new HashMap<String, Object>();

    private List<MediumType>     mediumTypes      = new ArrayList<MediumType>();   ;

    /**
     * For sending email notification of default type with specific subject and
     * body to a specific user
     * 
     * @param userId
     * @param subject
     * @param body
     */
    public NotificationCreatorServiceRequest(int userId, String subject, String body) {
        this.userIds.add(userId);
        this.subject = subject;
        this.body = body;
        this.mediumTypes.add(MediumType.Email);
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
        this.notificationType = notificationType;
        this.userIds.add(userId);
        this.template = template;
        this.mediumTypes = mediumTypes;
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
        this.notificationType = notificationType;
        this.userIds.add(userId);
        this.payloadMap = payloadMap;
        this.mediumTypes = mediumTypes;
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
        this.notificationType = notificationType;
        this.userIds.add(userId);
        this.payloadMap = payloadMap;
        this.fromEmail = fromEmail;
        this.ccList = ccList;
        this.bccList = bccList;
        this.mediumTypes = mediumTypes;
    }

    public NotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<String> getCcList() {
        return ccList;
    }

    public void setCcList(List<String> ccList) {
        this.ccList = ccList;
    }

    public List<String> getBccList() {
        return bccList;
    }

    public void setBccList(List<String> bccList) {
        this.bccList = bccList;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
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
