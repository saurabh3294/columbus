/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.proptiger.exception.ProAPIException;

/**
 * 
 * @author mukand
 */
public class HMAC_Client {
    private final static String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public static String calculateHMAC(String secret, String data) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            String result = Hex.encodeHexString(rawHmac);
            return result;
        }
        catch (GeneralSecurityException e) {
            throw new IllegalArgumentException();
        }
    }

    public String calculateMD5(String contentToEncode) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(contentToEncode.getBytes());
            String result = new String(Base64.encodeBase64(digest.digest()));
            return result;
        }
        catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            throw new ProAPIException(e);
        }
        
    }

}
