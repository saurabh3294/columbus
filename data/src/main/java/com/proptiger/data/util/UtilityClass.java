package com.proptiger.data.util;

public class UtilityClass {
	/**
     * Returns non zero max of given 2 numbers - null otherwise
     * @param a
     * @param b
     * @return
     */
    public static Double max(Double a, Double b) {
        Double c = a;
        if (a == null) {
            c = b;
        }
        else if (b != null) {
            c = Math.max(a, b);
        }

        return c;
    }

    /**
     * Returns non zero min of given 2 numbers - null otherwise
     * @param a
     * @param b
     * @return
     */
    public static Double min(Double a, Double b) {
        Double c = a;
        if (a == null || a == 0) {
            c = b;
        }
        else if (b != null && b != 0) {
            c = Math.min(a, b);
        }

        return c;
    }
}
