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

    public static final String PROFILE_IMAGE_FORMAT = ".jpg";

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
        forumUser.setProviderid(connectionKey.getProviderUserId());
        forumUser.setUsername(userProfile.getName());
        forumUser.setEmail(userProfile.getEmail());
        if (connectionKey.getProviderId().equalsIgnoreCase("facebook")) {
            //setting provider with first char as caps, to provide backward compatibility from database
            forumUser.setProvider("Facebook");
            forumUser.setFbImageUrl(connection.getImageUrl());
            forumUser.setImage(connectionKey.getProviderUserId() + PROFILE_IMAGE_FORMAT);
        }
        else if(connectionKey.getProviderId().equalsIgnoreCase("google")){
            //setting provider with first char as caps, to provide backward compatibility from database
            forumUser.setProvider("Google");
            forumUser.setFbImageUrl("");
            forumUser.setImage("");
        }
        else{
            forumUser.setProvider(connectionKey.getProviderId());
            forumUser.setFbImageUrl("");
            forumUser.setImage("");
        }
        
        forumUser.setCity("");
        forumUser.setPassword("");
        forumUser.setUniqueUserId("");
        forumUser.setStatus(ForumUser.USER_STATUS_ACTIVE);
        forumUser = forumUserDao.save(forumUser);
        return forumUser.getUserId().toString();
    }

}
