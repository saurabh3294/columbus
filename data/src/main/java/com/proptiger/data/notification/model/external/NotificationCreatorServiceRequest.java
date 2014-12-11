package com.proptiger.data.notification.model.external;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.proptiger.core.model.BaseModel;
import com.proptiger.core.model.user.User;
import com.proptiger.data.internal.dto.mail.DefaultMediumDetails;
import com.proptiger.data.internal.dto.mail.MediumDetails;
import com.proptiger.data.notification.enums.NotificationTypeEnum;

public class NotificationCreatorServiceRequest extends BaseModel {

    private static final long   serialVersionUID = 1156368440678315380L;

    @NotNull
    private String              notificationType;

    @Size(min = 1)
    private List<User>          users            = new ArrayList<User>();

    private Map<String, Object> payloadMap       = new HashMap<String, Object>();

    @Size(min = 1)
    private List<MediumDetails> mediumDetails    = new ArrayList<MediumDetails>();

    public NotificationCreatorServiceRequest() {

    }

    public NotificationCreatorServiceRequest(int userId, MediumDetails mediumDetails) {
        User user = new User();
        user.setId(userId);
        this.users.add(user);
        this.mediumDetails.add(mediumDetails);
        this.notificationType = NotificationTypeEnum.Default.name();
    }

    public NotificationCreatorServiceRequest(
            NotificationTypeEnum notificationType,
            int userId,
            DefaultMediumDetails mediumDetails) {

        if (notificationType == null) {
            notificationType = NotificationTypeEnum.Default;
        }
        User user = new User();
        user.setId(userId);
        this.users.add(user);
        this.mediumDetails.add(mediumDetails);
        this.notificationType = notificationType.name();
    }

    public NotificationCreatorServiceRequest(
            NotificationTypeEnum notificationType,
            int userId,
            Map<String, Object> payloadMap,
            List<MediumDetails> mediumDetails) {

        if (notificationType == null) {
            notificationType = NotificationTypeEnum.Default;
        }
        this.notificationType = notificationType.name();

        User user = new User();
        user.setId(userId);
        this.users.add(user);

        if (payloadMap != null) {
            this.payloadMap.putAll(payloadMap);
        }

        if (mediumDetails != null) {
            this.mediumDetails.addAll(mediumDetails);
        }
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Map<String, Object> getPayloadMap() {
        return payloadMap;
    }

    public void setPayloadMap(Map<String, Object> payloadMap) {
        this.payloadMap = payloadMap;
    }

    public List<MediumDetails> getMediumDetails() {
        return mediumDetails;
    }

    public void setMediumDetails(List<MediumDetails> mediumDetails) {
        this.mediumDetails = mediumDetails;
    }

}
