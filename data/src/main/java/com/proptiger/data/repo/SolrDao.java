package com.proptiger.data.repo;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proptiger.data.enums.DocumentType;
import com.proptiger.data.util.PropertyKeys;
import com.proptiger.data.util.PropertyReader;
import com.proptiger.exception.ProAPIException;

/**
 * Solr dao provides method to execute solr query.
 * 
 * @author Rajeev Pandey
 * 
 */
@Component
public class SolrDao {
    private static Logger    logger = LoggerFactory.getLogger(SolrDao.class);

    @Autowired
    protected PropertyReader propertyReader;

    private HttpSolrServer   httpSolrServerb2b;

    private HttpSolrServer   httpSolrServerDefault;

    @PostConstruct
    private void init() {

        httpSolrServerb2b = new HttpSolrServer(propertyReader.getRequiredProperty(PropertyKeys.SOLR_SERVER_B2B_URL));
        httpSolrServerDefault = new HttpSolrServer(
                propertyReader.getRequiredProperty(PropertyKeys.SOLR_SERVER_DEFAULT_URL));
    }

    /**
     * This method takes a SolrQuery and execute that, and if any exception
     * occures then it wrapps that exception in ProAPIException and throw back
     * to the caller.
     * 
     * @param query
     * @return
     */
    public QueryResponse executeQuery(SolrQuery query) {
        try {
            logger.debug("SolrQuery {}", query);
            String applicationType = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest().getHeader("applicationType");
            if ( applicationType != null && applicationType.equals("b2b")) {
                logger.debug("Running SolrQuery for b2b ");
                return httpSolrServerb2b.query(query);
            }
            else {
                logger.debug("Running SolrQuery for website");
                return httpSolrServerDefault.query(query);
            }
        }
        catch (Exception e) {
            throw new ProAPIException("Could not run Solr query", e);
        }
    }

    /**
     * Creating basic solr query for document type
     * 
     * @param documentType
     * @return
     */
    public static SolrQuery createSolrQuery(DocumentType documentType) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.addFilterQuery(DocumentType.getDocumentTypeFilter(documentType));
        return solrQuery;
    }
}
