package com.proptiger.data.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.ext.search.PrimitiveStatement;
import org.apache.cxf.jaxrs.ext.search.SearchBean;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.fiql.FiqlParser;

import com.proptiger.core.pojo.FIQLSelector;

public class FIQLUtils {
    
    /**
     * Returns all primitive statements present in the given FIQLSelector that contain the given property.
     * @param selector
     * @param property 
     * @return
     */
    public static <T> List<PrimitiveStatement> getPrimitiveStatementsFromSelector(FIQLSelector selector, String propertyName){
        String filters = selector.getFilters();
        FiqlParser<SearchBean> fiqlParser = new FiqlParser<SearchBean>(SearchBean.class);
        SearchCondition<SearchBean> searchCondition = fiqlParser.parse(filters);
        List<PrimitiveStatement> psList = new ArrayList<PrimitiveStatement>();
        FIQLUtils.fillPrimitiveStatementsHavingProperty(searchCondition, propertyName, psList);
        return psList;
    }
        
    private static <T> void fillPrimitiveStatementsHavingProperty(SearchCondition<T> searchCondition, String pname, List<PrimitiveStatement> list){
        
        List<SearchCondition<T>> scList = searchCondition.getSearchConditions();
        if(scList == null){
            PrimitiveStatement ps = searchCondition.getStatement();
            if(ps.getProperty().equals(pname)){
                list.add(ps);
            }
            return;
        }
        else {
            for(SearchCondition<T> sc : scList){
                fillPrimitiveStatementsHavingProperty(sc, pname, list);
            }
        }
    }

}
