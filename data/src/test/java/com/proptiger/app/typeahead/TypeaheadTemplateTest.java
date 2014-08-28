package com.proptiger.app.typeahead;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.app.typeahead.suggestions.NLPSuggestionHandler;
import com.proptiger.app.typeahead.thandlers.TemplateTypes;
import com.proptiger.data.TestAPIs;
import com.proptiger.data.model.Typeahead;
import com.proptiger.data.service.AbstractTest;
import com.proptiger.data.util.URLUtil;

public class TypeaheadTemplateTest extends AbstractTest {

    @Autowired
    private NLPSuggestionHandler nlpSuggestionHandler;

    private RestTemplate         restTemplate = new RestTemplate();

    private static Logger        logger       = LoggerFactory.getLogger(TypeaheadTemplateTest.class);

    /* TODO :: Pick this up from config file */
    private String               BASE_URL     = "http://beta.proptiger-ws.com/";
    private String               testCity     = "Noida";

    // @Test (enabled = false)
    @Test
    public void TypeaheadTemplateValidURLTest() {

        List<Typeahead> taResults = new ArrayList<Typeahead>();
        for (TemplateTypes ttype : TemplateTypes.values()) {
            taResults.addAll(nlpSuggestionHandler.getNlpTemplateBasedResults(ttype.getText(), testCity, 5));
        }

        ArrayList<TypeaheadUrlValidityResult> taUrlValidityResults = new ArrayList<TypeaheadUrlValidityResult>();
        HttpStatus httpStatus;
        for (Typeahead t : taResults) {
            httpStatus = getURLResponseStatus(t.getRedirectUrl());
            TypeaheadUrlValidityResult tuvResult = new TypeaheadUrlValidityResult(t, httpStatus);
            taUrlValidityResults.add(tuvResult);
            logger.error(tuvResult.getMessage() + " ::: Status = " + httpStatus.name());
            Assert.assertEquals(tuvResult.httpStatus, HttpStatus.OK, tuvResult.getMessage());
        }
    }

    private HttpStatus getURLResponseStatus(String url) {
        try {
            URI uri = URLUtil.getEncodedURIObject(url, BASE_URL);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
            return (response.getStatusCode());
        }
        catch (Exception ex) {
            return null;
        }
    }

    private class TypeaheadUrlValidityResult {
        public Typeahead  typeahead;
        public HttpStatus httpStatus;

        public TypeaheadUrlValidityResult(Typeahead typeahead, HttpStatus httpStatus) {
            this.typeahead = typeahead;
            this.httpStatus = httpStatus;
        }

        public String getMessage() {
            String msg = "Typeahead Template Suggestion generated an invalid URL. Template Details = [" + this.typeahead
                    .getId() + ", " + this.typeahead.getDisplayText() + ", " + this.typeahead.getRedirectUrl() + "]";
            return msg;
        }
    }

}
