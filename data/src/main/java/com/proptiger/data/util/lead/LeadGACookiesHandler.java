package com.proptiger.data.util.lead;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.data.service.CookiesService;

public class LeadGACookiesHandler {

    @Autowired
    CookiesService cookiesService;

    public void setGACookies(Enquiry enquiry, Map<String, String> cookieMap) {
        String campaignSource = null;
        String campaignName = null;
        String campaignMedium = null;
        String campaignTerm = null;
        String randomId = null;
        String gaPpcActiveDBEnum = "1";
        String gaPpcInactiveDBEnum = "0";

        if (cookieMap.containsKey(CookieConstants.UTMA) && cookieMap.containsKey(CookieConstants.UTMZ)) {

            // Parse __utmz cookie
            Map<String, String> utmzCookieMap = new HashMap<String, String>();
            cookiesService.getUTMZCookieParams(cookieMap.get(CookieConstants.UTMZ), utmzCookieMap);

            campaignSource = utmzCookieMap.get(CookieConstants.UTM_CSR);
            campaignName = utmzCookieMap.get(CookieConstants.UTM_CCN);
            campaignMedium = utmzCookieMap.get(CookieConstants.UTM_CMD);
            campaignTerm = utmzCookieMap.get(CookieConstants.UTM_CTR);

            if (utmzCookieMap.containsKey(CookieConstants.UTM_GCLID)) {
                campaignSource = "google";
                campaignMedium = "cpc";
            }

            // Parse the __utma Cookie
            String[] utmaCookies = cookieMap.get(CookieConstants.UTMA).split("\\.");
            randomId = utmaCookies[1];

            long currentTime = System.currentTimeMillis() / 1000l;
            long timeSpent = (currentTime - Long.parseLong(utmaCookies[4])) * 1000;
            String dateString = DurationFormatUtils.formatDuration(timeSpent, "'0-0-'d' 'H':'m':'s");
            enquiry.setGaTimespent(dateString);
        }

        if (cookieMap.get(CookieConstants.USER_NETWORK) != null) {
            enquiry.setGaNetwork(cookieMap.get(CookieConstants.USER_NETWORK).toLowerCase().trim());
        }

        if (campaignSource != null) {
            enquiry.setGaSource(campaignSource);
        }
        else {
            enquiry.setGaSource(enquiry.getSource());
        }
        
        enquiry.setGaMedium(campaignMedium);
        enquiry.setGaUserId(randomId);

        if (campaignTerm != null) {
            enquiry.setGaKeywords(campaignTerm);
        }

        if (campaignName != null) {
            enquiry.setGaCampaign(campaignName);
        }

        if (enquiry.getGaMedium() != null && (enquiry.getGaMedium().toLowerCase().trim().equals(CookieConstants.PPC) || enquiry
                .getGaMedium().toLowerCase().trim().equals(CookieConstants.CPC))) {
            enquiry.setPpc(true);
            enquiry.setGaPpc(gaPpcActiveDBEnum);
        }
        else {
            enquiry.setPpc(false);
            enquiry.setGaPpc(gaPpcInactiveDBEnum);
        }
        if(campaignMedium == null) {
            enquiry.setGaMedium(enquiry.getUserMedium());
        }

    }
}