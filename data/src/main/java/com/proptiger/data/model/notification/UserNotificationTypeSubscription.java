/**
 * 
 */
package com.proptiger.data.model.notification;

import java.util.Date;
import java.util.Map;


/**
 * @author mandeep
 * TODO - Check if we really need this
 */
public class UserNotificationTypeSubscription {
    private int userId;
    Map<NotificationDeprecatedType, Date> notificationTypesMap;
}
