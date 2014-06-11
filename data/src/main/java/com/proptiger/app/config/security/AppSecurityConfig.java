package com.proptiger.app.config.security;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

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

    private static final String     COOKIE_NAME_JSESSIONID  = "JSESSIONID";

    private static final String     LOGOUT_URL              = "/app/v1/logout";

    private static final String     PASSWORD_PARAMETER_NAME = "password";

    private static final String     USERNAME_PARAMETER_NAME = "username";

    private static final String     LOGIN_URL               = "/app/v1/login";

    private static final String     APP_REMEMBER_ME_KEY     = "auth-key";

    @Autowired
    private CustomUserDetailService userDetailService;
    @Autowired
    private DataSource              dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
         * to enable form login for testing purpose uncomment these start
         */
//         http.csrf().disable();
//         http.authorizeRequests().antMatchers("/data/v1/entity/user/**").authenticated().and().formLogin().successHandler(createAuthSuccessHandler());
//         //http.exceptionHandling().authenticationEntryPoint(createAuthEntryPoint());
//         http.addFilter(createUserNamePasswordLoginFilter());
//         http.logout().logoutSuccessHandler(createLogoutHanlder()).logoutUrl(LOGOUT_URL);
        /*
         * to enable form login for testing purpose uncomment these end and
         * comment below code
         */

        http.csrf().disable();
        http.authorizeRequests().regexMatchers(".*/user/.*").authenticated()/*.and().requiresChannel()
                .antMatchers(LOGIN_URL).requiresSecure().anyRequest().requiresInsecure()*/;
        http.exceptionHandling().authenticationEntryPoint(createAuthEntryPoint());
        http.headers().disable();
        http.addFilter(createUserNamePasswordLoginFilter());
        http.addFilter(createRememberMeAuthFilter());

        http.logout().logoutSuccessHandler(createLogoutHanlder()).logoutUrl(LOGOUT_URL)
                .deleteCookies(COOKIE_NAME_JSESSIONID);
    }

    @Bean
    public LogoutSuccessHandler createLogoutHanlder() {
        return new ApiLogoutSuccessHandler();
    }

    @Bean
    public AuthEntryPoint createAuthEntryPoint() {
        AuthEntryPoint authEntryPoint = new AuthEntryPoint();
        return authEntryPoint;
    }

    @Bean
    public AuthSuccessHandler createAuthSuccessHandler() {
        return new AuthSuccessHandler();
    }

    @Bean
    public GenericFilterBean createUserNamePasswordLoginFilter() throws Exception {
        UsernamePasswordAuthenticationFilter loginFilter = new UsernamePasswordAuthenticationFilter();
        loginFilter.setAuthenticationManager(createAuthenticationManager());
        loginFilter.setAuthenticationFailureHandler(createAuthFailureHandler());
        loginFilter.setAuthenticationSuccessHandler(createAuthSuccessHandler());
        loginFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(LOGIN_URL, "POST"));
        loginFilter.setUsernameParameter(USERNAME_PARAMETER_NAME);
        loginFilter.setPasswordParameter(PASSWORD_PARAMETER_NAME);
        loginFilter.setPostOnly(true);

        loginFilter.setRememberMeServices(createTokenBasedRememberMeService());
        return loginFilter;
    }

    @Bean
    public AuthenticationManager createAuthenticationManager() {
        List<AuthenticationProvider> authProvider = new ArrayList<>();
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
        daoAuthProvider.setUserDetailsService(userDetailService);
        daoAuthProvider.setPasswordEncoder(new Md5PasswordEncoder());
        authProvider.add(daoAuthProvider);

        authProvider.add(createRememberMeAuthProvider());
        return new ProviderManager(authProvider);
    }

    @Bean
    public RememberMeAuthenticationProvider createRememberMeAuthProvider() {
        return new RememberMeAuthenticationProvider(APP_REMEMBER_ME_KEY);
    }

    //@Bean
    public PersistentTokenBasedRememberMeServices createPersistentTokenBasedRememberMeService() {
        return new PersistentTokenBasedRememberMeServices(
                APP_REMEMBER_ME_KEY,
                userDetailService,
                createPersistentLoginRepository());
    }

    //@Bean
    public JdbcTokenRepositoryImpl createPersistentLoginRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public TokenBasedRememberMeServices createTokenBasedRememberMeService() {
        return new TokenBasedRememberMeServices(APP_REMEMBER_ME_KEY, userDetailService);
    }

    @Bean
    public GenericFilterBean createRememberMeAuthFilter() {
        RememberMeAuthenticationFilter filterBean = new RememberMeAuthenticationFilter(
                createAuthenticationManager(),
                createTokenBasedRememberMeService());
        return filterBean;
    }

    @Bean
    public AuthenticationFailureHandler createAuthFailureHandler() {
        AuthFailureHandler authFailureHandler = new AuthFailureHandler();
        return authFailureHandler;
    }
}
