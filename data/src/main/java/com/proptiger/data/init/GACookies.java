package com.proptiger.data.init;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DurationFormatUtils;

import com.proptiger.data.model.Enquiry;

public class GACookies {

    public void setGACookies(Enquiry enquiry, Map<String, String>cookieMap) {

        String campaignSource = null;
        String campaignName = null;
        String campaignMedium = null;
        String campaignTerm = null;
        String campaignContent = null;
        String randomid = null;
        
        if (cookieMap.containsKey("__utma") && cookieMap.containsKey("__utmz")) {

            // Parse __utmz cookie
        String[] utmzCookies = cookieMap.get("__utmz").split("\\.", 5);
        String campaignData = utmzCookies[4];

        Map<String, String> fbParam = new HashMap<String, String>();

        if (campaignData.contains("|")) {
            String[] pairs = campaignData.split("\\|");
            for (String pair : pairs) {
                String[] keyval = pair.split("=");
                fbParam.put(keyval[0], keyval[1]);
            }

            /*
             * You should tag you campaigns manually to have a full view of your
             * adwords campaigns data.
             */
            campaignSource = fbParam.get("utmcsr");
            campaignName = fbParam.get("utmccn");
            campaignMedium = fbParam.get("utmcmd");
            campaignTerm = fbParam.get("utmctr");
            campaignContent = fbParam.get("utmcct");

            if (fbParam.containsKey("utmgclid")) {
                campaignSource = "google";
                campaignMedium = "cpc";
                campaignTerm = fbParam.get("utmctr");
            }
        }
        // Parse the __utma Cookie
        String[] utmaCookies = cookieMap.get("__utma").split("\\.");
        randomid = utmaCookies[1];

        DateFormat format = new SimpleDateFormat("Y-m-d H:m:s");
        // format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        long currentTime = System.currentTimeMillis() / 1000l;
        long timeSpent = (currentTime - Long.parseLong(utmaCookies[4])) * 1000;
        String dateString = DurationFormatUtils.formatDuration(timeSpent, "'0-0-'d' 'H':'m':'s");
        enquiry.setGaTimespent(dateString); // wrong
        }

        if (cookieMap.get("USER_NETWORK") != null) {
            enquiry.setGaNetwork(cookieMap.get("USER_NETWORK").toLowerCase().trim());
            // logMsg(TRACE, "in lead.php network is: " .$ga_network);
        }

        enquiry.setGaSource(campaignSource);
        enquiry.setGaMedium(campaignMedium);
        enquiry.setGaKeywords(campaignTerm);
        enquiry.setGaCampaign(campaignName);
        enquiry.setGaUserId(randomid);
        
        if (enquiry.getGaMedium() != null && (enquiry.getGaMedium().toLowerCase().trim().equals("ppc") || enquiry
                .getGaMedium().toLowerCase().trim().equals("cpc")
                || enquiry.getGaMedium().toLowerCase().trim().equals("external mailer")
                || enquiry.getGaMedium().toLowerCase().trim().equals("externalmailer")
                || enquiry.getGaMedium().toLowerCase().trim().equals("mailer external")
                || enquiry.getGaMedium().toLowerCase().trim().equals("mailerexternal")
                || enquiry.getGaMedium().toLowerCase().trim().equals("banner") || !enquiry.getGaSource().toLowerCase()
                .trim().equals("banner_ad"))) {
            enquiry.setPpc(true);
            enquiry.setGaPpc(1);
        }
        else {
            enquiry.setPpc(false);
            enquiry.setGaPpc(0);
        }
        // can be set before
        // setting to ''
        if (campaignSource == null) {
            enquiry.setGaSource(enquiry.getSource());
        }
        if (campaignMedium == null) {
            enquiry.setGaMedium(enquiry.getUserMedium());
        }
    }

}
