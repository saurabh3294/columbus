package com.proptiger.app.config.security.social;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;

import com.proptiger.data.enums.AuthProvider;
import com.proptiger.data.service.user.UserService;

/**
 * 
 * @author Rajeev Pandey
 * @author azi
 * 
 */
public final class ConnectionSignUpImpl implements ConnectionSignUp {
    @Autowired
    private UserService userService;

    /*
     * Creating user social connection and saving in database to get connection
     * information from local database from next login onwards
     */
    public String execute(Connection<?> connection) {
        ConnectionKey connectionKey = connection.getKey();
        UserProfile userProfile = connection.fetchUserProfile();

        URL imageUrl = null;
        try {
            imageUrl = new URL(connection.getImageUrl());
        }
        catch (MalformedURLException e) {
        }

        int userId = userService.createSocialAuthDetails(
                userProfile,
                AuthProvider.getAuthProviderIgnoreCase(connectionKey.getProviderId()),
                connectionKey.getProviderUserId(),
                imageUrl).getId();

        return String.valueOf(userId);
    }
}
