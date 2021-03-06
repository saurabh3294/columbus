package com.proptiger.columbus.typeahead;

import java.util.List;
import java.util.Map;

import com.proptiger.core.model.Typeahead;

public class TaTestCase {

    protected String              query;
    protected Map<String, String> urlParams;
    protected TaTestCaseType      type;
    protected int                 minRank;
    protected int                 maxRank;
    protected String              expectedTypeaheadId;
    protected String              testUrl;
    private List<Typeahead>       results;

    public TaTestCase(String query, TaTestCaseType type, int minRank, int maxRank, String expectedTypeaheadId) {
        super();
        this.query = query;
        this.type = type;
        this.minRank = minRank;
        this.maxRank = maxRank;
        this.expectedTypeaheadId = expectedTypeaheadId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, String> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(Map<String, String> urlParams) {
        this.urlParams = urlParams;
    }

    public TaTestCaseType getType() {
        return type;
    }

    public void setType(TaTestCaseType type) {
        this.type = type;
    }

    public int getMinRank() {
        return minRank;
    }

    public void setMinRank(int minRank) {
        this.minRank = minRank;
    }

    public int getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(int maxRank) {
        this.maxRank = maxRank;
    }

    public String getExpectedTypeaheadId() {
        return expectedTypeaheadId;
    }

    public void setExpectedTypeaheadId(String expectedTypeaheadId) {
        this.expectedTypeaheadId = expectedTypeaheadId;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public List<Typeahead> getResults() {
        return results;
    }

    public void setResults(List<Typeahead> results) {
        this.results = results;
    }

    public String getLogString() {
        String logString = this.query + ","
                + type
                + ", ("
                + this.minRank
                + "-"
                + this.maxRank
                + "), "
                + this.expectedTypeaheadId
                + ", [ "
                + String.valueOf(urlParams)
                + " ]";
        return logString;
    }
}
