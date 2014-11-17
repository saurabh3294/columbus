package com.proptiger.columbus.typeahead;

import java.util.List;

import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.Typeahead;

@Component
public class TaTestReport {

    class TestReport{
        boolean status;
        String message;
        public TestReport(boolean status, String message) {
            super();
            this.status = status;
            this.message = message;
        }
    }

    public TestReport getReport(TaTestCase ttc){
        int pos = getTypeaheadPosition(ttc);
        boolean status = (pos >= ttc.getMinRank() && pos <= ttc.getMaxRank());
        String message;
        if(pos < 0){
            message = "Test = " + ttc.getLogString() + " Result = INVALID TEST CASE";
        }
        else{
            message = "Test = " + ttc.getLogString() + " Result = " + pos;
        }
        return new TestReport(status, message);
    }

    private int getTypeaheadPosition(TaTestCase taTestCase) {
        String tid = taTestCase.getExpectedTypeaheadId();
        List<Typeahead> resultList = taTestCase.getResults();
        if(tid == null || resultList == null){
            return -1;
        }
        
        int pos = 0;
        for(int i=0; i<resultList.size(); i++){
            if(tid.equals(resultList.get(i).getId())){
                pos = i;
                return (pos+1);
            }
        }
        return pos;
    }
    
    
    
}
