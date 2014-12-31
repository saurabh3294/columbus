package com.proptiger.data.util.lead;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.core.util.IPUtils;

public class LeadCookiesHandler {

    private static Logger logger = LoggerFactory.getLogger(LeadCookiesHandler.class);

    public Map<String, String> setCookies(
            Enquiry enquiry,
            HttpServletRequest request,
            Map<String, String> requestCookiesMap) {

        Map<String, String> cookieMap = new HashMap<String, String>();

        if (request.getHeader(CookieConstants.REFERER) != null) {
            enquiry.setHttpReferer(request.getHeader(CookieConstants.REFERER));
        }

        if (enquiry.getResaleAndLaunchFlag() == null) {
            enquiry.setResaleAndLaunchFlag(request.getParameter(CookieConstants.RESALENLAUNCHFLAG));
        }

        // Set application source of lead
        if (enquiry.getApplicationType() == null) {
            UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
            ReadableUserAgent agent = parser.parse(request.getHeader(CookieConstants.USER_AGENT));
            String applicationSource = agent.getDeviceCategory().getName();

            if (!applicationSource.isEmpty() && (applicationSource.toLowerCase().equals("pda") || applicationSource
                    .toLowerCase().equals("smartphone"))) {
                enquiry.setApplicationType("Mobile Site");
            }

            else if (!applicationSource.isEmpty() && applicationSource.toLowerCase().equals("tablet")) {
                enquiry.setApplicationType("Tablet Site");
            }
            else {
                enquiry.setApplicationType("Desktop Site");
            }
        }

        for (String key : requestCookiesMap.keySet()) {
            switch (key) {
                case CookieConstants.LANDING_PAGE:
                    enquiry.setLandingPage(requestCookiesMap.get(key));
                    break;
                case CookieConstants.USER_CAMPAIGN:
                    enquiry.setCampaign(requestCookiesMap.get(key));
                    break;
                case CookieConstants.USER_ADGROUP:
                    enquiry.setAdGrp(requestCookiesMap.get(key));
                    break;
                case CookieConstants.USER_KEYWORD:
                    enquiry.setKeywords(requestCookiesMap.get(key));
                    break;
                case CookieConstants.USER_MEDIUM:
                    enquiry.setUserMedium(requestCookiesMap.get(key));
                    break;
                case CookieConstants.USER_FROM:
                    enquiry.setSource(requestCookiesMap.get(key));
                    break;
                default:
                    break;
            }
        }

        // utmz cookie parsed from header in case of enquiry via lead.php
        if (request.getHeader(CookieConstants.IP) != null) {
            String cookies = request.getHeader("Cookie");
            if (cookies != null) {
                Pattern urlPattern = Pattern.compile("__utmz=(.*?);");
                Matcher m = urlPattern.matcher(cookies);
                String utmzCookie = null;
                if (m.find()) {
                    utmzCookie = m.group(1);
                    try {

                        // to be done
                        cookieMap.put(
                                CookieConstants.UTMZ,
                                java.net.URLDecoder.decode(utmzCookie, CookieConstants.UTF_8));
                    }
                    catch (Exception exception) {
                        logger.error("Not able to decode Cookie", exception);
                    }
                }
            }
        }

        // IP header sent in case of enquiry via lead.php
        if (request.getHeader(CookieConstants.IP) != null) {
            enquiry.setIp(request.getHeader(CookieConstants.IP));
        }
        else if (IPUtils.getClientIP(request) != null) {
            enquiry.setIp(IPUtils.getClientIP(request));
        }

        return cookieMap;
    }
}
