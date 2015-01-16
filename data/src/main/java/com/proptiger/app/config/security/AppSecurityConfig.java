package com.proptiger.app.config.security;

import javax.servlet.Filter;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSession;
import org.springframework.session.web.http.SessionRepositoryFilter;

import com.proptiger.api.filter.IPBasedAPIAccessFilter;
import com.proptiger.core.config.CustomAccessDeniedHandler;
import com.proptiger.core.enums.security.UserRole;
import com.proptiger.core.util.Constants;

/**
 * Application security configurations. Define url regex and handlers to handle
 * success and failure scenarios. Define filters to process the requests.
 * 
 * @author Rajeev Pandey
 * 
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "com.proptiger" })
@Order
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource         dataSource;

    @Autowired
    private RedisSerializer<?> jdkSerializer;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(createSessionRepositoryFilter(), ChannelProcessingFilter.class);
        http.csrf().disable();

        http.authorizeRequests().regexMatchers(Constants.Security.USER_ROLE_API_REGEX)
                .access("hasRole('" + UserRole.Admin.name() + "')")
                .regexMatchers(Constants.Security.USER_DETAIL_API_REGEX)
                .access("hasRole('" + UserRole.Admin.name() + "')").regexMatchers(Constants.Security.USER_API_REGEX)
                .access("hasRole('" + UserRole.USER.name() + "')").regexMatchers(Constants.Security.USER_API_REGEX)
                .access("hasRole('" + UserRole.Admin.name() + "')")
                .regexMatchers(Constants.Security.OTP_VALIDATE_API_REGEX)
                .access("hasRole('" + UserRole.PRE_AUTH_USER.name() + "')")
                .regexMatchers(Constants.Security.SUDO_USER_API_REGEX)
                .access("hasRole('" + UserRole.Admin.name() + "')").anyRequest().permitAll();

        http.exceptionHandling().authenticationEntryPoint(createAuthEntryPoint());

        http.exceptionHandling().accessDeniedHandler(createAccessDeniedHandler());
    }

    /**
     * Create session repository filter, that will create session object instead
     * of container
     * 
     * @return
     */
    @Bean(name = "springSessionRepositoryFilter")
    public SessionRepositoryFilter<? extends ExpiringSession> createSessionRepositoryFilter() {
        final SessionRepositoryFilter<MapSession> sessionRepositoryFilter = new SessionRepositoryFilter<MapSession>(
                createSessionRepository());
        DoNothingCookieSessionStrategy httpSessionStrategy = new DoNothingCookieSessionStrategy();
        sessionRepositoryFilter.setHttpSessionStrategy(httpSessionStrategy);
        return sessionRepositoryFilter;
    }

    @Bean
    public UserServiceSessionRepository createSessionRepository() {
        return new UserServiceSessionRepository();
    }

    @Bean
    public Filter createIPBasedAPIAccessFilter() {
        return new IPBasedAPIAccessFilter();
    }

    @Bean
    public AccessDeniedHandler createAccessDeniedHandler() {
        AccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
        return accessDeniedHandler;
    }

    @Bean
    public LogoutHandler createLogoutHandler() {
        return new CookieClearingLogoutHandler(
                Constants.Security.COOKIE_NAME_JSESSIONID,
                Constants.Security.REMEMBER_ME_COOKIE);
    }

    @Bean
    public LoginUrlAuthenticationEntryPoint createAuthEntryPoint() {
        AuthEntryPoint authEntryPoint = new AuthEntryPoint(Constants.Security.LOGIN_URL);
        return authEntryPoint;
    }

    @Bean
    public SessionRegistry createSessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public Md5PasswordEncoder createPasswordEncoder() {
        return new Md5PasswordEncoder();
    }
}
