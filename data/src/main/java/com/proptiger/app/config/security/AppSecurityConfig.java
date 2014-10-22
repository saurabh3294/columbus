package com.proptiger.app.config.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import com.proptiger.api.filter.IPBasedAPIAccessFilter;
import com.proptiger.app.config.security.social.CustomSpringSocialConfigurer;
import com.proptiger.core.enums.security.UserRole;
import com.proptiger.data.service.security.OTPService;
import com.proptiger.data.util.Constants;

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
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailManagerService userService;

    @Autowired
    private DataSource               dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
         * to enable form login for testing purpose uncomment these start
         */
        // http.csrf().disable();
        // http.authorizeRequests().antMatchers("/data/v1/entity/user/**").authenticated().and().formLogin().successHandler(createAuthSuccessHandler());
        // //http.exceptionHandling().authenticationEntryPoint(createAuthEntryPoint());
        // http.addFilter(createUserNamePasswordLoginFilter());
        // http.logout().logoutSuccessHandler(createLogoutHanlder()).logoutUrl(LOGOUT_URL);
        /*
         * to enable form login for testing purpose uncomment these end and
         * comment below code
         */

        // http.requiresChannel().antMatchers(Constants.Security.USER_API_REGEX,
        // Constants.Security.AUTH_API_REGEX).requiresSecure();
        http.rememberMe().rememberMeServices(createPersistentTokenBasedRememberMeService())
                .key(Constants.Security.REMEMBER_ME_COOKIE);
        http.csrf().disable();

        http.authorizeRequests()
                .regexMatchers(Constants.Security.USER_DETAIL_API_REGEX).access("hasRole('" + UserRole.ADMIN_BACKEND.name() + "')")
                .regexMatchers(Constants.Security.USER_API_REGEX).access("hasRole('" + UserRole.USER.name() + "')")
                .regexMatchers(Constants.Security.USER_API_REGEX).access("hasRole('" + UserRole.ADMIN_BACKEND.name() + "')")
                .regexMatchers(Constants.Security.OTP_VALIDATE_API_REGEX)
                .access("hasRole('" + UserRole.PRE_AUTH_USER.name() + "')").anyRequest().permitAll();

        http.exceptionHandling().authenticationEntryPoint(createAuthEntryPoint());
        http.addFilter(createUserNamePasswordLoginFilter());
        http.addFilter(createRememberMeAuthFilter());
        http.logout().logoutSuccessHandler(createLogoutHanlder()).logoutUrl(Constants.Security.LOGOUT_URL)
                .addLogoutHandler(createLogoutHandler());

        http.apply(createSocialAuthConfigurer());
        http.addFilter(createConcurrentSessionFilter());
        http.exceptionHandling().accessDeniedHandler(createAccessDeniedHandler());
        http.addFilterBefore(createIPBasedAPIAccessFilter(), CustomUsernamePasswordAuthenticationFilter.class);
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
    public CustomSpringSocialConfigurer createSocialAuthConfigurer() {
        return new CustomSpringSocialConfigurer();
    }

    @Bean
    public LogoutSuccessHandler createLogoutHanlder() {
        return new ApiLogoutSuccessHandler();
    }

    @Bean
    public LoginUrlAuthenticationEntryPoint createAuthEntryPoint() {
        AuthEntryPoint authEntryPoint = new AuthEntryPoint(Constants.Security.LOGIN_URL);
        return authEntryPoint;
    }

    @Bean
    public AuthSuccessHandler createAuthSuccessHandler() {
        return new AuthSuccessHandler();
    }

    @Bean
    public GenericFilterBean createUserNamePasswordLoginFilter() throws Exception {
        UsernamePasswordAuthenticationFilter loginFilter = new CustomUsernamePasswordAuthenticationFilter(
                createOTPService());
        loginFilter.setSessionAuthenticationStrategy(createSessionStrategy());
        loginFilter.setAuthenticationManager(createAuthenticationManager());
        loginFilter.setAuthenticationFailureHandler(createAuthFailureHandler());
        loginFilter.setAuthenticationSuccessHandler(createAuthSuccessHandler());
        loginFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(
                Constants.Security.LOGIN_URL,
                "POST"));
        loginFilter.setUsernameParameter(Constants.Security.USERNAME_PARAMETER_NAME);
        loginFilter.setPasswordParameter(Constants.Security.PASSWORD_PARAMETER_NAME);
        loginFilter.setPostOnly(true);

        loginFilter.setRememberMeServices(createPersistentTokenBasedRememberMeService());
        return loginFilter;
    }

    @Bean
    public OTPService createOTPService() {
        return new OTPService();
    }

    @Bean
    public SessionAuthenticationStrategy createSessionStrategy() {
        List<SessionAuthenticationStrategy> delegateStrategies = new ArrayList<>();
        delegateStrategies.add(createConcurrentSessionControlAuthenticationStrategy());
        delegateStrategies.add(new RegisterSessionAuthenticationStrategy(createSessionRegistry()));
        CompositeSessionAuthenticationStrategy sessionControlStrategy = new CompositeSessionAuthenticationStrategy(
                delegateStrategies);
        return sessionControlStrategy;
    }

    @Bean
    public ConcurrentSessionControlAuthenticationStrategy createConcurrentSessionControlAuthenticationStrategy() {
        ConcurrentSessionControlAuthenticationStrategy authenticationStrategy = new CustomConcurrentSessionControlAuthenticationStrategy(
                createSessionRegistry());
        // -1 meaning unlimited session allowed for a user
        authenticationStrategy.setMaximumSessions(-1);
        return authenticationStrategy;
    }

    @Bean
    public Filter createConcurrentSessionFilter() {
        SessionRegistry sessionRegistry = createSessionRegistry();
        ConcurrentSessionFilter cf = new ConcurrentSessionFilter(sessionRegistry, Constants.Security.LOGIN_URL);
        cf.setLogoutHandlers(new LogoutHandler[] { createLogoutHandler() });
        cf.setRedirectStrategy(new SessionExpiryStrategyHandler());
        return cf;
    }

    @Bean
    public SessionRegistry createSessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public AuthenticationManager createAuthenticationManager() {
        List<AuthenticationProvider> authProvider = new ArrayList<>();
        DaoAuthenticationProvider daoAuthProvider = createDaoAuthProvider();
        authProvider.add(daoAuthProvider);

        authProvider.add(createRememberMeAuthProvider());
        return new ProviderManager(authProvider);
    }
    @Bean
    public DaoAuthenticationProvider createDaoAuthProvider() {
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
        daoAuthProvider.setPostAuthenticationChecks(createPostAuthCheckProvider());
        daoAuthProvider.setUserDetailsService(userService);
        daoAuthProvider.setPasswordEncoder(createPasswordEncoder());
        return daoAuthProvider;
    }

    @Bean
    public UserDetailsChecker createPostAuthCheckProvider() {
        UserDetailsChecker userDetailsChecker = new PostAuthenticationCheck();
        return userDetailsChecker;
    }

    @Bean
    public Md5PasswordEncoder createPasswordEncoder() {
        return new Md5PasswordEncoder();
    }

    @Bean
    public RememberMeAuthenticationProvider createRememberMeAuthProvider() {
        return new RememberMeAuthenticationProvider(Constants.Security.API_SECRET_KEY);
    }

    @Bean
    public GenericFilterBean createRememberMeAuthFilter() {
        CustomRememberMeAuthFilter rememberMeFilter = new CustomRememberMeAuthFilter(
                createAuthenticationManager(),
                createPersistentTokenBasedRememberMeService());
        rememberMeFilter.setSessionStrategy(createSessionStrategy());
        return rememberMeFilter;
    }

    @Bean
    public PersistentTokenBasedRememberMeServices createPersistentTokenBasedRememberMeService() {
        PersistentTokenBasedRememberMeServices tokenBasedRememberMeService = new PersistentTokenBasedRememberMeServices(
                Constants.Security.API_SECRET_KEY,
                userService,
                createPersistentLoginRepository());
        tokenBasedRememberMeService.setParameter(Constants.Security.REMEMBER_ME_PARAMETER);
        tokenBasedRememberMeService.setCookieName(Constants.Security.REMEMBER_ME_COOKIE);
        tokenBasedRememberMeService.setTokenValiditySeconds(Constants.Security.REMEMBER_ME_COOKIE_VALIDITY);
        return tokenBasedRememberMeService;
    }

    @Bean
    public JdbcTokenRepositoryImpl createPersistentLoginRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public AuthenticationFailureHandler createAuthFailureHandler() {
        AuthFailureHandler authFailureHandler = new AuthFailureHandler();
        return authFailureHandler;
    }
}
