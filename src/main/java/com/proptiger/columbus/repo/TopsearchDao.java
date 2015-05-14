package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javassist.expr.NewArray;

import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.proptiger.columbus.model.TypeaheadConstants;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.columbus.model.Topsearch;
import com.proptiger.core.repo.SolrDao;
import com.proptiger.core.util.Constants;
import com.proptiger.core.util.UtilityClass;

/**
 * 
 * @author Manmohan
 * 
 */

@Repository
public class TopsearchDao {

    @Autowired
    private SolrDao solrDao;

    /*private Logger  logger = LoggerFactory.getLogger(TypeaheadDao.class);*/

    public List<Topsearch> getTopsearches(String requiredEntities, int rows, List<String> filterQueries) {
        SolrQuery solrQuery = this.getSolrQuery(requiredEntities, rows, filterQueries);
        List<Topsearch> results = getSpellCheckedResponse(solrQuery, rows, filterQueries);
        return UtilityClass.getFirstNElementsOfList(results, rows);
    }

    // Add parameters to use the custom requestHandler
    private SolrQuery getSolrQuery(String requiredEntities, int rows, List<String> filterQueries) {
    	String query = "";
        SolrQuery solrQuery = getSimpleSolrQuery(query, rows, filterQueries);
        
        solrQuery.setParam("qt", "/payload");
        solrQuery.setParam("defType", "payload");
        //solrQuery.setParam("fl", "*,score");
        
        String[] entityArr =  requiredEntities.split(","); 
        String reqField = "";
        for (String entity : entityArr) {
        	String str = entity.trim().toUpperCase();
			if (str.equals("SUBURB"))
				reqField += "TYPEAHEAD_TOP_SEARCHED_SUBURB";
			if (str.equals("LOCALITY"))
				reqField += "TYPEAHEAD_TOP_SEARCHED_LOCALITY";
			if (str.equals("BUILDER"))
				reqField += "TYPEAHEAD_TOP_SEARCHED_BUILDER";
			if (str.equals("PROJECT"))
				reqField += "TYPEAHEAD_TOP_SEARCHED_PROJECT";
			reqField += ",";			
		}
        reqField = reqField.replaceAll("^[,]+|[,]+$", "");
        reqField = reqField+",id,TYPEAHEAD_TYPE";
        solrQuery.setParam("fl", reqField);

        /* Query time boosting */
        /*String boostQuery = getBoostQuery(query);
        if (!boostQuery.isEmpty()) {
            solrQuery.setParam("bq", boostQuery);
        }*/
        return solrQuery;
    }

    private String getBoostQuery(String query) {
        String boostQuery = "";
        StringTokenizer st = new StringTokenizer(query.trim());
        int count = st.countTokens();
        if (count < 2) {
            return boostQuery;
        }

        float boost = TypeaheadConstants.queryTimeBoostStart;
        /* Boost all-but-last query strings as core-texts */
        for (int i = 0; i < count - 1; i++) {
            boostQuery += "Core_text:" + st.nextToken() + "^" + Math.max(1, boost) + " ";
            boost *= TypeaheadConstants.queryTimeBoostMultiplier;
        }

        /* Boost last query string as an edgeNGram */
        boostQuery += ("ENGram:" + st.nextToken() + "^" + Math.max(1, boost) + " ");

        // System.out.println("=====>> Q = [" + query + "], BQ = [" + boostQuery
        // + "]");
        return boostQuery;
    }

    private SolrQuery getSimpleSolrQuery(String query, int rows, List<String> filterQueries) {
        SolrQuery solrQuery = new SolrQuery(QueryParserUtil.escape(query.toLowerCase()));
        for (String fq : filterQueries) {
            solrQuery.addFilterQuery(fq);
        }

        solrQuery.setRows(rows);
        return solrQuery;
    }

    /**
     * If the query has a typo and can be corrected then new query is generated
     * using the suggestions and executed automatically
     */
    private List<Topsearch> getSpellCheckedResponse(SolrQuery solrQuery, int rows, List<String> filterQueries) {

        List<Topsearch> results = new ArrayList<Topsearch>();
        QueryResponse response = solrDao.executeQuery(solrQuery);        
        results = response.getBeans(Topsearch.class);
        return results;
       
    }
  
    
    
  
}
