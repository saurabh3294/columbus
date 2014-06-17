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
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

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
    private CustomUserDetailService userDetailService;
    @Autowired
    private DataSource              dataSource;

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
        http.rememberMe().rememberMeServices(createPersistentTokenBasedRememberMeService())
                .key(Constants.Security.REMEMBER_ME_COOKIE);
        http.csrf().disable();
        http.authorizeRequests().regexMatchers(Constants.Security.USER_API_REGEX, Constants.Security.AUTH_API_REGEX).authenticated();
        http.exceptionHandling().authenticationEntryPoint(createAuthEntryPoint());
        http.addFilter(createUserNamePasswordLoginFilter());
        http.addFilter(createRememberMeAuthFilter());
        http.logout().logoutSuccessHandler(createLogoutHanlder()).logoutUrl(Constants.Security.LOGOUT_URL)
                .deleteCookies(Constants.Security.COOKIE_NAME_JSESSIONID, Constants.Security.REMEMBER_ME_COOKIE);
    }

    @Bean
    public LogoutSuccessHandler createLogoutHanlder() {
        return new ApiLogoutSuccessHandler();
    }

    @Bean
    public LoginUrlAuthenticationEntryPoint createAuthEntryPoint() {
        AuthEntryPoint authEntryPoint = new AuthEntryPoint(Constants.Security.LOGIN_URL);
        // authEntryPoint.setForceHttps(true);
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
        return new RememberMeAuthenticationProvider(Constants.Security.API_SECRET_KEY);
    }

    @Bean
    public GenericFilterBean createRememberMeAuthFilter() {
        RememberMeAuthenticationFilter rememberMeFilter = new RememberMeAuthenticationFilter(
                createAuthenticationManager(),
                createPersistentTokenBasedRememberMeService());
        rememberMeFilter.setAuthenticationSuccessHandler(createAuthSuccessHandler());
        return rememberMeFilter;
    }

    @Bean
    public PersistentTokenBasedRememberMeServices createPersistentTokenBasedRememberMeService() {
        PersistentTokenBasedRememberMeServices tokenBasedRememberMeService = new PersistentTokenBasedRememberMeServices(
                Constants.Security.API_SECRET_KEY,
                userDetailService,
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
