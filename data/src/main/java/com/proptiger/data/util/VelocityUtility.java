package com.proptiger.data.util;

import java.util.Collections;
import java.util.List;

/**
 * This class provides a way to do some sorting in velocity template file
 * 
 * @author Rajeev Pandey
 * 
 */
public class VelocityUtility {

    public void sort(List<String> list) {
        if (list != null) {
            Collections.sort(list);
        }
    }
    public static void main(String args[]){
        String str = "1.5BHK";
        str.split("\\d");
    }
}
