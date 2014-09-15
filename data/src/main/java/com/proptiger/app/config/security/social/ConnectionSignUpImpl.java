package com.proptiger.app.config.security.social;

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
    public static final String PROFILE_IMAGE_FORMAT = ".jpg";

    @Autowired
    private UserService userService;

    /*
     * Creating user social connection and saving in database to get connection
     * information from local database from next login onwards
     */
    public String execute(Connection<?> connection) {
        ConnectionKey connectionKey = connection.getKey();
        UserProfile userProfile = connection.fetchUserProfile();
        int userId = userService.createSocialAuthDetails(
                userProfile,
                AuthProvider.getAuthProviderIgnoreCase(connectionKey.getProviderId()),
                connectionKey.getProviderUserId(),
                connection.getImageUrl()).getId();

        return String.valueOf(userId);
    }
}
