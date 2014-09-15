package com.proptiger.data.enums;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author azi
 * 
 */

public enum AuthProvider {
    Facebook(1), Google(2), Yahoo(3);

    private int                                         providerId;
    private static final HashMap<Integer, AuthProvider> idToProviderMap = new HashMap<>();

    static {
        for (AuthProvider iterable_element : AuthProvider.values()) {
            idToProviderMap.put(iterable_element.getProviderId(), iterable_element);
        }
    }

    private AuthProvider(int providerId) {
        this.providerId = providerId;
    }

    public int getProviderId() {
        return this.providerId;
    }

    public static AuthProvider getAuthProviderIgnoreCase(String provider) {
        for (AuthProvider authProvider : AuthProvider.values()) {
            if (StringUtils.equalsIgnoreCase(provider, authProvider.toString())) {
                return authProvider;
            }
        }
        return null;
    }
}