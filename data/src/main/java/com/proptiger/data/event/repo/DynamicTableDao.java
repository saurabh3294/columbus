package com.proptiger.data.event.repo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.core.util.DateUtil;

@Repository
@Transactional(value = "hibernateTransactionManager")
public class DynamicTableDao {

    @Autowired
    protected SessionFactory    sessionFactory;

    @Autowired
    protected ConversionService conversionService;

    protected Logger            logger = LoggerFactory.getLogger(this.getClass());

    protected List<Map<String, Object>> runDynamicTableQuery(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return new ArrayList<Map<String, Object>>();
        }
        logger.info(queryString);
        Session session = sessionFactory.openSession();// getCurrentSession();
        // Transaction tx = session.beginTransaction();

        Query query = session.createSQLQuery(queryString);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> results = null;
        try {
            // TODO to make null values as empty. Find a way where Hibernate
            // makes it.
            results = query.list();
            //logger.debug(" QUERY OUTPUT " + Serializer.toJson(results));
        }
        catch (Exception e) {
            session.close();
            logger.error("Query " + queryString + " : Error Message : " + e.getMessage());
            e.printStackTrace();
        }

        if (results == null) {
            new ArrayList<Map<String, Object>>();
        }

        session.close();
        return results;
    }

    protected String convertMapToSql(Map<String, Object> map) {
        if (map == null) {
            return "";
        }

        //logger.debug(" Start Convert MAP TO QUERY STRING : " + Serializer.toJson(map));
        String condition = "";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            condition += " AND " + entry.getKey() + "= ";
            if (entry.getValue() instanceof Number) {
                condition += entry.getValue();
            }
            else if (entry.getValue() instanceof Date) {
                // TODO to reset the time during db load adjusting by its
                // offset.
                Date time = (Date) entry.getValue();
                time = DateUtil.addMinutes(time, time.getTimezoneOffset() * -1);

                condition += " '" + conversionService.convert(time, String.class) + "'";
            }
            else {
                condition += " '" + entry.getValue() + "'";
            }
        }

        //logger.debug(" CONDITION FORMED IS " + condition);
        return condition;
    }

    protected String convertMapOfListToSql(Map<String, List<Object>> map) {
        if (map == null) {
            return "";
        }

        //logger.debug(" Start Convert MAP TO QUERY STRING : " + Serializer.toJson(map));
        String condition = "";
        for (Entry<String, List<Object>> entry : map.entrySet()) {
            List<Object> list = entry.getValue();
            if (list == null || list.isEmpty()) {
                continue;
            }
            condition += " AND " + entry.getKey() + " in (";
            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);               
                if (obj instanceof Number) {
                    condition += obj;
                }
                else if (obj instanceof Date) {
                    // TODO to reset the time during db load adjusting by its
                    // offset.
                    Date time = (Date) obj;
                    time = DateUtil.addMinutes(time, time.getTimezoneOffset() * -1);

                    condition += " '" + conversionService.convert(time, String.class) + "'";
                }
                else {
                    condition += " '" + obj + "'";
                }
                if (i != list.size() - 1) {
                    condition += ", ";
                }
            }
            condition += ") ";
        }

        //logger.debug(" CONDITION FORMED IS " + condition);
        return condition;
    }
}
