package com.proptiger.columbus.typeahead;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.columbus.mvc.TypeaheadController;
import com.proptiger.columbus.response.ColumbusAPIResponse;
import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.core.model.Typeahead;

@Test(singleThreaded = true)
public class TestMultipleTypeaheadType extends AbstractTest{
    private static Logger       logger = LoggerFactory.getLogger(Typeahead.class);
    
    @Value("${typeahead.api.url.pattern}")
    private String TYPEAHEAD_URL;
    
    private String                           URL_PARAM_TEMPLATE_TYPEAHEAD= "?query=%s&rows=%s&typeAheadType=%s";

    @Autowired
    private TypeaheadController typeaheadController;
    
    @Test(enabled = true)
    public void testMultipleTypeaheadType(){
        String url;
        ColumbusAPIResponse apiResponse = null;
        int rows = 10;
        String query = "whitefield";
        String typeAheadType = "locality,suburb"; 
        url = String.format(TYPEAHEAD_URL,"v4") + String.format(URL_PARAM_TEMPLATE_TYPEAHEAD, query, rows, typeAheadType);
        
        boolean isLocality = false;
        boolean isSuburb = false;
        logger.info("RUNNING TEST (Typeahead-Multiple-Typeahead-Type). Url = " + url);
        apiResponse = mockRequestAndGetColumbusAPIResponse(typeaheadController, url);
        
        @SuppressWarnings("unchecked")
        List<Object> results = (List<Object>) (apiResponse.getData());
        List<Typeahead> typeaheadList = new ArrayList<Typeahead>();
        Typeahead typeahead = null;
        try {
            for (Object result : results) {
                ObjectMapper mapper = new ObjectMapper();
                typeahead = mapper.readValue(mapper.writer().writeValueAsString(result), Typeahead.class);
                typeaheadList.add(typeahead);
            }
        }
        catch (IOException e) {
            Assert.assertTrue(false, "Error mapping response to Typeahead.");
        }
        
        for(Typeahead ta : typeaheadList){
            if(ta.getType().equalsIgnoreCase("LOCALITY")){
                isLocality = true;
            }
            if(ta.getType().equalsIgnoreCase("SUBURB")){
                isSuburb = true;
            }
        }
        
        Assert.assertTrue(isSuburb);
        Assert.assertTrue(isLocality);
    }

}
