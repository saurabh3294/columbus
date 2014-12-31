package com.proptiger.data.util.lead;

import java.util.Map;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.proptiger.core.model.proptiger.Enquiry;
import com.proptiger.data.service.CookiesService;

public class LeadGACookiesHandler {

    @Autowired
    CookiesService cookiesService;

    public void setGACookies(Enquiry enquiry, Map<String, String> cookiesMap) {
        String campaignSource = null;
        String campaignName = null;
        String campaignMedium = null;
        String campaignTerm = null;
        String randomId = null;
        String gaPpcActiveDBEnum = "1";
        String gaPpcInactiveDBEnum = "0";

        campaignSource = cookiesMap.get(CookieConstants.USER_FROM);
        campaignName = cookiesMap.get(CookieConstants.USER_CAMPAIGN);
        campaignMedium = cookiesMap.get(CookieConstants.USER_MEDIUM);
        campaignTerm = cookiesMap.get(CookieConstants.USER_KEYWORD);

        // Parse the __utma Cookie
        if (cookiesMap.get(CookieConstants.UTMA) != null) {
            String[] utmaCookies = cookiesMap.get(CookieConstants.UTMA).split("\\.");
            randomId = utmaCookies[1];
            long currentTime = System.currentTimeMillis() / 1000l;
            long timeSpent = (currentTime - Long.parseLong(utmaCookies[4])) * 1000;
            String dateString = DurationFormatUtils.formatDuration(timeSpent, "'0-0-'d' 'H':'m':'s");
            enquiry.setGaTimespent(dateString);
        }

        if (campaignMedium != null) {
            enquiry.setGaMedium(campaignMedium);
        }
        if (randomId != null) {
            enquiry.setGaUserId(randomId);
        }
        if (campaignSource != null) {
            enquiry.setGaSource(campaignSource);
        }
        if (campaignTerm != null) {
            enquiry.setGaKeywords(campaignTerm);
        }
        if (campaignName != null) {
            enquiry.setGaCampaign(campaignName);
        }

        if (cookiesMap.get(CookieConstants.USER_NETWORK) != null) {
            enquiry.setGaNetwork(cookiesMap.get(CookieConstants.USER_NETWORK).trim());
        }

        if (enquiry.getGaMedium() != null && (enquiry.getGaMedium().trim().equals(CookieConstants.PPC) || enquiry
                .getGaMedium().trim().equals(CookieConstants.CPC))) {
            enquiry.setPpc(true);
            enquiry.setGaPpc(gaPpcActiveDBEnum);
        }
        else {
            enquiry.setPpc(false);
            enquiry.setGaPpc(gaPpcInactiveDBEnum);
        }
    }
}