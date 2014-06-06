package com.proptiger.app.config.security;

import org.springframework.security.web.context.*;

/**
 * Initialize spring security system
 * @author Rajeev Pandey
 *
 */
public class SecurityWebApplicationInitializer
      extends AbstractSecurityWebApplicationInitializer {

    public SecurityWebApplicationInitializer() {
        super(AppSecurityConfig.class);
    }
}