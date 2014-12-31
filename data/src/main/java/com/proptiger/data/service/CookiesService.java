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

    public Map<String, String> setCookies(HttpServletRequest request, HttpServletResponse response) {

        // In addition to response object, cookies are added to this map to be
        // consumed by Enquiry
        Map<String, String> cookiesMap = new HashMap<String, String>();

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
                    cookiesMap.put(CookieConstants.UTMZ, utmz);
                }
                else if (c.getName().equals(CookieConstants.UTMA)) {
                    cookiesMap.put(CookieConstants.UTMA, c.getValue());
                }
            }
        }

        // utmsource from request parameter
        utmSourceFromRequest = request.getParameter(CookieConstants.UTM_SOURCE);

        if (utmSourceFromRequest != null && !utmSourceFromRequest.isEmpty()) {

            // setting USER_NETWORK cookie
            network = request.getParameter(CookieConstants.NETWORK);
            if (network != null) {
                setCookie(CookieConstants.USER_NETWORK, network.toLowerCase(), response, cookiesMap);
            }

            // Setting utm cookies
            setUTMCookies(utmz, request, response, cookiesMap);

            // setting REF_URL cookie
            httpReferer = request.getHeader(CookieConstants.REFERER);
            if (httpReferer != null && !httpReferer.isEmpty()) {
                setCookie(CookieConstants.REF_URL, httpReferer, response, cookiesMap);
            }

            setUserIp(request, response, cookiesMap);
            setLandingPage(request, landingPage, response, cookiesMap);

            // ppc set to true if utm_medium is 'ppc' or 'cpc'
            if (cookiesMap.get(CookieConstants.USER_MEDIUM) != null && (cookiesMap.get(CookieConstants.USER_MEDIUM) != CookieConstants.CPC || cookiesMap
                    .get(CookieConstants.USER_MEDIUM) != CookieConstants.PPC)) {
                ppc = true;
            }

            // ppc set to true if utm_source is 'adwords' or 'adword'
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
            setCookie(CookieConstants.USER_FROM_PPC, String.valueOf(ppc).toUpperCase(), response, cookiesMap);
        }
        return cookiesMap;
    }

    private void setUserIp(HttpServletRequest request, HttpServletResponse response, Map<String, String> cookiesMap) {
        String userIP = null;
        if (IPUtils.getClientIP(request) != null) {
            userIP = IPUtils.getClientIP(request);
        }
        setCookie(CookieConstants.USER_IP, userIP, response, cookiesMap);
    }

    private void setLandingPage(
            HttpServletRequest request,
            String landingPage,
            HttpServletResponse response,
            Map<String, String> cookiesMap) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString != null) {
            requestURL.append('?').append(queryString).toString();
        }
        setCookie(CookieConstants.LANDING_PAGE, requestURL.toString(), response, cookiesMap);
    }

    // First extract utm cookies from request parameters, if not present,
    // extract them from utmz cookie
    private void setUTMCookies(
            String utmz,
            HttpServletRequest request,
            HttpServletResponse response,
            Map<String, String> cookiesMap) {

        String utmAdgroup = null;
        String utmKeyword = null;
        String utmCampaign = null;
        String utmMedium = null;
        String utmSource = null;
        String utmGclid = null;

        Map<String, String> utmzCookieMap = new HashMap<String, String>();

        // get utm parameters from request parameters
        utmAdgroup = request.getParameter(CookieConstants.UTM_ADGROUP);
        utmCampaign = request.getParameter(CookieConstants.UTM_CAMPAIGN);
        utmKeyword = request.getParameter(CookieConstants.UTM_TERM);
        utmMedium = request.getParameter(CookieConstants.UTM_MEDIUM);
        utmSource = request.getParameter(CookieConstants.UTM_SOURCE);
        utmGclid = request.getParameter(CookieConstants.UTM_GCLID);

        // Fallback of utmAdgroup, utmCampaign, utmKeyword, utmMedium on
        // utmz cookie
        if (utmz != null) {

            getUTMZCookieParams(utmz, utmzCookieMap);

            if (utmSource == null) {
                utmSource = utmzCookieMap.get(CookieConstants.UTM_CSR);
            }
            if (utmAdgroup == null) {
                utmAdgroup = utmzCookieMap.get(CookieConstants.UTM_CCN);
            }
            if (utmCampaign == null) {
                utmCampaign = utmzCookieMap.get(CookieConstants.UTM_CCN);
            }
            if (utmKeyword == null) {
                utmKeyword = utmzCookieMap.get(CookieConstants.UTM_CTR);
            }
            if (utmMedium == null) {
                utmMedium = utmzCookieMap.get(CookieConstants.UTM_CMD);
            }
            if (utmGclid == null) {
                utmGclid = utmzCookieMap.get(CookieConstants.UTM_GCLID);
            }
        }

        // Overriding utmsource and utmmedium if utmgclid present
        if (utmGclid != null) {
            utmSource = "google";
            utmMedium = "cpc";
        }

        if (utmSource != null) {
            setCookie(CookieConstants.USER_FROM, utmSource, response, cookiesMap);
        }
        if (utmAdgroup != null) {
            setCookie(CookieConstants.USER_ADGROUP, utmAdgroup, response, cookiesMap);
        }
        if (utmCampaign != null) {
            setCookie(CookieConstants.USER_CAMPAIGN, utmCampaign, response, cookiesMap);
        }
        if (utmKeyword != null) {
            setCookie(CookieConstants.USER_KEYWORD, utmKeyword.toLowerCase(), response, cookiesMap);
        }
        if (utmMedium != null) {
            setCookie(CookieConstants.USER_MEDIUM, utmMedium.toLowerCase(), response, cookiesMap);
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

    public void setCookie(
            String cookieName,
            String cookieValue,
            HttpServletResponse response,
            Map<String, String> cookiesMap) {
        Cookie landingPageCookie = new Cookie(cookieName, cookieValue);
        landingPageCookie.setMaxAge(cookieExpiryPeriod);
        landingPageCookie.setPath("/");
        response.addCookie(landingPageCookie);
        cookiesMap.put(cookieName, cookieValue);
    }
}
