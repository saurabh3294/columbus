package com.proptiger.data.event.repo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.data.service.ImageService;

/**
 * 
 * @author sahil
 * 
 */
@Repository
public class RawDBEventDao {
    @Autowired
    private SessionFactory sessionFactory;

    private static Logger  logger = LoggerFactory.getLogger(ImageService.class);

    public List<Map<String, Object>> getRawDBEventByTableNameAndDate(
            String hostName,
            String dbName,
            String tableName,
            String dateAttributeName,
            Date dateAttributeValue) {

        /* *
         * The rows will sorted in ascending order by their current time. As
         * processing will take place accordingly.
         */
        String queryString = "SELECT * FROM " + dbName
                + "."
                + tableName
                + " WHERE "
                + dateAttributeName
                + " > "
                + dateAttributeValue
                + " ORDER BY "
                + dateAttributeName
                + " ASC ";

        return runDynamicTableQuery(queryString);
    }

    public Map<String, Object> getRaw(
            String hostname,
            String dbName,
            String tableName,
            String dateAttributeName,
            String dateAttributeValue, String primaryKeyName, Object primaryKeyValue) {
        
        return null;

    }
    
    private List<Map<String, Object>> runDynamicTableQuery(String queryString){
        Session session = sessionFactory.getCurrentSession();
        
        Query query = session.createQuery(queryString);

        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> results = null;
        try {
            results = query.list();
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (results == null) {
            new ArrayList<Map<String, Object>>();
        }

        return results;
    }
}
