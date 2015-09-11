package com.proptiger.columbus.util;

import com.proptiger.core.annotations.Essential;

public class ResultEssential {

    private boolean             passed;

    private String              message         = "";

    private final static String SUCCESS_MESSAGE = "Requirement '%s' satisfied on Field '%s'";
    private final static String ERROR_MESSAGE   = "Requirement '%s' failed on Field '%s'";

    public ResultEssential(boolean passed, String field, Essential.TestType testType) {
        this.passed = passed;
        if (!passed) {
            this.message = String.format(ERROR_MESSAGE, testType, field);
        }
        else {
            this.message = String.format(SUCCESS_MESSAGE, testType, field);
        }
    }

    public boolean isPassed() {
        return passed;
    }

    public String getMessage() {
        return message;
    }

}
