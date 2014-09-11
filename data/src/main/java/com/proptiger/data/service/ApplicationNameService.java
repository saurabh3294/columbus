package com.proptiger.data.service;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proptiger.data.enums.Application;
import com.proptiger.data.util.Constants;

/**
 * Identify type off application making request to apis.
 * @author Rajeev Pandey
 *
 */
public class ApplicationNameService {

    public static boolean isB2BApplicationRequest() {
        String appName = getApplicationType();
        if (appName != null && appName.equals("b2b")) {
            return true;
        }
        else {
            return false;
        }
    }

    public static Application getApplicationTypeOfRequest(){
        String appName = getApplicationType();
        if (appName != null && appName.equals("b2b")) {
            return Application.B2B;
        }
        return Application.DEFAULT;
        
    }
    public static String getApplicationType() {
        RequestAttributes requestAttribute = RequestContextHolder.getRequestAttributes();
        if (requestAttribute != null && requestAttribute instanceof ServletRequestAttributes) {
            return (((ServletRequestAttributes) requestAttribute).getRequest()
                    .getHeader(Constants.APPLICATION_TYPE_HEADER));
        }
        else {
            return null;
        }
    }

}
