package com.proptiger.columbus.typeahead;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.Typeahead;

@Component
public class TaTestReport {

    class TestReport {
        boolean status;
        String  message;

        public TestReport(boolean status, String message) {
            super();
            this.status = status;
            this.message = message;
        }
    }

    public TestReport getReport(TaTestCase ttc) {
        String message = "";
        int pos = getTypeaheadPosition(ttc);
        if (pos < 0) {
            message = "Test=" + ttc.getLogString() + " : Outcome=[INVALID TEST CASE]";
            return new TestReport(false, message);
        }

        boolean status = (pos >= ttc.getMinRank() && pos <= ttc.getMaxRank());
        if (!status) {
            String betterResults = getResultsAbovePos(ttc, pos).toString();
            message = "Test=" + ttc.getLogString() + " : Outcome=[Position=" + pos + ", " + betterResults + "]";
        }
        else {
            message = "Test=" + ttc.getLogString() + " : Outcome=[Position=" + pos + "]";
        }
        return new TestReport(status, message);
    }

    private int getTypeaheadPosition(TaTestCase taTestCase) {
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

    private List<String> getResultsAbovePos(TaTestCase taTestCase, int pos) {
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
