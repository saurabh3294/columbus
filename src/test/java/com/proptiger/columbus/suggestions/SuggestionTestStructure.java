package com.proptiger.columbus.suggestions;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.testng.Assert;

import com.proptiger.columbus.service.SuggestionTest;
import com.proptiger.core.model.Typeahead;

@Component
public class SuggestionTestStructure implements SuggestionTest {

    public void test(List<Typeahead> suggestions) {
        for (Typeahead suggestion : suggestions) {
            testObjectValidity(suggestion);
        }
    }

    private void testObjectValidity(Typeahead typeahead) {
        Assert.assertNotNull(typeahead, "Typeahead object is null");

        String typeaheadId = typeahead.getId();
        Assert.assertNotNull(typeaheadId, "Typeahead ID is null");
        Assert.assertFalse(typeaheadId.isEmpty(), "Typeahead ID is empty");
        Assert.assertTrue(StringUtils.contains(typeaheadId, "Typeahead-"));

        String typeaheadType = typeahead.getType();
        Assert.assertNotNull(typeaheadType, "Typeahead Type is null");
        Assert.assertFalse(typeaheadType.isEmpty(), "Typeahead Type is empty");
        Assert.assertTrue(typeaheadType.equalsIgnoreCase(typeaheadId), "Typeahead ID and type should be same");

        String typeaheadDisplayText = typeahead.getDisplayText();
        Assert.assertNotNull(typeaheadDisplayText, "Typeahead ID is null");
        Assert.assertFalse(typeaheadDisplayText.isEmpty(), "Typeahead ID is empty");

        String typeaheadRedirectUrl = typeahead.getRedirectUrl();
        Assert.assertNotNull(typeaheadRedirectUrl, "Typeahead redirect URL is null");
        Assert.assertFalse(typeaheadRedirectUrl.isEmpty(), "Typeahead redirect URL is empty");

        String typeaheadRedirectUrlFilters = typeahead.getRedirectUrlFilters();
        Assert.assertNotNull(typeaheadRedirectUrlFilters, "Typeahead redirect URL filter is null");
        Assert.assertFalse(typeaheadRedirectUrlFilters.isEmpty(), "Typeahead redirect URL filter is empty");

        Assert.assertTrue(typeahead.isSuggestion(), "isSuggestion flag must be on");
        Assert.assertFalse(typeahead.isGooglePlace(), "isGooglePlace flag must be off");

    }

}