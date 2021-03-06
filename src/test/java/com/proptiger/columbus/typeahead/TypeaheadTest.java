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
@Test(singleThreaded = true)
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
    private int                  pageSizeFileExport;

    @Value("${test.default.typeahead.version}")
    private String               defaultApiVersion;

    String                       fileNameReport;

    private static Logger        logger = LoggerFactory.getLogger(TypeaheadTest.class);

    private enum TestMode {
        normal, report
    }

    private TestMode            testMode;
    private String              apiVersion;

    private static final String OptionName_Mode    = "mode";
    private static final String OptionName_Version = "version";

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
        this.apiVersion = getApiVersion();

        /** Use Test-Mode specific settings. **/

        if (testMode == TestMode.report) {
            FileUtils.forceMkdir(new File(testReportDir));
            fileNameReport = testReportDir + "test-report-"
                    + apiVersion
                    + "-"
                    + StringUtils.replaceChars((new Date()).toString(), ' ', '_')
                    + ".csv";
        }
    }

    @Test(enabled = false)
    public void testAllCity() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.City, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test(enabled = false)
    public void testAllLocality() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.Locality, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test(enabled = false)
    public void testAllProject() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.Project, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test(enabled = false)
    public void testAllSuburb() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.Suburb, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test(enabled = false)
    public void testAllBuilder() {
        runTests(
                taTestGenerator.getTestCasesByType(TaTestCaseType.Builder, defaultEntityFetchLimit),
                defaultTestExecutionLimit,
                testMode);
    }

    @Test(enabled = true)
    public void testSpecialCharInQuery() {
        logger.info("TEST NAME = SPECIAL CHARACTERS");
        taTestExecuter.assertNonNullResponse("AND");
        taTestExecuter.assertNonNullResponse("upto 50/- lacs in sahakar nagar");
    }

    @Test(enabled = true)
    public void testCustom() {
        logger.info("TEST NAME = CUSTOM");
        Map<String, List<TaTestCase>> mapTestCases = customTestCaseReader.getCustomTestCases(apiVersion);
        List<TaTestCase> testList;
        for (Entry<String, List<TaTestCase>> entry : mapTestCases.entrySet()) {
            testList = entry.getValue();
            logger.info("Runnig custom test cases from file : " + entry.getKey());
            runTests(testList, defaultTestExecutionLimit, testMode);
        }
    }

    private String getApiVersion() {
        String apiVersion = System.getProperty(OptionName_Version);
        if (apiVersion == null) {
            apiVersion = defaultApiVersion;
        }
        return apiVersion;
    }

    private TestMode getTestMode() {
        TestMode testMode = TestMode.normal;
        String testModeString = System.getProperty(OptionName_Mode);
        if (testModeString == null) {
            return testMode;
        }
        else {
            return (TestMode.valueOf(System.getProperty(OptionName_Mode)));
        }
    }

    private void runTests(List<TaTestCase> testList, int testLimit, TestMode tmode) {
        testList = taTestExecuter.executeTests(testList, testLimit, apiVersion);
        if (tmode == TestMode.report) {
            try {
                exportTestResultsToReport(testList);
            }
            catch (IOException ioEx) {
                logger.error("IO Exception while writing to file : " + fileNameReport, ioEx);
            }
            return;
        }

        TaTestReport tr;
        for (TaTestCase ttc : testList) {
            tr = TaTestReport.getReport(ttc);
            Assert.assertTrue(tr.status, tr.message);
            logger.debug("Test case passed : [" + ttc.getLogString() + "]");
        }
    }

    /**
     * Writes test-result reports to file, page wise.
     * 
     * @param testList
     */
    private synchronized void exportTestResultsToReport(List<TaTestCase> testList) throws IOException {
        logger.info("Exporting test results to file : " + fileNameReport);
        List<String> reportLines = new ArrayList<String>();

        /* Write report-lines to file page wise. */
        for (TaTestCase ttc : testList) {
            reportLines.add(TaTestReport.getReport(ttc).getReportLine());
            logger.debug("Test case passed : [" + ttc.getLogString() + "]");
            if (reportLines.size() >= pageSizeFileExport) {
                logger.debug("Writing " + reportLines.size() + "test report-lines to file.");
                FileUtils.writeLines(new File(fileNameReport), reportLines, true);
                reportLines.clear();
            }
        }

        /* Write remaining report-lines to file page wise. */
        logger.debug("Writing " + reportLines.size() + "test report-lines to file.");
        FileUtils.writeLines(new File(fileNameReport), reportLines, true);
    }
}
