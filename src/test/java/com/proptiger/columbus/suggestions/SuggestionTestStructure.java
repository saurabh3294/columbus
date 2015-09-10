package com.proptiger.columbus.suggestions;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.Assert;

import com.proptiger.columbus.service.SuggestionTest;
import com.proptiger.columbus.util.TestEssential;
import com.proptiger.columbus.util.ResultEssential;
import com.proptiger.core.model.Typeahead;

@Component
public class SuggestionTestStructure implements SuggestionTest {

    @Autowired
    private TestEssential<Typeahead> testEssential;

    public void test(List<Typeahead> suggestions) {
        for (Typeahead suggestion : suggestions) {
            testObjectValidity(suggestion);
        }
    }

    private void testObjectValidity(Typeahead typeahead) {
        Assert.assertNotNull(typeahead, "Typeahead object is null");

        List<ResultEssential> results = testEssential.testEssentialFields(typeahead);
        for (ResultEssential result : results) {
            Assert.assertTrue(result.isPassed(), result.getMessage());
        }

        String typeaheadId = typeahead.getId();
        Assert.assertTrue(StringUtils.contains(typeaheadId, "Typeahead-"));

        String typeaheadRedirectUrlFilters = typeahead.getRedirectUrlFilters();
        Assert.assertNotNull(typeaheadRedirectUrlFilters, "Typeahead redirect URL filter is null");
        Assert.assertFalse(typeaheadRedirectUrlFilters.isEmpty(), "Typeahead redirect URL filter is empty");

        Assert.assertTrue(typeahead.isSuggestion(), "isSuggestion flag must be on");
        Assert.assertFalse(typeahead.isGooglePlace(), "isGooglePlace flag must be off");

    }

}