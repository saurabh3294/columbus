package com.proptiger.app.config;

import java.util.Date;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listner to print session id with access time data for debuging purpose
 * 
 * @author Rajeev Pandey
 *
 */
@WebListener
public class HttpSessionTracker implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionTracker.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.debug("@@Sess ID {} created at {}, last accessed {}", se.getSession().getId(), new Date(), new Date(se
                .getSession().getLastAccessedTime()));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        logger.debug(
                "@@Sess ID {} destroyed at {}, last accessed {}, create at {}",
                se.getSession().getId(),
                new Date(),
                new Date(se.getSession().getLastAccessedTime()),
                se.getSession().getCreationTime());
    }

}
