package com.proptiger.data.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.proptiger.core.util.IPUtils;
import com.proptiger.core.util.PropertyKeys;
import com.proptiger.core.util.PropertyReader;
import com.proptiger.data.util.lead.CookieConstants;

@Service
public class CookiesService {

    private int cookieExpiryPeriod;

    @PostConstruct
    public void init() {
        cookieExpiryPeriod = PropertyReader.getRequiredPropertyAsType(PropertyKeys.COOKIE_EXPIRY_PERIOD, Integer.class);
    }

    // /every cookie should be set to empty if not present
    public void setCookies(HttpServletRequest request, HttpServletResponse response) {

        String landingPage = null;
        String utmz = null;
        String httpReferer = null;
        String network = null;
        String utmSourceFromRequest = null;

        // ppc to be set
        boolean ppc = false;

        // Current ppc cookie of user
        boolean originalPPC = false;

        // Reading request Cookies
        Cookie[] requestCookies = request.getCookies();

        if (requestCookies != null) {
            for (Cookie c : requestCookies) {

                if (c.getName().equals(CookieConstants.USER_FROM_PPC)) {
                    originalPPC = "TRUE".equalsIgnoreCase(c.getValue());
                    ppc = originalPPC;
                }
                else if (c.getName().equals(CookieConstants.LANDING_PAGE)) {
                    landingPage = c.getValue();
                }
                else if (c.getName().equals(CookieConstants.UTMZ)) {
                    utmz = c.getValue();
                }
            }
        }

        // utmsource from request parameter
        utmSourceFromRequest = request.getParameter(CookieConstants.UTM_SOURCE);

        if (utmSourceFromRequest != null && !utmSourceFromRequest.isEmpty()) {

            // setting USER_NETWORK cookie
            network = request.getParameter(CookieConstants.NETWORK);
            if (network != null) {
                setCookie(CookieConstants.USER_NETWORK, network.toLowerCase(), response);
            }

            // Setting utm cookies
            setUTMCookies(utmz, request, response);

            // setting REF_URL cookie
            httpReferer = request.getHeader(CookieConstants.REFERER);
            if (httpReferer != null && !httpReferer.isEmpty()) {
                setCookie(CookieConstants.REF_URL, httpReferer, response);
            }

            setUserIp(request, response);
            setLandingPage(request, landingPage, response);

            // ppc set to true for Paid Traffic
            if (utmSourceFromRequest.equalsIgnoreCase("adwords") || utmSourceFromRequest.equalsIgnoreCase("adword")) {
                ppc = true;
            }
            // ppc evaluated for Non-Paid Traffic
            else {
                if (httpReferer != null && httpReferer.contains(CookieConstants.UTM_GCLID)) {
                    ppc = true;
                }
                else {
                    ppc = false;
                }
            }
        }

        // Override ppc cookie of user only if it was not true
        if (!originalPPC) {
            setCookie(CookieConstants.USER_FROM_PPC, String.valueOf(ppc).toUpperCase(), response);
        }
    }

    private void setUserIp(HttpServletRequest request, HttpServletResponse response) {
        String userIP = null;
        if (IPUtils.getClientIP(request) != null) {
            userIP = IPUtils.getClientIP(request);
        }
        setCookie(CookieConstants.USER_IP, userIP, response);
    }

    private void setLandingPage(HttpServletRequest request, String landingPage, HttpServletResponse response) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString != null) {
            requestURL.append('?').append(queryString).toString();
        }
        setCookie(CookieConstants.LANDING_PAGE, requestURL.toString(), response);
    }

    // First extract utm cookies from utmz cookie, if not present, extract them
    // from request parameters
    private void setUTMCookies(String utmz, HttpServletRequest request, HttpServletResponse response) {

        String utmAdgroup = null;
        String utmKeyword = null;
        String utmCampaign = null;
        String utmMedium = null;

        Map<String, String> utmzCookieMap = new HashMap<String, String>();

        if (utmz != null) {
            // get utm parameters from utmz cookie
            getUTMZCookieParams(utmz, utmzCookieMap);

            utmAdgroup = utmzCookieMap.get(CookieConstants.UTM_CSR);
            utmCampaign = utmzCookieMap.get(CookieConstants.UTM_CCN);
            utmKeyword = utmzCookieMap.get(CookieConstants.UTM_CTR);
            utmMedium = utmzCookieMap.get(CookieConstants.UTM_CMD);
        }
        // Fallback of utmAdgroup, utmCampaign, utmKeyword, utmMedium on
        // request parameters
        if (utmAdgroup == null) {
            utmAdgroup = request.getParameter(CookieConstants.UTM_ADGROUP);
        }

        if (utmCampaign == null) {
            utmCampaign = request.getParameter(CookieConstants.UTM_CAMPAIGN);
        }

        if (utmKeyword == null) {
            utmKeyword = request.getParameter(CookieConstants.UTM_TERM);
        }

        if (utmMedium == null) {
            utmMedium = request.getParameter(CookieConstants.UTM_MEDIUM);
        }

        if (utmAdgroup != null) {
            setCookie(CookieConstants.USER_ADGROUP, utmAdgroup, response);
        }
        if (utmAdgroup != null) {
            setCookie(CookieConstants.USER_CAMPAIGN, utmCampaign, response);
        }
        if (utmKeyword != null) {
            setCookie(CookieConstants.USER_KEYWORD, utmKeyword.toLowerCase(), response);
        }
        if (utmMedium != null) {
            setCookie(CookieConstants.USER_MEDIUM, utmMedium.toLowerCase(), response);
        }
    }

    public Map<String, String> getUTMZCookieParams(String utmz, Map<String, String> utmzCookieMap) {
        String[] utmzCookies = utmz.split("\\.", 5);
        String campaignData = utmzCookies[4];

        Map<String, String> utmParams = new HashMap<String, String>();

        // extracting from utmz cookies
        if (campaignData.contains("|")) {
            String[] pairs = campaignData.split("\\|");
            for (String pair : pairs) {
                String[] keyval = pair.split("=");
                utmParams.put(keyval[0], keyval[1]);
            }

            utmzCookieMap.put(CookieConstants.UTM_CCN, utmParams.get(CookieConstants.UTM_CCN));
            utmzCookieMap.put(CookieConstants.UTM_CTR, utmParams.get(CookieConstants.UTM_CTR));
            utmzCookieMap.put(CookieConstants.UTM_CMD, utmParams.get(CookieConstants.UTM_CMD));
            utmzCookieMap.put(CookieConstants.UTM_CSR, utmParams.get(CookieConstants.UTM_CSR));
            utmzCookieMap.put(CookieConstants.UTM_GCLID, utmParams.get(CookieConstants.UTM_GCLID));
        }

        return utmzCookieMap;
    }

    public void setCookie(String cookieName, String cookieValue, HttpServletResponse response) {
        Cookie landingPageCookie = new Cookie(cookieName, cookieValue);
        landingPageCookie.setMaxAge(cookieExpiryPeriod);
        landingPageCookie.setPath("/");
        response.addCookie(landingPageCookie);
    }
}
