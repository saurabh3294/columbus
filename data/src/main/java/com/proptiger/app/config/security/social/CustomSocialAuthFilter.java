package com.proptiger.app.config.security.social;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SocialAuthenticationServiceLocator;

import com.proptiger.app.config.security.ModifiableHttpServletRequest;
import com.proptiger.data.util.PropertyReader;

/**
 * Custom social authentication filter hack request to change for service
 * provider.
 * 
 * @author Rajeev Pandey
 *
 */
public class CustomSocialAuthFilter extends SocialAuthenticationFilter {

    private static final String                 SCOPE = "scope";
    private PropertyReader                      propertyReader;

    private UsersConnectionRepository           connectionRepository;

    private CustomJdbcUsersConnectionRepository customJdbcUsersConnectionRepository;

    public CustomSocialAuthFilter(
            PropertyReader propertyReader,
            AuthenticationManager authManager,
            UserIdSource userIdSource,
            UsersConnectionRepository usersConnectionRepository,
            SocialAuthenticationServiceLocator authServiceLocator) {
        super(authManager, userIdSource, usersConnectionRepository, authServiceLocator);
        this.propertyReader = propertyReader;
        this.connectionRepository = usersConnectionRepository;
        if (connectionRepository instanceof CustomJdbcUsersConnectionRepository) {
            customJdbcUsersConnectionRepository = (CustomJdbcUsersConnectionRepository) connectionRepository;
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        Authentication authentication = createAuthIfLoggedInUsingPHPCall(request);
        if (authentication != null) {
            /*
             * this flow is to make website or other clien work even without
             * posting on our app/v1/login/{provider} api. So if authentication
             * created then return it other wise normal flow should execute.
             */
            return authentication;
        }
        HttpServletRequest wrappedRequest = addScopeInRequestParameter(request);
        return super.attemptAuthentication(wrappedRequest, response);
    }

    private Authentication createAuthIfLoggedInUsingPHPCall(HttpServletRequest request) {
        // these string constants are as per defined in checkuser.php
        String provider = request.getParameter("provider");
        String providerUserId = request.getParameter("providerUserId");
        if (provider != null && providerUserId != null && !provider.isEmpty() && !providerUserId.isEmpty()) {
            if (customJdbcUsersConnectionRepository != null) {
                return customJdbcUsersConnectionRepository.createAuthenicationByProviderAndProviderUserId(
                        provider,
                        providerUserId);
            }

        }
        return null;
    }

    /**
     * Adding scope varibale in request parameter as that is mandatory for
     * google auth, for rest like facebook it may be empty
     * 
     * @param request
     * @return
     */
    private HttpServletRequest addScopeInRequestParameter(HttpServletRequest request) {
        String providerId = getProviderId(request);
        String scopeKey = providerId + "." + SCOPE;
        Map<String, String[]> extraParams = new TreeMap<String, String[]>();
        String[] values = { propertyReader.getRequiredProperty(scopeKey) };
        extraParams.put(SCOPE, values);
        HttpServletRequest wrappedRequest = new ModifiableHttpServletRequest(request, extraParams);
        return wrappedRequest;
    }

    /**
     * Default implementation copied from super class.
     * 
     * @param request
     * @return
     */
    @SuppressWarnings("deprecation")
    private String getProviderId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }

        // uri must start with context path
        uri = uri.substring(request.getContextPath().length());

        // remaining uri must start with filterProcessesUrl
        if (!uri.startsWith(getFilterProcessesUrl())) {
            return null;
        }
        uri = uri.substring(getFilterProcessesUrl().length());

        // expect /filterprocessesurl/provider, not /filterprocessesurlproviderr
        if (uri.startsWith("/")) {
            return uri.substring(1);
        }
        else {
            return null;
        }
    }
}
