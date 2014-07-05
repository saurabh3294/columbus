package com.proptiger.app.config.security.social;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;

import com.proptiger.data.model.ForumUser;
import com.proptiger.data.repo.ForumUserDao;

/**
 * Connection repository to find already estabilished connections with provider
 * @author Rajeev Pandey
 *
 */
public class CustomJdbcUsersConnectionRepository extends JdbcUsersConnectionRepository{

    @Autowired
    private ForumUserDao forumUserDao;
    
    private ConnectionSignUp connectionSignUp;
    
    public CustomJdbcUsersConnectionRepository(
            DataSource dataSource,
            ConnectionFactoryLocator connectionFactoryLocator,
            TextEncryptor textEncryptor,ConnectionSignUp connectionSignUp) {
        super(dataSource, connectionFactoryLocator, textEncryptor);
        this.connectionSignUp = connectionSignUp;
    }
    
    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        List<String> localUserIds = new ArrayList<String>();
        List<ForumUser> socialForumUser = forumUserDao.findByProviderAndProviderid(key.getProviderId(), key.getProviderUserId());
        if (socialForumUser.size() == 0 && connectionSignUp != null) {
            String newUserId = connectionSignUp.execute(connection);
            if (newUserId != null)
            {
                createConnectionRepository(newUserId).addConnection(connection);
                return Arrays.asList(newUserId);
            }
        }
        else{
            for(ForumUser forumUser: socialForumUser){
                localUserIds.add(String.valueOf(forumUser.getUserId()));
            }
        }
        
        return localUserIds;
    }

}
