package com.proptiger.data.util.lead;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.core.util.IPUtils;

public class LeadCookiesHandler {

    public HashMap<String, String> setCookies(Enquiry enquiry, HttpServletRequest request) {

        HashMap<String, String> cookieMap = new HashMap<String, String>();
        Cookie[] requestCookies = request.getCookies();

        if (request.getHeader("Referer") != null) {
            enquiry.setHttpReferer(request.getHeader("Referer"));
        }
        else {
            enquiry.setHttpReferer("");
        }
        if (enquiry.getResaleAndLaunchFlag() == null) {
            enquiry.setResaleAndLaunchFlag(request.getParameter("resaleNlaunchFlg"));
        }

        // Set application source of lead
        if (enquiry.getApplicationType() == null) {
            UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
            ReadableUserAgent agent = parser.parse(request.getHeader("User-Agent"));
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

        if (requestCookies != null) {
            for (Cookie c : requestCookies) {
                try {
                    cookieMap.put(c.getName(), URLDecoder.decode(c.getValue(), "UTF-8"));
                    c.setValue(URLDecoder.decode(c.getValue(), "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (c.getName().equals("LANDING_PAGE")) {
                    enquiry.setLandingPage(c.getValue());
                }
                else if (c.getName().equals("USER_CAMPAIGN")) {
                    enquiry.setCampaign(c.getValue());
                }
                else if (c.getName().equals("USER_ADGROUP")) {
                    enquiry.setAdGrp(c.getValue());
                }
                else if (c.getName().equals("USER_KEYWORD")) {
                    enquiry.setKeywords(c.getValue());
                }
                else if (c.getName().equals("USER_FROM")) {
                    enquiry.setSource(c.getValue());
                }
                else if (c.getName().equals("USER_ID")) {
                    enquiry.setUser(c.getValue());
                }
                else if (c.getName().equals("USER_MEDIUM")) {
                    enquiry.setUserMedium(c.getValue());
                }
            }
        }

        if (request.getHeader("IP") == null) {
            String cookies = request.getHeader("Cookie");
            Pattern urlPattern = Pattern.compile("__utmz=(.*?);");
            Matcher m = urlPattern.matcher(cookies);
            String utmzCookie = null;
            if (m.find()) {
                utmzCookie = m.group(1);
                try {
                    cookieMap.put("__utmz", java.net.URLDecoder.decode(utmzCookie, "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        if (request.getHeader("IP") != null) {
            enquiry.setIp(request.getHeader("IP"));
        }
        else if (IPUtils.getClientIP(request) != null) {
            enquiry.setIp(IPUtils.getClientIP(request));
        }
        else {
            enquiry.setIp("");
        }

        if (enquiry.getUserMedium() == null) {
            enquiry.setUserMedium("");
        }
        if (enquiry.getUser() == null) {
            enquiry.setUser("");
        }
        if (enquiry.getSource() == null) {
            enquiry.setSource("");
        }
        if (enquiry.getKeywords() == null) {
            enquiry.setKeywords("");
        }
        if (enquiry.getAdGrp() == null) {
            enquiry.setAdGrp("");
        }
        if (enquiry.getLandingPage() == null) {
            enquiry.setLandingPage("");
        }
        if (enquiry.getCampaign() == null) {
            enquiry.setCampaign("");
        }

        return cookieMap;
    }

}
