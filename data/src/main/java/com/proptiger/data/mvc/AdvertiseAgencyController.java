/**
 * 
 */
package com.proptiger.data.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proptiger.data.pojo.response.APIResponse;

/**
 * @author mandeep
 *
 */
@Controller
@RequestMapping("data/v1/advertise-agency-status")
public class AdvertiseAgencyController {

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    private static enum AdvertiseAgency {
        MIXPANEL("MIXPANEL", true),
        GOOGLE_CAMPAIGN("GOOGLE_CAMPAIGN", true),
        OMG("OMG", true),
        TYROO("TYROO", false),
        _79MOBI("79MOBI", true),
        VSERVE("VSERVE", true),
        INMOBI("INMOBI", true),
        GMOBI("GMOBI", true);

        private boolean enabled;
        private String label;

        AdvertiseAgency(String name, boolean enabled) {
            this.setEnabled(enabled);
            this.setLabel(name);
        }
        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        public String getLabel() {
            return label;
        }
        public void setLabel(String name) {
            this.label = name;
        }
    }

    @RequestMapping
    @ResponseBody
    public APIResponse get() {
        return new APIResponse(AdvertiseAgency.values());
    }
}
