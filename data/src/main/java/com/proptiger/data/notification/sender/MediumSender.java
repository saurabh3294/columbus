package com.proptiger.data.notification.sender;

import com.proptiger.data.internal.dto.mail.MailBody;
import com.proptiger.data.model.ForumUser;

public interface MediumSender {
    public void send(MailBody mailBody, ForumUser forumUser);
}
