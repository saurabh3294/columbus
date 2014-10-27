package com.proptiger.columbus.thandlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.util.StringUtils;

import com.proptiger.columbus.model.Typeahead;

public class THandlerProjectBudgetArea extends RootTHandler {

    /* BUDGET related fields */
    int            minBudget                   = 1000000;
    int            maxBudget                   = 300000000;
    int[][]        budgetRanges                = { { 4000000, 5000000 }, { 6000000, 8000000 }, { 10000000, 20000000 } };

    int[]          budgetPointsBelow           = { 5000000, 7000000 };
    int[]          budgetPointsAbove           = { 10000000, 20000000 };
    String         budgetUnit                  = "";
    String         genericBudgetFilter         = "filters?budget=%s,%s";

    /* template for : <projects between> <Rs.> <5000000> to <6000000> in <noida> */
    String         genericBetweenTextBudget    = "%s %s %s to %s in %s";

    /* template for : <projects below> <Rs.> <5000000> in <noida> */
    String         genericAboveBelowTextBudget = "%s %s %s in %s";

    /* AREA related fields */

    int            minArea                     = 100;
    int            maxArea                     = 10000;
    int[][]        areaRanges                  = { { 800, 1200 }, { 1400, 1600 } };
    int[]          areaPointsBelow             = { 800, 1200 };
    int[]          areaPointsAbove             = { 1400, 2000 };
    String         areaUnit                    = "sq ft";
    String         genericAreaFilter           = "filters?size=%s,%s";

    /* template for : <projects between> <1000> to <2000> <SqFt> in <noida> */
    String         genericBetweenTextArea      = "%s %s to %s %s in %s";

    /* template for : <projects below> <2000> <SqFt> in <noida> */
    String         genericAboveBelowTextArea   = "%s %s %s in %s";

    private String genericUrlCity              = "%s-real-estate/";

    @Override
    public List<Typeahead> getResults(String query, Typeahead typeahead, String city, int rows) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String templateText = typeahead.getTemplateText();
        String keyword = StringUtils.split(templateText, ' ')[1];
        switch (keyword.toLowerCase()) {
            case "below":
            case "under":
                results.addAll(getResultsForAreaUnder(templateText, city));
                results.addAll(getResultsForBudgetUnder(templateText, city));
                return results;
            case "above":
                results.addAll(getResultsForAreaAbove(templateText, city));
                results.addAll(getResultsForBudgetAbove(templateText, city));
                return results;
            case "between":
                results.addAll(getResultsForAreaBetween(templateText, city));
                results.addAll(getResultsForBudgetBetween(templateText, city));
                return results;
            default:
                return results;
        }
    }

    /* BUDGET methods */

    private List<Typeahead> getResultsForBudgetUnder(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String displayText, redirectUrl;
        for (int x : budgetPointsBelow) {
            displayText = (String.format(
                    genericAboveBelowTextBudget,
                    templateText,
                    budgetUnit,
                    convertBudgetAmountInWords(x),
                    city));
            redirectUrl = (String.format(genericUrlCity, city.toLowerCase()) + String.format(
                    genericBudgetFilter,
                    minBudget,
                    x));
            results.add(getTypeaheadObjectByIdTextAndURL(this.getType().toString(), displayText, redirectUrl));
        }
        return results;
    }

    private List<Typeahead> getResultsForBudgetAbove(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String displayText, redirectUrl;
        for (int x : budgetPointsAbove) {
            displayText = (String.format(
                    genericAboveBelowTextBudget,
                    templateText,
                    budgetUnit,
                    convertBudgetAmountInWords(x),
                    city));
            redirectUrl = (String.format(genericUrlCity, city.toLowerCase()) + String.format(
                    genericBudgetFilter,
                    x,
                    maxBudget));
            results.add(getTypeaheadObjectByIdTextAndURL(this.getType().toString(), displayText, redirectUrl));
        }
        return results;
    }

    private List<Typeahead> getResultsForBudgetBetween(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String displayText, redirectUrl;
        for (int[] x : budgetRanges) {
            displayText = (String.format(
                    genericBetweenTextBudget,
                    templateText,
                    budgetUnit,
                    convertBudgetAmountInWords(x[0]),
                    convertBudgetAmountInWords(x[1]),
                    city));
            redirectUrl = (String.format(genericUrlCity, city.toLowerCase()) + String.format(
                    genericBudgetFilter,
                    x[0],
                    x[1]));
            results.add(getTypeaheadObjectByIdTextAndURL(this.getType().toString(), displayText, redirectUrl));
        }
        return results;
    }

    /* AREA methods */

    private List<Typeahead> getResultsForAreaUnder(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String displayText, redirectUrl;
        for (int x : areaPointsBelow) {
            displayText = (String.format(genericAboveBelowTextArea, templateText, x, areaUnit, city));
            redirectUrl = (String.format(genericUrlCity, city.toLowerCase()) + String.format(
                    genericAreaFilter,
                    minArea,
                    x));
            results.add(getTypeaheadObjectByIdTextAndURL(this.getType().toString(), displayText, redirectUrl));
        }
        return results;
    }

    private List<Typeahead> getResultsForAreaAbove(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String displayText, redirectUrl;
        for (int x : areaPointsAbove) {
            displayText = (String.format(genericAboveBelowTextArea, templateText, x, areaUnit, city));
            redirectUrl = (String.format(genericUrlCity, city.toLowerCase()) + String.format(
                    genericAreaFilter,
                    x,
                    maxArea));
            results.add(getTypeaheadObjectByIdTextAndURL(this.getType().toString(), displayText, redirectUrl));
        }
        return results;
    }

    private List<Typeahead> getResultsForAreaBetween(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String displayText, redirectUrl;
        for (int[] x : areaRanges) {
            displayText = (String.format(genericBetweenTextArea, templateText, x[0], x[1], areaUnit, city));
            redirectUrl = (String.format(genericUrlCity, city.toLowerCase()) + String.format(
                    genericAreaFilter,
                    x[0],
                    x[1]));
            results.add(getTypeaheadObjectByIdTextAndURL(this.getType().toString(), displayText, redirectUrl));
        }
        return results;
    }

    @Override
    public Typeahead getTopResult(String query, Typeahead typeahead, String city) {
        List<Typeahead> results = getResults(query, typeahead, city, 1);
        if (results.isEmpty()) {
            return null;
        }
        else {
            return results.get(0);
        }
    }

    private static String convertBudgetAmountInWords(int number) {
        int lakh = 100000;
        int crore = 10000000;

        if (number / crore > 0) {
            return ((number / crore) + " Cr");
        }
        else if (number / lakh > 0) {
            return ((number / lakh) + " Lacs");
        }
        else {
            return String.valueOf(number);
        }
    }

}
