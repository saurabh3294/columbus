package com.proptiger.data.notification.sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.proptiger.data.enums.AndroidApplication;
import com.proptiger.data.model.GCMUser;
import com.proptiger.data.notification.model.payload.NotificationSenderPayload;
import com.proptiger.data.service.GCMUserService;

@Service
public class AndroidSender implements MediumSender {

    private static Logger       logger   = LoggerFactory.getLogger(AndroidSender.class);

    private static final String TYPE_KEY = "type";
    private static final String DATA_KEY = "data";

    @Autowired
    private GCMUserService      gcmUserService;

    @Value("${app.android.key}")
    private String              KEY_STRING;

    @Value("${app.android.timeToLiveInSeconds}")
    private Integer             TIME_TO_LIVE;

    @Value("${app.android.retryCount}")
    private Integer             RETRY_COUNT;

    private Map<String, String> androidKeyMap;

    @PostConstruct
    private void populateAndroidkeyMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            setAndroidKeyMap(mapper.readValue(KEY_STRING, HashMap.class));
        }
        catch (Exception e) {
            logger.error("Error while populating AndroidKeyMap.", e.getStackTrace().toString());
        }
    }

    @Override
    public boolean send(String template, Integer userId, String typeName, NotificationSenderPayload payload) {

        if (userId == null) {
            logger.error("Found null User Id while sending Push Notification.");
            return false;
        }

        List<GCMUser> gcmUsersList = new ArrayList<GCMUser>();
        gcmUsersList = gcmUserService.findByLoggedInUserId(userId);

        if (gcmUsersList == null || gcmUsersList.isEmpty()) {
            logger.error("No GCM users found for UserId: " + userId + " while sending Push Notification.");
            return false;
        }

        return pushToAndroidDevice(template, gcmUsersList, typeName);
    }

    public boolean sendToMarketplaceApp(String template, Integer userId, String typeName) {
        return findUsersAndPushToAndroidDevice(template, userId, AndroidApplication.Marketplace, typeName);
    }

    public boolean sendToProptigerApp(String template, Integer userId, String typeName) {
        return findUsersAndPushToAndroidDevice(template, userId, AndroidApplication.Proptiger, typeName);
    }

    private boolean findUsersAndPushToAndroidDevice(
            String template,
            Integer userId,
            AndroidApplication androidApp,
            String typeName) {

        if (userId == null) {
            logger.error("Found null User Id while sending Push Notification for " + androidApp.name()
                    + " AndroidApplication");
            return false;
        }

        List<GCMUser> gcmUsersList = new ArrayList<GCMUser>();
        gcmUsersList = gcmUserService.findByAppIdentifierAndLoggedInUserId(androidApp, userId);

        if (gcmUsersList == null || gcmUsersList.isEmpty()) {
            logger.error("No GCM users found for UserId: " + userId
                    + " while sending Push Notification for "
                    + androidApp.name()
                    + " AndroidApplication");
            return false;
        }

        return pushToAndroidDevice(template, gcmUsersList, typeName);
    }

    private boolean pushToAndroidDevice(String template, List<GCMUser> gcmUsersList, String typeName) {

        // Create a map of AppIdentifier to List of Reg Ids
        Map<AndroidApplication, List<String>> regIdMap = new HashMap<AndroidApplication, List<String>>();
        for (GCMUser gcmUser : gcmUsersList) {
            AndroidApplication app = gcmUser.getAppIdentifier();
            String regId = gcmUser.getGcmRegId();

            List<String> regIds = regIdMap.get(app);
            if (regIds == null) {
                regIds = new ArrayList<String>();
            }
            regIds.add(regId);
            regIdMap.put(app, regIds);
        }

        for (Map.Entry<AndroidApplication, List<String>> entry : regIdMap.entrySet()) {
            AndroidApplication app = entry.getKey();
            List<String> regIds = entry.getValue();

            // Sending Push Notification
            String androidKey = androidKeyMap.get(app.toString());
            Map<String, String> dataMap = getDataMap(template, typeName);
            Sender sender = new Sender(androidKey);
            Message message = new Message.Builder().timeToLive(TIME_TO_LIVE).delayWhileIdle(true).collapseKey(typeName)
                    .setData(dataMap).build();
            try {
                logger.debug("Sending Android notification with AppID: " + androidKey
                        + " and message: "
                        + message
                        + " to regIds: "
                        + regIds);

                MulticastResult result = sender.send(message, regIds, RETRY_COUNT);

                if (result.getSuccess() == 0) {
                    logger.error("Unable to send android notification to regIds: " + regIds
                            + ". Got Result: "
                            + result.toString());
                    return false;
                }

                logger.info("Got Result " + result.toString()
                        + " after sending android notification to regIds: "
                        + regIds);
            }
            catch (IOException ioe) {
                logger.error("Error while sending Push Notification.", ioe.getStackTrace().toString());
            }
            catch (Exception e) {
                logger.error("Error while sending Push Notification.", e.getStackTrace().toString());
            }
        }

        return true;
    }

    private Map<String, String> getDataMap(String template, String typeName) {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put(TYPE_KEY, typeName);
        dataMap.put(DATA_KEY, template);
        return dataMap;
    }

    public Map<String, String> getAndroidKeyMap() {
        return androidKeyMap;
    }

    public void setAndroidKeyMap(Map<String, String> androidKeyMap) {
        this.androidKeyMap = androidKeyMap;
    }
}
