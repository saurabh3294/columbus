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
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.GCMUsers;
import com.proptiger.data.service.GCMUserService;

@Service
public class AndroidSender implements MediumSender {

    private static Logger       logger       = LoggerFactory.getLogger(AndroidSender.class);

    private static final String DUMMY_REG_ID = "APA91bH2YQR2UDkjdHSmz5UPeZrE2DFzSOF37FK3DeBnGHFRkXSaplBQLocrPZ2dTy-Y0Z6hTfI9QszdtT4usbVsOWameXYVoYH9RknKFtkdGwlEW__V3MpmDR1zo3Dn_sNFDkY6XwFYuOUYTWKc2kr-N8TSpQlzSA";
    // private static final String DUMMY_REG_ID =
    // "APA91bFplBbOgmXopu5RfDO1tiSJH9tkIW-Cl0BwmAQhOL8lGVt50sj-eLwX6TVXpOmgno6MXG9-iRYvdH-H9RNDYAF37glWThvE-yx38ymIBnM6ZsXzdurOHsssQ-0e-jk1Ikeod-ywQWGEE8Q_SuqMNI99I8EkyA";

    private static final String TYPE_KEY     = "type";
    private static final String DATA_KEY     = "data";

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
            e.printStackTrace();
        }
    }

    @Override
    public void send(String template, ForumUser forumUser, String typeName) {
        List<GCMUsers> gcmUsersList = new ArrayList<GCMUsers>();
        if (forumUser == null || forumUser.getUserId() == null) {
            gcmUsersList = gcmUserService.findAll();
        }
        else {
            gcmUsersList = gcmUserService.findByLoggedInUserId(forumUser.getUserId());
        }
        pushToAndroidDevice(template, gcmUsersList, typeName);
    }

    public void sendToMarketplaceApp(String template, ForumUser forumUser, String typeName) {
        findUsersAndPushToAndroidDevice(template, forumUser, AndroidApplication.Marketplace, typeName);
    }

    public void sendToProptigerApp(String template, ForumUser forumUser, String typeName) {
        findUsersAndPushToAndroidDevice(template, forumUser, AndroidApplication.Proptiger, typeName);
    }

    private void findUsersAndPushToAndroidDevice(
            String template,
            ForumUser forumUser,
            AndroidApplication androidApp,
            String typeName) {

        List<GCMUsers> gcmUsersList = new ArrayList<GCMUsers>();
        if (forumUser == null || forumUser.getUserId() == null) {
            gcmUsersList = gcmUserService.findByAppIdentifier(androidApp);
        }
        else {
            gcmUsersList = gcmUserService.findByAppIdentifierAndLoggedInUserId(androidApp, forumUser.getUserId());
        }
        pushToAndroidDevice(template, gcmUsersList, typeName);
    }

    private void pushToAndroidDevice(String template, List<GCMUsers> gcmUsersList, String typeName) {

        if (gcmUsersList == null) {
            logger.info("No GCM users found.");
            return;
        }

        // Create a map of AppIdentifier to List of Reg Ids
        Map<AndroidApplication, List<String>> regIdMap = new HashMap<AndroidApplication, List<String>>();
        for (GCMUsers gcmUser : gcmUsersList) {
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

            /*
             * For testing, please add a test reg id below to avoid sending
             * unnecessary notifications to actual users
             */
            regIds.clear();
            regIds.add(DUMMY_REG_ID);

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
                logger.debug("Got Result " + result.toString()
                        + " after sending android notification "
                        + dataMap
                        + " to regIds: "
                        + regIds);
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
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
