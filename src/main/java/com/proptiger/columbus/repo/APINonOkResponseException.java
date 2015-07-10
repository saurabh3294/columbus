/**
 * 
 */
package com.proptiger.columbus.repo;

/**
 * @author user
 *
 */
public class APINonOkResponseException extends Exception {
    private final String msg;

    public APINonOkResponseException() {
        msg = "Google API Non-Ok Responce";
    }

    public APINonOkResponseException(String s) {
        msg = s;
    }

    @Override
    public String toString() {
        return msg;
    }
}
