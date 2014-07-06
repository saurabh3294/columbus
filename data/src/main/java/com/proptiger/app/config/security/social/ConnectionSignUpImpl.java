package com.proptiger.app.config.security.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;

/**
 * 
 * @author Rajeev Pandey
 */
public final class ConnectionSignUpImpl implements ConnectionSignUp {

    private static final String PROFILE_IMAGE_FORMAT = ".jpg";

    private static final String ACTIVE               = "1";

    @Autowired
    private ForumUserDao        forumUserDao;

    /*
     * Creating user social connection and saving in database to get connection
     * information from local database from next login onwards
     */
    public String execute(Connection<?> connection) {
        ConnectionKey connectionKey = connection.getKey();
        UserProfile userProfile = connection.fetchUserProfile();
        ForumUser forumUser = new ForumUser();
        forumUser.setProvider(connectionKey.getProviderId());
        forumUser.setProviderid(connectionKey.getProviderUserId());
        forumUser.setUsername(userProfile.getName());
        forumUser.setEmail(userProfile.getEmail());
        if (connectionKey.getProviderId().equalsIgnoreCase("facebook")) {
            forumUser.setFbImageUrl(connection.getImageUrl());
            forumUser.setImage(connectionKey.getProviderUserId() + PROFILE_IMAGE_FORMAT);
        }
        forumUser.setCity("");
        forumUser.setPassword("");
        forumUser.setUniqueUserId("");
        forumUser.setStatus(ACTIVE);
        forumUser = forumUserDao.save(forumUser);
        return forumUser.getUserId().toString();
    }

}
