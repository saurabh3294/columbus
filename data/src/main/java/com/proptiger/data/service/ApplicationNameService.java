package com.proptiger.data.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proptiger.data.util.Constants;

@Service
public class ApplicationNameService {

    public boolean isB2BApplicationRequest() {
        String appName = getApplicationName();
        if (appName != null && appName.equals("b2b")) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getApplicationName() {
        RequestAttributes requestAttribute = RequestContextHolder.getRequestAttributes();
        if (requestAttribute != null && requestAttribute instanceof ServletRequestAttributes) {
            return (((ServletRequestAttributes) requestAttribute).getRequest().getHeader(Constants.APPLICATION_NAME_HEADER));
        }
        else {
            return null;
        }
    }

}
