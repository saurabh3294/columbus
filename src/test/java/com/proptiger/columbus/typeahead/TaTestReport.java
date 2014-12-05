package com.proptiger.columbus.typeahead;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.proptiger.columbus.model.Typeahead;

public class TaTestReport {

    int          position      = 0;
    boolean      status;
    String       message;
    String       testCaseInfo = "";
    List<String> betterResults = null;

    private TaTestReport(boolean status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public String getReportLine() {
        List<Object> list = new ArrayList<Object>();
        list.add(this.status);
        list.add(this.testCaseInfo);
        list.add(this.position);
        list.addAll(betterResults);
        return StringUtils.join(list, ",");
    }

    public static TaTestReport getReport(TaTestCase ttc) {
        TaTestReport taTestReport;
        String message = "";
        int pos = getTypeaheadPosition(ttc);
        if (pos < 0) {
            message = "Test=" + ttc.getLogString() + " : Outcome=[INVALID TEST CASE]";
            return new TaTestReport(false, message);
        }

        boolean status = (pos >= ttc.getMinRank() && pos <= ttc.getMaxRank());
        message = "Test=" + ttc.getLogString() + " : Outcome=[Position=" + pos + "]";
        taTestReport = new TaTestReport(status, message);
        taTestReport.betterResults = getResultsAbovePos(ttc, pos);
        taTestReport.testCaseInfo = ttc.getLogString(); 
        return new TaTestReport(status, message);
    }

    private static int getTypeaheadPosition(TaTestCase taTestCase) {
        String tid = taTestCase.getExpectedTypeaheadId();
        List<Typeahead> resultList = taTestCase.getResults();
        if (tid == null || resultList == null) {
            return -1;
        }

        int pos = 0;
        for (int i = 0; i < resultList.size(); i++) {
            if (tid.equals(resultList.get(i).getId())) {
                pos = i;
                return (pos + 1);
            }
        }
        return pos;
    }

    private static List<String> getResultsAbovePos(TaTestCase taTestCase, int pos) {
        List<String> betterResults = new ArrayList<String>();
        List<Typeahead> resultList = taTestCase.getResults();
        Typeahead t;
        for (int i = 0; i < resultList.size() && i < pos - 1; i++) {
            t = resultList.get(i);
            betterResults.add("(" + t.getDisplayText() + ")--(" + t.getId() + ")");
        }

        return betterResults;
    }
}
