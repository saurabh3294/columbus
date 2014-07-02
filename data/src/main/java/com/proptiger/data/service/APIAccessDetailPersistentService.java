package com.proptiger.data.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.APIAccessLog;
import com.proptiger.data.repo.APIAccessLogDao;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.data.util.SecurityContextUtils;

/**
 * This service is saving user access data either in log file for anonymous user
 * or in database for logged in user.
 * 
 * @author Rajeev Pandey
 *
 */
@Service
@ManagedResource(
        objectName = "com.proptiger.data.service:name=APIAccessDetailPersistentService",
        description = "API access details service")
public class APIAccessDetailPersistentService {
    private static final String                 MIXPANEL_DISTINCT_ID                  = "distinct_id";
    private static final String                 MP_MIXPANEL_COOKIE_REGEX              = "mp_.*_mixpanel";
    private static final Logger                 logger                                = LoggerFactory
                                                                                              .getLogger(APIAccessDetailPersistentService.class);
    private static final int                    ACCESS_LOG_FETCH_BATCH_THRESHOLD      = 100;
    private boolean                             schedulingEnabled                     = true;
    private ObjectMapper                        mapper;

    private ConcurrentLinkedQueue<APIAccessLog> apiAccessLogToPersist;

    @Autowired
    private APIAccessLogDao                     apiAccessLogDao;

    @Autowired
    private PropertyReader propertyreader;
    
    @PostConstruct
    public void postConstruct() {
        mapper = new ObjectMapper();
        apiAccessLogToPersist = new ConcurrentLinkedQueue<>();
    }

    public void processRequest(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        ActiveUser activeUser = SecurityContextUtils.getLoggedInUser();
        if (activeUser != null) {
            addAccessLogToInternalDS(activeUser, httpRequest);
        }
        else {
            putAccessDetailInLogFile(httpRequest);
        }
    }

    /**
     * Write api access details in log file for anonymous user, could be tracked
     * by mixpanel distinct_id in future
     * 
     * @param httpRequest
     */
    private void putAccessDetailInLogFile(HttpServletRequest httpRequest) {
        /*
         * For anonymous user put details in log file
         */
        Cookie[] cookies = httpRequest.getCookies();
        JsonNode mixpanelCookieValue = null;
        String mixPanelDistinctId = "";
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().matches(MP_MIXPANEL_COOKIE_REGEX)) {
                    try {
                        mixpanelCookieValue = mapper.readTree(URLDecoder.decode(c.getValue(), "UTF-8"));
                        mixPanelDistinctId = mixpanelCookieValue.get(MIXPANEL_DISTINCT_ID).toString();
                        logger.debug(
                                "distinct_id:{}, uri:{}, user_agent:{}, user_ip:{}",
                                mixPanelDistinctId,
                                httpRequest.getRequestURI(),
                                httpRequest.getHeader("user-agent"),
                                httpRequest.getRemoteAddr());
                        break;
                    }
                    catch (Exception e) {
                        logger.error("Could not read mixpanel cookie", e);
                    }
                }
            }
        }
    }

    /**
     * Add access log details in internal data structure, that would be
     * processed by sceduler.
     * 
     * @param activeUser
     * @param request
     */
    private void addAccessLogToInternalDS(ActiveUser activeUser, HttpServletRequest request) {
        APIAccessLog accessLog = createUserAccessLogObj(activeUser, request);
        apiAccessLogToPersist.add(accessLog);
        int threshold = propertyreader.getRequiredPropertyAsType(PropertyKeys.ACCESS_LOG_INTERNAL_DS_SIZE_THRESHOLD, Integer.class);
        if (apiAccessLogToPersist.size() > threshold ) {
            // TODO need to run scheduled task based on some trigger like size > threshold
            logger.error(
                    "apiAccessLogToPersist size {} is more than threshold {}",
                    apiAccessLogToPersist.size(),
                    threshold);
        }
    }

    private APIAccessLog createUserAccessLogObj(ActiveUser activeUser, HttpServletRequest request) {
        APIAccessLog accessLog = new APIAccessLog();
        accessLog.setUserId(activeUser.getUserIdentifier());
        accessLog.setUserAgent(request.getHeader("user-agent"));
        accessLog.setUserIp(request.getRemoteAddr());
        accessLog.setEmail(activeUser.getUsername());
        accessLog.setAccessCount(1);
        accessLog.updateAccessHash();
        return accessLog;
    }

    @Scheduled(cron = "${api.access.scheduled.cron}")
    public void exceuteTask() {
        if (!schedulingEnabled) {
            logger.debug("api access log scheduling disabled");
            return;
        }
        logger.debug("Access log sceduler invoked with accesslog size {}", apiAccessLogToPersist.size());
        if (apiAccessLogToPersist.size() > 0) {
            synchronized (this) {
                APIAccessLog[] copy = new APIAccessLog[apiAccessLogToPersist.size()];
                copy = apiAccessLogToPersist.toArray(copy);
                /*
                 * reset internal list to save fresh access logs
                 */
                apiAccessLogToPersist = new ConcurrentLinkedQueue<APIAccessLog>();
                List<APIAccessLog> list = removeDuplicateEntries(copy);
                logger.error("Size after remove duplicates {}", list.size());
                saveOrUpdate(list);
            }
        }
    }

    /**
     * Save or update access log details in database
     * 
     * @param recentAccessLogList
     */
    private void saveOrUpdate(List<APIAccessLog> recentAccessLogList) {
        if (recentAccessLogList.size() > 0) {
            logger.debug("Save or update access log size {}", recentAccessLogList.size());
            List<APIAccessLog> existingAccessLogList = new ArrayList<>();
            List<APIAccessLog> newAccessLogList = new ArrayList<>();

            populateExistingAndNewAccessLogs(recentAccessLogList, existingAccessLogList, newAccessLogList);
            try {
                if (existingAccessLogList.size() > 0) {
                    apiAccessLogDao.save(existingAccessLogList);
                    logger.debug("Updated {} records", existingAccessLogList.size());
                }
            }
            catch (Exception e) {
                logger.error("Failed to update existing accesslogs of size {} {}", existingAccessLogList.size(), e);
            }
            try {
                if (newAccessLogList.size() > 0) {
                    apiAccessLogDao.save(newAccessLogList);
                    logger.debug("Created {} records", newAccessLogList.size());
                }
            }
            catch (Exception e) {
                logger.error("Failed to create new accesslogs of size {} {}", newAccessLogList.size(), e);
            }
        }

    }

    /**
     * populate list of existing and new to be created access log details
     * 
     * @param recentAccessLogList
     * @param existingAccessLogList
     * @param newAccessLogList
     */
    private void populateExistingAndNewAccessLogs(
            List<APIAccessLog> recentAccessLogList,
            List<APIAccessLog> existingAccessLogList,
            List<APIAccessLog> newAccessLogList) {
        List<String> hashList = new ArrayList<>();
        for (APIAccessLog log : recentAccessLogList) {
            hashList.add(log.getAccessHash());
            /*
             * find existing access log from database in batch
             */
            if (hashList.size() >= ACCESS_LOG_FETCH_BATCH_THRESHOLD) {
                // add existing access log from database in list
                existingAccessLogList.addAll(apiAccessLogDao.findByAccessHashIn(hashList));
                hashList.clear();
            }
        }
        existingAccessLogList.addAll(apiAccessLogDao.findByAccessHashIn(hashList));
        Map<String, APIAccessLog> hashToAccessLogMap = new HashMap<>();
        for (APIAccessLog log : existingAccessLogList) {
            hashToAccessLogMap.put(log.getAccessHash(), log);
        }
        for (APIAccessLog accessLog : recentAccessLogList) {
            APIAccessLog existingAccessLog = hashToAccessLogMap.get(accessLog.getAccessHash());
            if (existingAccessLog != null) {
                existingAccessLog.setAccessCount(existingAccessLog.getAccessCount() + accessLog.getAccessCount());
                existingAccessLogList.add(existingAccessLog);
            }
            else {
                newAccessLogList.add(accessLog);
            }
        }
    }

    /**
     * Remove duplicate entry from list of access log based on access hash
     * created. Even if any duplicate entry remains on the basis of unique
     * constraint (username,user_agent,userip) then that would be rejected from
     * database. For each duplicate access log, access count will be incremented
     * by 1.
     * 
     * @param apiAccessLogCopy
     * @return
     */
    private List<APIAccessLog> removeDuplicateEntries(APIAccessLog[] apiAccessLogCopy) {
        List<APIAccessLog> list = new ArrayList<APIAccessLog>();
        Map<String, APIAccessLog> hashToAccessLog = new HashMap<>();

        for (APIAccessLog accessLog : apiAccessLogCopy) {
            String hash = accessLog.getAccessHash();
            APIAccessLog accessLogInMap = hashToAccessLog.get(hash);
            if (accessLogInMap == null) {
                list.add(accessLog);
                hashToAccessLog.put(hash, accessLog);
            }
            else {
                accessLogInMap.setAccessCount(accessLogInMap.getAccessCount() + 1);
            }
        }
        hashToAccessLog = null;
        return list;
    }

    @ManagedOperation(description = "is scheduling enabled")
    public boolean isSchedulingEnabled() {
        return schedulingEnabled;
    }

    @ManagedOperation(description = "set scheduling enabled")
    @ManagedOperationParameters({ @ManagedOperationParameter(
            name = "schedulingEnabled",
            description = "boolean value true/false") })
    public void setSchedulingEnabled(boolean schedulingEnabled) {
        this.schedulingEnabled = schedulingEnabled;
    }
    
    @ManagedOperation(description = "Reset access log container to empty")
    public void resetAPIAccessLogContainer(){
        apiAccessLogToPersist = new ConcurrentLinkedQueue<>();
    }

}
