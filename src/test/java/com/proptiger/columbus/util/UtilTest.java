package com.proptiger.columbus.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.columbus.service.TypeaheadService;
import com.proptiger.core.model.Typeahead;

@Component
@Test(singleThreaded = true)
public class UtilTest extends AbstractTest {

    @Autowired
    private TypeaheadService typeaheadService;

    @Test(enabled = true)
    public void testTypeaheadUtils() {
        int[] idList = new int[] { 10052, 51656, 100002, 502704 };
        Typeahead t;
        for (int id : idList) {
            t = typeaheadService.getTypeaheadsV4(String.valueOf(id), 1, null, null, null).get(0);
            Assert.assertEquals(String.valueOf(id), TypeaheadUtils.parseEntityIdAsString(t));
            Assert.assertEquals(id, TypeaheadUtils.parseEntityIdAsInt(t));
        }
    }
}
