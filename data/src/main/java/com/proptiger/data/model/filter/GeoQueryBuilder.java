/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model.filter;

/**
 *
 * @author mukand
 */
public class GeoQueryBuilder {
    
    public static void applyDistanceQuery(Float latitude, Float longitude, Float radius, QueryBuilder queryBuilder){
        if(latitude != null && longitude != null && radius != null){
            queryBuilder.addGeo(radius, latitude+","+longitude);
        }
            
    }
    
}
