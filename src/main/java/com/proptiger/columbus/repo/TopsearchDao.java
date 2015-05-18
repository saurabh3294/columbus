package com.proptiger.columbus.repo;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proptiger.columbus.util.Topsearch;
import com.proptiger.columbus.util.TopsearchUtils;
import com.proptiger.core.enums.DomainObject;
import com.proptiger.core.model.Typeahead;
import com.proptiger.core.repo.SolrDao;

/**
 * 
 * @author Manmohan
 * 
 */

@Repository
public class TopsearchDao {

    @Autowired
    private SolrDao solrDao;

    public List<Topsearch> getTopsearchess(int entityId, String requiredEntities) {
        List<Topsearch> topsearchResults = new ArrayList<Topsearch>();
        if (requiredEntities == null || requiredEntities.trim() == "" || entityId == 0) {
            return topsearchResults;
        }
        String entityType = getEntityTypeFromEntityId(entityId);
        SolrQuery solrQuery = this.getSolrQuery(entityType, entityId, requiredEntities);

        List<Typeahead> results = executeSolrQuery(solrQuery);

        topsearchResults = TopsearchUtils.typeaheadToTopsearchConverter(results);
        return topsearchResults;

    }

    private String getEntityTypeFromEntityId(int entityId) {
        DomainObject dObj = DomainObject.getDomainInstance(Long.parseLong(String.valueOf(entityId)));
        return dObj.getText();
    }

    private SolrQuery getSolrQuery(String entityType, int entityId, String requiredEntities) {
        String typeaheadId = "TYPEAHEAD-" + entityType.toUpperCase() + "-" + String.valueOf(entityId);

        SolrQuery solrQuery = new SolrQuery(QueryParserUtil.escape(""));
        solrQuery.addFilterQuery("id:" + typeaheadId);
        solrQuery.addFilterQuery("TYPEAHEAD_TYPE:" + entityType.toUpperCase());
        solrQuery.addFilterQuery("DOCUMENT_TYPE:TYPEAHEAD");

        solrQuery.setParam("qt", "/payload");
        solrQuery.setParam("defType", "payload");

        String reqField = getRequiredFields(requiredEntities);
        solrQuery.setParam("fl", reqField);

        return solrQuery;
    }

    private String getRequiredFields(String requiredEntities) {
        String[] entityArr = requiredEntities.split(",");
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
        reqField = reqField + ",id,TYPEAHEAD_TYPE";

        return reqField;
    }

    private List<Typeahead> executeSolrQuery(SolrQuery solrQuery) {

        List<Typeahead> results = new ArrayList<Typeahead>();
        QueryResponse response = solrDao.executeQuery(solrQuery);
        results = response.getBeans(Typeahead.class);
        return results;

    }

}
