package com.proptiger.data.event.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

@Repository
@Transactional(value="hibernateTransactionManager")
public class DynamicTableDao {

    @Autowired
    protected SessionFactory sessionFactory;

    protected Logger         logger = LoggerFactory.getLogger(this.getClass());

    protected List<Map<String, Object>> runDynamicTableQuery(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return new ArrayList<Map<String, Object>>();
        }
        
        Session session = sessionFactory.openSession();//getCurrentSession();
        //Transaction tx = session.beginTransaction();


        Query query = session.createSQLQuery(queryString);
        logger.info("Session status: "+session.isOpen());
        logger.info("Connection open:"+ session.isConnected());
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        logger.info("Session status: "+session.isOpen());
        logger.info("Connection open:"+ session.isConnected());
        List<Map<String, Object>> results = null;
        try {
            logger.info("Session status: "+session.isOpen());
            logger.info("Connection open:"+ session.isConnected());
            results = query.list();
            logger.info("OUTPUT "+new Gson().toJson(results));
        }
        catch (Exception e) {
            //tx.commit();
            session.close();
            logger.error("Query " + queryString + " : Error Message : " + e.getMessage());
            e.printStackTrace();
        }

        if (results == null) {
            new ArrayList<Map<String, Object>>();
        }
        //tx.commit();
        session.close();
        return results;
    }
}
