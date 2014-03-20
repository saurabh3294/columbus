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
    
    public Integer abs(Integer val){
        return Math.abs(val);
    }
    public Double abs(Double val){
        return Math.abs(val);
    }
    public long round(double val){
        return Math.round(val);
    }
    public long round(Double val){
        return Math.round(val);
    }
}
