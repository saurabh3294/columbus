package com.proptiger.data.event.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DynamicTableDao {

    @Autowired
    protected SessionFactory sessionFactory;

    protected Logger         logger = LoggerFactory.getLogger(this.getClass());

    protected List<Map<String, Object>> runDynamicTableQuery(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return new ArrayList<Map<String, Object>>();
        }
        
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery(queryString);

        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        List<Map<String, Object>> results = null;
        try {
            results = query.list();
        }
        catch (Exception e) {
            logger.error("Query " + queryString + " : Error Message : " + e.getMessage());
        }

        if (results == null) {
            new ArrayList<Map<String, Object>>();
        }

        return results;
    }
}
