/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.util;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;

/**
 *
 * @author mukand
 */
public class SolrResponseReader {
    
    public Map<String, Map<String, Integer>> getFacetResults(NamedList<Object> response){
            Map<String, Map<String, Integer>> list = new HashMap<String, Map<String, Integer>>();
            Gson gson = new Gson();
            SimpleOrderedMap map = (SimpleOrderedMap)response.getVal(2);
            map = (SimpleOrderedMap)map.getVal(1);
            int i=0,j=0;
            NamedList<Object> it;
            HashMap<String, Integer> hash = null;
            String key;
            
            for(i=0; i<map.size(); i++){
                it = (NamedList<Object>)map.getVal(i);
                key = map.getName(i);
                hash = new HashMap<String, Integer>();
                for(j=0; j<it.size(); j++){
                    hash.put(it.getName(j), (Integer)it.getVal(j) );
                }
                list.put(key, hash);
            }
            
            return list;
    }
    
}
