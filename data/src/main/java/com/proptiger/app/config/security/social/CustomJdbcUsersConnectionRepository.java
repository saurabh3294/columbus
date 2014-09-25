package com.proptiger.app.config.security.social;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.security.SocialUser;

import com.proptiger.data.enums.Application;
import com.proptiger.data.enums.AuthProvider;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.user.User;
import com.proptiger.data.repo.user.UserDao;
import com.proptiger.data.service.ApplicationNameService;
import com.proptiger.data.service.user.UserService;
import com.proptiger.data.util.SecurityContextUtils;

/**
 * Connection repository to find already estabilished connections with provider
 * 
 * @author Rajeev Pandey
 * @author azi
 * 
 */

public class CustomJdbcUsersConnectionRepository extends JdbcUsersConnectionRepository {

    @Autowired
    private UserService      userService;

    @Autowired
    private UserDao          userDao;

    private ConnectionSignUp connectionSignUp;

    public CustomJdbcUsersConnectionRepository(
            DataSource dataSource,
            ConnectionFactoryLocator connectionFactoryLocator,
            TextEncryptor textEncryptor,
            ConnectionSignUp connectionSignUp) {
        super(dataSource, connectionFactoryLocator, textEncryptor);
        this.connectionSignUp = connectionSignUp;
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        List<String> localUserIds = new ArrayList<String>();
        User socialForumUser = userDao.findByProviderIdAndProviderUserId(
                AuthProvider.getAuthProviderIgnoreCase(key.getProviderId()).getProviderId(),
                key.getProviderUserId());

        if (socialForumUser == null && connectionSignUp != null) {
            /*
             * if no provider and provider user id combination found in database
             * then create new
             */
            String newUserId = connectionSignUp.execute(connection);
            if (newUserId != null) {
                return Arrays.asList(newUserId);
            }
        }
        else {
            localUserIds.add(String.valueOf(socialForumUser.getId()));
        }

        return localUserIds;
    }

    /**
     * This method create Authentication object using provider and provider user
     * id by fetching data from database, those users should already be logged
     * in create a row in forum user table. As of now this is being used to
     * create Authentication after checkuser.php hit
     * 
     * @param provider
     * @param providerUserId
     * @param profileImageUrl
     * @param email
     * @param userName
     * @return
     */
    public Authentication createAuthenicationByProviderAndProviderUserId(
            String provider,
            String providerUserId,
            String userName,
            String email,
            String profileImageUrl) {
        User user = userService.createSocialAuthDetails(
                email,
                userName,
                AuthProvider.getAuthProviderIgnoreCase(provider),
                providerUserId,
                profileImageUrl);
        if (user != null) {
            Application applicationType = ApplicationNameService.getApplicationTypeOfRequest();
            SocialUser socialUser = new ActiveUser(
                    user.getId(),
                    user.getEmail(),
                    (user.getPassword() == null) ? "dummy" : user.getPassword(),
                    true,
                    true,
                    true,
                    true,
                    SecurityContextUtils.getDefaultAuthority(),
                    applicationType);

            return new UsernamePasswordAuthenticationToken(socialUser, null, socialUser.getAuthorities());
        }

        return null;
    }
}
