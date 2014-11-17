package com.proptiger.columbus.typeahead;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.service.AbstractTest;
import com.proptiger.columbus.typeahead.TaTestReport.TestReport;

@Component
public class TypeaheadTest extends AbstractTest {

    @Autowired
    private TaTestExecuter       taTestExecuter;

    @Autowired
    private TaTestGenerator      taTestGenerator;

    @Autowired
    private TaTestReport         taTestReport;

    @Autowired
    private CustomTestCaseReader customTestCaseReader;

    @Value("${test.execution.limit}")
    private int                  defaultTestLimit;

    private static Logger        logger = LoggerFactory.getLogger(TypeaheadTest.class);

    @Test
    public void testCity() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.City), defaultTestLimit);
    }

    @Test
    public void testLocality() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.Locality), defaultTestLimit);
    }

    @Test
    public void testProject() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.Project), defaultTestLimit);
    }

    @Test
    public void testSuburb() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.Suburb), defaultTestLimit);
    }

    @Test
    public void testBuilder() {
        runTests(taTestGenerator.getTestCasesByType(TaTestCaseType.Builder), defaultTestLimit);
    }

    @Test
    public void testCustom() {
        Map<String, List<TaTestCase>> mapTestCases = customTestCaseReader.getCustomTestCases();
        List<TaTestCase> testList;
        for (Entry<String, List<TaTestCase>> entry : mapTestCases.entrySet()) {
            testList = entry.getValue();
            runTests(testList, defaultTestLimit);
        }
    }

    private void runTests(List<TaTestCase> testList, int testLimit) {
        testList = taTestExecuter.executeTests(testList, testLimit);
        TestReport tr;
        int ctr = 0;
        for (TaTestCase ttc : testList) {
            if (ctr >= testLimit) {
                break;
            }
            tr = taTestReport.getReport(ttc);
            Assert.assertTrue(tr.status, tr.message);
            logger.info("Typeahead Test : Test case passed : " + ttc.getLogString());
            ctr++;
        }
    }
    
    

}
