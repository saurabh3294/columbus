package com.proptiger.data.notification.sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.GCMUsers;
import com.proptiger.data.repo.GCMUsersDao;

@Service
public class AndroidSender implements MediumSender {

    private static Logger        logger            = LoggerFactory.getLogger(AndroidSender.class);

    // Put your Google API Server Key here
    private static final String  GOOGLE_SERVER_KEY = "AIzaSyAXoA544kq4k2AqEOuzW81fZOor2JK2am4";
    private static final String  MESSAGE_KEY       = "message";
    private static final Integer RETRY_COUNT       = 3;

    @Autowired
    private GCMUsersDao          gcmUsersDao;

    @Override
    public void send(MailBody mailBody, ForumUser forumUser) {
        
        String emailId = forumUser.getEmail();
        List<GCMUsers> gcmUsersList = gcmUsersDao.findByEmail(emailId);

        if (gcmUsersList == null) {
            logger.debug("No GCM user found with email id: " + emailId);
            return;
        }

        String messageString = mailBody.getBody();
        List<String> regIds = new ArrayList<String>();

        for (GCMUsers gcmUser : gcmUsersList) {
            String regId = gcmUser.getGcmRegId();

            /*
             * For testing, please add a test reg id below to avoid sending
             * unnecessary notifications to actual users
             */
            //regId = "APA91bG-7vGe2VPa1KILEL0-Lv3fIBa9EKSTsKNl9BOqUfbeiL61wRMUXh7NsuQTmNO3h7K1-dCMCSzo0AX6A3W0ufDH5eZOo3BRFyzCv7Ovb2GrTrNoO2qs33KBqKjzhy2YeA1OWd_hlZUJZiyoWSz4mwbrcUrvRQ";
            regId = "APA91bH2YQR2UDkjdHSmz5UPeZrE2DFzSOF37FK3DeBnGHFRkXSaplBQLocrPZ2dTy-Y0Z6hTfI9QszdtT4usbVsOWameXYVoYH9RknKFtkdGwlEW__V3MpmDR1zo3Dn_sNFDkY6XwFYuOUYTWKc2kr-N8TSpQlzSA";
            
            logger.debug("Sending Android notification " + messageString
                    + " to email: "
                    + emailId
                    + " and regId: "
                    + regId);

            regIds.add(regId);
        }
        pushToAndroidDevice(regIds, messageString);
    }

    public void pushToAndroidDevice(List<String> regIds, String messageString) {
        Sender sender = new Sender(GOOGLE_SERVER_KEY);
        Message message = new Message.Builder().timeToLive(30).delayWhileIdle(true).addData(MESSAGE_KEY, messageString)
                .build();
        try {
            MulticastResult result = sender.send(message, regIds, RETRY_COUNT);
            logger.debug("Got Result " + result.toString()
                    + " after sending android notification "
                    + messageString
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
