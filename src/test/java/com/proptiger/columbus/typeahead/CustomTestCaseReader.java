package com.proptiger.columbus.typeahead;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomTestCaseReader {

    private String        fileName   = "/tmp/typeahead-test";
    private String[]      extensions = new String[] { "csv" };

    private static Logger logger     = LoggerFactory.getLogger(CustomTestCaseReader.class);

    public CustomTestCaseReader() {

    }

    public Map<String, List<TaTestCase>> getCustomTestCases() {
        Map<String, List<TaTestCase>> mapTestCases = new HashMap<String, List<TaTestCase>>();
        File dir = new File(fileName);
        Collection<File> fileList = FileUtils.listFiles(dir, extensions, true);
        if (fileList == null) {
            logger.error("Could not find test case directory : " + dir.getAbsolutePath());
        }

        for (File file : fileList) {
            mapTestCases.put(file.getName(), getCustomTestCasesFromFile(file));
        }
        return mapTestCases;
    }

    public List<TaTestCase> getCustomTestCasesFromFile(File file) {
        List<TaTestCase> testList = new ArrayList<TaTestCase>();
        List<String> testLineList = new ArrayList<String>();
        try {
            testLineList = FileUtils.readLines(file);
        }
        catch (IOException e) {
            logger.error("Could not read custom test case file : " + file.getAbsolutePath());
            return testList;
        }

        TaTestCase taTestCase;
        int lineCount = 0;
        for (String testLine : testLineList) {
            if (testLine.startsWith("#")) {
                continue;
            }
            taTestCase = getTestcaseObjFromLogLine(testLine);
            if (taTestCase == null) {
                logger.error("Invalid custom testcase at line " + lineCount + " [" + testLine + "]");
            }
            testList.add(taTestCase);
            lineCount++;
        }
        return testList;
    }

    private TaTestCase getTestcaseObjFromLogLine(String line) {
        String[] params = StringUtils.split(line, ',');
        TaTestCase taTestCase = null;
        try {
            taTestCase = new TaTestCase(
                    params[0],
                    TaTestCaseType.valueOf(params[1]),
                    Integer.parseInt(params[2]),
                    Integer.parseInt(params[3]),
                    params[4]);
        }
        catch (Exception e) {
            return null;
        }
        return taTestCase;
    }

}
