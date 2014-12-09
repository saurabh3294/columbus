package com.proptiger.columbus.typeahead;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.columbus.service.AbstractTest;

@Component
public class TypeaheadTest extends AbstractTest {

    @Autowired
    private TaTestExecuter       taTestExecuter;

    @Autowired
    private TaTestGenerator      taTestGenerator;

    @Autowired
    private CustomTestCaseReader customTestCaseReader;

    @Value("${test.default.test.execution.limit}")
    private int                  defaultTestExecutionLimit;

    @Value("${test.default.entity.fetch.limit}")
    private int                  defaultEntityFetchLimit;

    @Value("${test.default.entity.fetch.pagesize}")
    private int                  defaultEntityFetchPageSize;

    @Value("${test.report.dir}")
    private String               testReportDir;

    @Value("${test.default.file.export.pagesize}")
    private static final int PageSize_FileExport = 500;

    String fileNameReport;

    private static Logger        logger = LoggerFactory.getLogger(TypeaheadTest.class);

    private enum TestMode {
        normal, report
    }

    private TestMode            testMode;

    private static final String OptionName_Mode = "mode";

    @PostConstruct
    public void initialize() throws Exception {

        /** Validations **/

        org.springframework.util.Assert.isTrue(
                (defaultEntityFetchLimit >= defaultTestExecutionLimit),
                "DefaultEntityFetchLimit should be greater that DefaultTestLimit");

        org.springframework.util.Assert.isTrue(
                (defaultEntityFetchPageSize > 0),
                "defaultEntityFetchPageSize should be greater that 0.");

        this.testMode = getTestMode();

        /** Use Test-Mode specific settings. **/

        if (testMode == TestMode.report) {
            FileUtils.forceMkdir(new File(testReportDir));
            fileNameReport = testReportDir + "test-report" + StringUtils.replaceChars((new Date()).toString(), ' ', '_') + ".csv";
        }
    }

    private TestMode getTestMode() {
        TestMode testMode = TestMode.normal;
        String testMode_s = System.getProperty(OptionName_Mode);
        if (testMode_s == null) {
            return testMode;
        }
        else {
            return (TestMode.valueOf(System.getProperty(OptionName_Mode)));
        }
    }

    @Test
    public void testCity() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.City, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test
    public void testLocality() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.Locality, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test
    public void testProject() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.Project, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test
    public void testSuburb() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.Suburb, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test
    public void testBuilder() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.Builder, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test
    public void testCustom() {
        Map<String, List<TaTestCase>> mapTestCases = customTestCaseReader.getCustomTestCases();
        List<TaTestCase> testList;
        for (Entry<String, List<TaTestCase>> entry : mapTestCases.entrySet()) {
            testList = entry.getValue();
            runTests(testList, defaultTestExecutionLimit, testMode);
        }
    }

    private void runTests(List<TaTestCase> testList, int testLimit, TestMode tmode) {
        testList = taTestExecuter.executeTests(testList, testLimit);
        if (tmode == TestMode.report) {
            exportTestResultsToReport(testList);
            return;
        }
        
        TaTestReport tr;
        for (TaTestCase ttc : testList) {
            tr = TaTestReport.getReport(ttc);
            Assert.assertTrue(tr.status, tr.message);
            logger.info("Typeahead Test : Test case passed : " + ttc.getLogString());
        }
    }
    
    /**
     * Writes test-result reports to file page-wise.
     * @param testList
     */
    private synchronized void exportTestResultsToReport(List<TaTestCase> testList){
        List<String> reportLines = new ArrayList<String>();
        for (TaTestCase ttc : testList) {
            reportLines.add(TaTestReport.getReport(ttc).getReportLine());
            logger.info("Typeahead Test : Test case passed : " + ttc.getLogString());
            if(reportLines.size() > PageSize_FileExport){
                logger.info("Writing " + reportLines.size() + "test case reports to file.");
                try {
                    FileUtils.writeLines(new File(fileNameReport), reportLines, true);
                }
                catch (IOException ioEx) {
                    logger.error("IO Exception while writing to file : " + fileNameReport, ioEx);
                }
                reportLines.clear();
            }
        }
    }    
}
