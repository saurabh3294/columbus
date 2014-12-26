package com.proptiger.data.util.lead;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.time.DurationFormatUtils;
import com.proptiger.core.model.proptiger.Enquiry;


public class LeadGACookiesHandler {

    private static final String UTMZ = "__utmz";
    private static final String UTMA = "__utma";

    public void setGACookies(Enquiry enquiry, Map<String, String> cookieMap) {
        String campaignSource = null;
        String campaignName = null;
        String campaignMedium = null;
        String campaignTerm = null;
        String randomId = null;
        String gaPpcActiveDBEnum = "1";
        String gaPpcInactiveDBEnum = "0";

        if (cookieMap.containsKey(UTMA) && cookieMap.containsKey(UTMZ)) {
            // Parse __utmz cookie
            String[] utmzCookies = cookieMap.get(UTMZ).split("\\.", 5);
            String campaignData = utmzCookies[4];

            Map<String, String> params = new HashMap<String, String>();

            if (campaignData.contains("|")) {
                String[] pairs = campaignData.split("\\|");
                for (String pair : pairs) {
                    String[] keyval = pair.split("=");
                    params.put(keyval[0], keyval[1]);
                }

                /*
                 * You should tag you campaigns manually to have a full view of
                 * your adwords campaigns data.
                 */
                campaignSource = params.get("utmcsr");
                campaignName = params.get("utmccn");
                campaignMedium = params.get("utmcmd");
                campaignTerm = params.get("utmctr");

                if (params.containsKey("utmgclid")) {
                    campaignSource = "google";
                    campaignMedium = "cpc";
                }
            }

            // Parse the __utma Cookie
            String[] utmaCookies = cookieMap.get(UTMA).split("\\.");
            randomId = utmaCookies[1];

            long currentTime = System.currentTimeMillis() / 1000l;
            long timeSpent = (currentTime - Long.parseLong(utmaCookies[4])) * 1000;
            String dateString = DurationFormatUtils.formatDuration(timeSpent, "'0-0-'d' 'H':'m':'s");
            enquiry.setGaTimespent(dateString);
        }

        if (cookieMap.get("USER_NETWORK") != null) {
            enquiry.setGaNetwork(cookieMap.get("USER_NETWORK").toLowerCase().trim());
        }
        else {
            enquiry.setGaNetwork("");
        }

        enquiry.setGaSource(campaignSource);
        enquiry.setGaMedium(campaignMedium);
        enquiry.setGaUserId(randomId);

        if (campaignTerm != null) {
            enquiry.setGaKeywords(campaignTerm);
        }
        else {
            enquiry.setGaKeywords("");
        }

        if (campaignName != null) {
            enquiry.setGaCampaign(campaignName);
        }
        else {
            enquiry.setGaCampaign("");
        }

        if (enquiry.getGaMedium() != null && (enquiry.getGaMedium().toLowerCase().trim().equals("ppc") || enquiry
                .getGaMedium().toLowerCase().trim().equals("cpc")
                || enquiry.getGaMedium().toLowerCase().trim().equals("external mailer")
                || enquiry.getGaMedium().toLowerCase().trim().equals("externalmailer")
                || enquiry.getGaMedium().toLowerCase().trim().equals("mailer external")
                || enquiry.getGaMedium().toLowerCase().trim().equals("mailerexternal")
                || (enquiry.getGaMedium().toLowerCase().trim().equals("banner") && !enquiry.getGaSource().toLowerCase()
                .trim().equals("banner_ad")))) {
            enquiry.setPpc(true);
            enquiry.setGaPpc(gaPpcActiveDBEnum);
        }
        else {
            enquiry.setPpc(false);
            enquiry.setGaPpc(gaPpcInactiveDBEnum);
        }

        if (campaignSource == null) {
            enquiry.setGaSource(enquiry.getSource());
        }
        if (campaignMedium == null) {
            enquiry.setGaMedium(enquiry.getUserMedium());
        }
    }

}