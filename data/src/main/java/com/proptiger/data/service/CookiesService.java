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
        String httpReferer = null;
        String network = null;
        String utmSourceFromRequest = null;

        // ppc to be set
        boolean ppc = false;

        // Current ppc cookie of user
        boolean originalPPC = false;

        utmSourceFromRequest = request.getParameter(CookieConstants.UTM_SOURCE);

        // Reading request Cookies
        Cookie[] requestCookies = request.getCookies();

        Map<String, String> requestCookiesMap = new HashMap<String, String>();

        if (requestCookies != null) {
            for (Cookie cookie : requestCookies) {
                requestCookiesMap.put(cookie.getName(), cookie.getValue());
                if (cookie.getName().equals(CookieConstants.USER_FROM_PPC)) {
                    originalPPC = CookieConstants.PPC_TRUE.equalsIgnoreCase(cookie.getValue());
                    ppc = originalPPC;
                }
                else if (cookie.getName().equals(CookieConstants.UTMA)) {
                    cookiesMap.put(CookieConstants.UTMA, cookie.getValue());
                }
                else if (cookie.getName().equals(CookieConstants.LANDING_PAGE)) {
                    landingPage = cookie.getValue();
                }
            }
        }

        // Override cookies only if USER_FROM_PPC is not true
        if (!originalPPC) {

            // Setting user_network cookie
            network = setCookieWithDueOrder(
                    CookieConstants.NETWORK,
                    CookieConstants.USER_NETWORK,
                    request,
                    requestCookiesMap);
            if (network != null) {
                setCookie(CookieConstants.USER_NETWORK, network.toLowerCase(), response, cookiesMap);
            }

            // Setting utm cookies
            setUTMCookies(request, response, requestCookiesMap, cookiesMap);

            // setting REF_URL cookie
            httpReferer = request.getHeader(CookieConstants.REFERER);
            if (httpReferer != null && !httpReferer.isEmpty()) {
                setCookie(CookieConstants.REF_URL, httpReferer, response, cookiesMap);
            }

            // ppc set to true if utm_medium is 'ppc' or 'cpc'
            if (CookieConstants.CPC.equalsIgnoreCase(cookiesMap.get(CookieConstants.USER_MEDIUM)) || CookieConstants.PPC
                    .equalsIgnoreCase(cookiesMap.get(CookieConstants.USER_MEDIUM))) {
                ppc = true;
            }

            // ppc set to true if utm_source is 'adwords' or 'adword'
            else if (utmSourceFromRequest != null && (utmSourceFromRequest.equalsIgnoreCase("adwords") || utmSourceFromRequest
                    .equalsIgnoreCase("adword"))) {
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

            setCookie(CookieConstants.USER_FROM_PPC, String.valueOf(ppc).toUpperCase(), response, cookiesMap);
        }

        // else just add the request cookies to cookies map
        else {

            for (Cookie cookie : requestCookies) {
                cookiesMap.put(cookie.getName(), cookie.getValue());
            }
        }

        setUserIp(request, response, cookiesMap);
        setLandingPage(request, landingPage, response, cookiesMap);

        return cookiesMap;
    }

    private void setUserIp(HttpServletRequest request, HttpServletResponse response, Map<String, String> cookiesMap) {
        String userIP = null;
        if (request.getHeader("IP") != null) {
            userIP = request.getHeader("IP");
        }
        else if (IPUtils.getClientIP(request) != null) {
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

    // First extract utm cookies from request parameters, then original cookies
    // and then extract them from utmz cookie
    private void setUTMCookies(
            HttpServletRequest request,
            HttpServletResponse response,
            Map<String, String> requestCookiesMap,
            Map<String, String> cookiesMap) {

        String utmAdgroup = null;
        String utmKeyword = null;
        String utmCampaign = null;
        String utmMedium = null;
        String utmSource = null;
        String utmGclid = null;
        String utmz = requestCookiesMap.get(CookieConstants.UTMZ);

        Map<String, String> utmzCookieMap = new HashMap<String, String>();

        utmAdgroup = setCookieWithDueOrder(
                CookieConstants.UTM_ADGROUP,
                CookieConstants.USER_ADGROUP,
                request,
                requestCookiesMap);
        utmCampaign = setCookieWithDueOrder(
                CookieConstants.UTM_CAMPAIGN,
                CookieConstants.USER_CAMPAIGN,
                request,
                requestCookiesMap);
        utmKeyword = setCookieWithDueOrder(
                CookieConstants.UTM_TERM,
                CookieConstants.USER_KEYWORD,
                request,
                requestCookiesMap);
        utmMedium = setCookieWithDueOrder(
                CookieConstants.UTM_MEDIUM,
                CookieConstants.USER_MEDIUM,
                request,
                requestCookiesMap);
        utmSource = setCookieWithDueOrder(
                CookieConstants.UTM_SOURCE,
                CookieConstants.USER_FROM,
                request,
                requestCookiesMap);
        utmGclid = setCookieWithDueOrder(CookieConstants.UTM_GCLID, null, request, requestCookiesMap);

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
            utmMedium = CookieConstants.CPC;
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

    public String setCookieWithDueOrder(
            String parameterName,
            String cookieName,
            HttpServletRequest request,
            Map<String, String> requestCookiesMap) {
        String cookieValue = null;
        if (request.getParameter(parameterName) != null) {
            cookieValue = request.getParameter(parameterName);
        }
        else if (requestCookiesMap.get(cookieName) != null) {
            cookieValue = requestCookiesMap.get(cookieName);
        }
        return cookieValue;
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
