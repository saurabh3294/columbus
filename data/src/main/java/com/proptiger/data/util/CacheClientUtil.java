package com.proptiger.data.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.proptiger.data.enums.Application;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.service.ApplicationNameService;
import com.proptiger.exception.ProAPIException;

/**
 * Util class to connect memcache and get the values from memcache
 * 
 * @author Rajeev Pandey
 * 
 */
@Component
public class CacheClientUtil {

    private static Pattern         userIdPattern   = Pattern.compile("USER_ID.+?\"(\\d+?)\"");
    private static Pattern         userNamePattern = Pattern.compile("USERNAME.+?\"([a-z|A-Z|0-9].+?)\"");
    private static Pattern         emailPattern    = Pattern.compile("EMAIL.+?\"(.+?)\"");

    private static MemcachedClient memcachedClient;
    @Autowired
    private PropertyReader         propertyReader;
    private static final Logger    logger          = LoggerFactory.getLogger(CacheClientUtil.class);

    @PostConstruct
    private void init() throws IOException {
        try {
            memcachedClient = new MemcachedClient(AddrUtil.getAddresses(propertyReader
                    .getRequiredProperty(PropertyKeys.MEMCACHE_URL_PORT)));
        }
        catch (IOException e) {
            logger.error("Exception while connecting to memcache", e);
            throw e;
        }
    }

    public static String getValue(String key) {
        if (key == null || "".equals(key)) {
            return null;
        }
        return (String) memcachedClient.get(key);
    }

    /**
     * Get user id from memcache based on key sessionId is expected to be
     * Constants.PHPSESSID_KEY
     * 
     * @param sessionId
     * @return
     */
    public static ActiveUser getUserInfoFromMemcache(String sessionId) {
        if (sessionId == null) {
            throw new ProAPIException("Session id null");
        }
        ActiveUser userInfo = null;// new ActiveUser();
        Integer userId = null;
        String userName = null;
        String email = null;
        if (sessionId != null) {
            String value = (String) CacheClientUtil.getValue(sessionId);
            if (value != null) {

                Matcher userIdMatcher = userIdPattern.matcher(value);
                Matcher userNameMatcher = userNamePattern.matcher(value);
                Matcher emailMatcher = emailPattern.matcher(value);
                while (userIdMatcher.find()) {
                    try {
                        userId = Integer.parseInt(userIdMatcher.group(1));
                        break;
                    }
                    catch (NumberFormatException e) {
                        logger.error("Number format exception {}", e.getMessage());
                    }
                }
                while (userNameMatcher.find()) {
                    userName = userNameMatcher.group(1);
                    break;
                }
                while (emailMatcher.find()) {
                    email = emailMatcher.group(1);
                    break;
                }
            }
        }
        if (userId == null) {
            throw new ProAPIException("session data not found in memcache for sessionkey " + sessionId);
        }
        else {
            Application applicationType = ApplicationNameService.getApplicationTypeOfRequest();
            userInfo = new ActiveUser(
                    userId,
                    email,
                    "dummy",
                    true,
                    true,
                    true,
                    true,
                    SecurityContextUtils.getUserAuthority(applicationType),
                    applicationType);
        }
        return userInfo;
    }

}
