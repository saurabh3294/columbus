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

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;

/**
 * Connection repository to find already estabilished connections with provider
 * 
 * @author Rajeev Pandey
 *
 */
public class CustomJdbcUsersConnectionRepository extends JdbcUsersConnectionRepository {

    @Autowired
    private ForumUserDao     forumUserDao;

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
        List<ForumUser> socialForumUser = forumUserDao.findByProviderAndProviderid(
                key.getProviderId(),
                key.getProviderUserId());
        if (socialForumUser.size() == 0 && connectionSignUp != null) {
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
            for (ForumUser forumUser : socialForumUser) {
                localUserIds.add(String.valueOf(forumUser.getUserId()));
            }
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
     * @return
     */
    public Authentication createAuthenicationByProviderAndProviderUserId(String provider, String providerUserId) {
        List<ForumUser> forumUserList = forumUserDao.findByProviderAndProviderid(provider, providerUserId);
        if (forumUserList != null && forumUserList.size() == 1) {
            ForumUser forumUser = forumUserList.get(0);
            SocialUser socialUser = new ActiveUser(
                    forumUser.getUserId(),
                    forumUser.getEmail(),
                    forumUser.getPassword(),
                    true,
                    true,
                    true,
                    true,
                    new ArrayList<GrantedAuthority>());

            return new UsernamePasswordAuthenticationToken(socialUser, null, socialUser.getAuthorities());
        }
        return null;
    }
}
