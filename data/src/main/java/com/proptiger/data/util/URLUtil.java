package com.proptiger.data.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

public class URLUtil {
    
    private static Logger                logger                    = LoggerFactory.getLogger(URLUtil.class);

    private static final String          FORWARD_SLASH             = "/";

    /**
     * Get complete url. if url passed have forward slash at start then remove
     * that since we already have forward slash in base url part. This method
     * will encode url.
     * 
     * @param uri
     * @return
     */
    public static String getCompleteUrl(String uri, String baseUrl) {

        if (uri.startsWith(FORWARD_SLASH)) {
            uri = uri.replaceFirst(FORWARD_SLASH, "");
        }
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        }
        catch (UnsupportedEncodingException e1) {
            logger.error("Could not decode uri {}", uri, e1);
        }
        String completeUrl = baseUrl + uri;
        String encoded = UriComponentsBuilder.fromUriString(completeUrl).build().encode().toString();
        return encoded;
    }
    
    public static URI getEncodedURIObject(String uri, String baseUrl) 
    {
        String completeEncodedURL = getCompleteUrl(uri, baseUrl);

        try{
            URI uriObj = new URI(completeEncodedURL);
            return uriObj;
        }
        catch(Exception ex)
        {
            logger.error("Could not make URO Object {}", completeEncodedURL, ex);
            return null;
        }
    }
    
}
