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
import com.proptiger.data.internal.dto.mail.MailDetails;
import com.proptiger.data.internal.dto.mail.MediumDetails;
import com.proptiger.data.notification.enums.MediumType;
import com.proptiger.data.notification.enums.NotificationTypeEnum;

public class NotificationCreatorServiceRequest extends BaseModel {

    private static final long           serialVersionUID = 1156368440678315380L;

    @NotNull
    private NotificationTypeEnum        notificationType;

    @Size(min = 1)
    private List<User>                  users            = new ArrayList<User>();

    private Map<String, Object>         payloadMap       = new HashMap<String, Object>();

    @Size(min = 1)
    private Map<MediumType, MediumDetails> mediumTypes      = new HashMap<MediumType, MediumDetails>();

    public NotificationCreatorServiceRequest() {

    }

    public NotificationCreatorServiceRequest(int userId, MailDetails mailDetails) {
        User user = new User();
        user.setId(userId);
        this.users.add(user);
        this.mediumTypes.put(MediumType.Email, mailDetails);
        this.notificationType = NotificationTypeEnum.Default;
    }

    public NotificationCreatorServiceRequest(
            NotificationTypeEnum notificationType,
            int userId,
            DefaultMediumDetails mediumData,
            MediumType mediumType) {

        if (notificationType != null) {
            this.notificationType = notificationType;
        }
        User user = new User();
        user.setId(userId);
        this.users.add(user);

        if (mediumType != null && !MediumType.Email.equals(mediumType)) {
            this.mediumTypes.put(mediumType, mediumData);
        }
    }

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
            for (MediumType mediumType : mediumTypes) {
                this.mediumTypes.put(mediumType, null);
            }
        }
    }

    public NotificationCreatorServiceRequest(
            NotificationTypeEnum notificationType,
            int userId,
            Map<String, Object> payloadMap,
            Map<MediumType, ? extends MediumDetails> mediumTypes) {

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
            this.mediumTypes.putAll(mediumTypes);
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

    public Map<String, Object> getPayloadMap() {
        return payloadMap;
    }

    public void setPayloadMap(Map<String, Object> payloadMap) {
        this.payloadMap = payloadMap;
    }

    public Map<MediumType, MediumDetails> getMediumTypes() {
        return mediumTypes;
    }

    public void setMediumTypes(Map<MediumType, MediumDetails> mediumTypes) {
        this.mediumTypes = mediumTypes;
    }

}
