package com.proptiger.columbus.typeahead;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    
    @Autowired
    private CustomTestCaseReader customTestCaseReader;
    
    @Test
    public void testCity() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.City));
    }
    
    @Test
    public void testLocality() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.Locality));
    }
    
    @Test
    public void testProject() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.Project));
    }
    
    @Test
    public void testSuburb() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.Suburb));
    }
    
    @Test
    public void testBuilder() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.Builder));
    }
    
    public void testCustom(){
        Map<String, List<TaTestCase>> mapTestCases = customTestCaseReader.getCustomTestCases();
        List<TaTestCase> testList;
        for(Entry<String, List<TaTestCase>> entry : mapTestCases.entrySet()){
            testList = entry.getValue();
            runTests(testList);
        }
    }
    
    private void runTests(List<TaTestCase> testList){
        testList = taTestExecuter.executeTests(testList);
        TestReport tr;
        for(TaTestCase ttc : testList){
            tr = taTestReport.getReport(ttc);
            Assert.assertTrue(tr.status, tr.message);
        }
    }

}
