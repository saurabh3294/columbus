package com.proptiger.data.model.marketplace;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.proptiger.data.model.BaseModel;
import com.proptiger.data.util.DateUtil;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "marketplace.master_notification_types")
public class NotificationType extends BaseModel {
    private static final long                         serialVersionUID = 1L;

    @Id
    private int                                       id;

    @Column(name = "notification_type")
    @Enumerated(EnumType.STRING)
    private com.proptiger.data.enums.NotificationType notificationType;

    @Column(name = "is_ignorable")
    private boolean                                   ignorable;

    @Column(name = "is_groupable")
    private boolean                                   groupable;

    @Column(name = "object_type_id")
    private int                                       objectTypeId;

    @OneToMany(mappedBy = "notificationTypeId")
    private List<Notification>                        notifications;

    private static final Comparator<NotificationType> notificationTypeReverseComparator = new Comparator<NotificationType>() {
        @Override
        public int compare(NotificationType o1, NotificationType o2) {
            int result;
            if (o1.isIgnorable() != o2.isIgnorable()) {
                if (o2.isIgnorable()) {
                    result = 1;
                }
                else {
                    result = -1;
                }
            }
            else {
                Date o1Date = o1.getLatestNotificationDate();
                Date o2Date = o2.getLatestNotificationDate();
                if (o1Date == null) {
                    result = -1;
                }
                else if (o2Date == null) {
                    result = 1;
                }
                else {
                    result = o1Date.compareTo(o2Date);
                }
            }
            return result * (-1);
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public com.proptiger.data.enums.NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(com.proptiger.data.enums.NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isIgnorable() {
        return ignorable;
    }

    public void setIgnorable(boolean ignorable) {
        this.ignorable = ignorable;
    }

    public boolean isGroupable() {
        return groupable;
    }

    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

    public int getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Date getLatestNotificationDate() {
        Date date = null;
        if (notifications != null) {
            for (Notification notification : notifications) {
                date = DateUtil.max(date, notification.getCreatedAt());
            }
        }
        return date;
    }

    public static Comparator<NotificationType> getNotificationtypereversecomparator() {
        return notificationTypeReverseComparator;
    }
}