package com.proptiger.columbus.typeahead;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.proptiger.columbus.typeahead.TaTestReport.TestReport;

public class TypeaheadTest {

    @Autowired
    private TaTestExecuter taTestExecuter;
    
    @Autowired
    private TaTestGenerator taTestGenerator;
    
    @Autowired
    private TaTestReport taTestReport;
    
    @Test
    public void testCity() {
        test(TaTestCaseType.City);
    }
    
    @Test
    public void testBuilder() {
        test(TaTestCaseType.Builder);
    }

    private void test(TaTestCaseType ttcType){
        List<TaTestCase> testList = taTestGenerator.getTestCasesByType(ttcType);
        testList = taTestExecuter.executeTests(testList);
        TestReport tr;
        for(TaTestCase ttc : testList){
            tr = taTestReport.getReport(ttc);
            Assert.assertTrue(tr.status, tr.message);
        }
    }

}
