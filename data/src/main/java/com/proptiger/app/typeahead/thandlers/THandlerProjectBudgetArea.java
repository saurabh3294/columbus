package com.proptiger.app.typeahead.thandlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.util.StringUtils;

import com.proptiger.data.model.Typeahead;

public class THandlerProjectBudgetArea extends RootTHandler {

    /* BUDGET related fields */
    int            minBudget                   = 1000000;
    int            maxBudget                   = 30000000;
    int[][]        budgetRanges                = { { 2000000, 5000000 }, { 5000000, 10000000 }, { 10000000, maxBudget } };

    int[]          budgetPointsBelow           = { 3000000, 5000000 };
    int[]          budgetPointsAbove           = { 5000000, 10000000 };
    String         budgetUnit                  = "Rs.";
    String         genericBudgetFilter         = "filters?budget=%s,%s";

    /* template for : <projects between> <Rs.> <5000000> to <6000000> in <noida> */
    String         genericBetweenTextBudget    = "%s %s %s to %s in %s";

    /* template for : <projects below> <Rs.> <5000000> in <noida> */
    String         genericAboveBelowTextBudget = "%s %s %s in %s";

    /* AREA related fields */

    int            minArea                     = 100;
    int            maxArea                     = 10000;
    int[][]        areaRanges                  = { { 1000, 2000 }, { 2000, 3000 }, { 3000, maxArea } };
    int[]          areaPointsBelow             = { 1500, 2000 };
    int[]          areaPointsAbove             = { 1200, 2000 };
    String         areaUnit                    = "SqFt";
    String         genericAreaFilter           = "filters?size=%s,%s";

    /* template for : <projects between> <1000> to <2000> <SqFt> in <noida> */
    String         genericBetweenTextArea      = "%s %s to %s %s in %s";

    /* template for : <projects below> <2000> <SqFt> in <noida> */
    String         genericAboveBelowTextArea   = "%s %s %s in %s";

    private String genericUrlCity              = "%s-real-estate/";

    @Override
    public List<Typeahead> getResults(Typeahead typeahead, String city, int rows) {
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

        Typeahead typeahead;
        for (int x : budgetPointsBelow) {
            typeahead = new Typeahead();
            typeahead.setDisplayText(String.format(genericAboveBelowTextBudget, templateText, budgetUnit, x, city));
            typeahead.setRedirectUrl(String.format(genericUrlCity, city) + String.format(genericBudgetFilter, minBudget, x));
            results.add(typeahead);
        }
        return results;
    }

    private List<Typeahead> getResultsForBudgetAbove(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        Typeahead typeahead;
        for (int x : budgetPointsAbove) {
            typeahead = new Typeahead();
            typeahead.setDisplayText(String.format(genericAboveBelowTextBudget, templateText, budgetUnit, x, city));
            typeahead.setRedirectUrl(String.format(genericUrlCity, city) + String.format(genericBudgetFilter, x, maxBudget));
            results.add(typeahead);
        }
        return results;
    }

    private List<Typeahead> getResultsForBudgetBetween(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        Typeahead typeahead;
        for (int[] x : budgetRanges) {
            typeahead = new Typeahead();
            typeahead.setDisplayText(String.format(genericBetweenTextBudget, templateText, budgetUnit, x[0], x[1], city));
            typeahead.setRedirectUrl(String.format(genericUrlCity, city) + String.format(genericAreaFilter, x[0], x[1]));
            results.add(typeahead);
        }
        return results;
    }

    /* AREA methods */

    private List<Typeahead> getResultsForAreaUnder(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        Typeahead typeahead;
        for (int x : areaPointsBelow) {
            typeahead = new Typeahead();
            typeahead.setDisplayText(String.format(genericAboveBelowTextArea, templateText, x, areaUnit, city));
            typeahead.setRedirectUrl(String.format(genericUrlCity, city) + String.format(genericAreaFilter, minArea, x));
            results.add(typeahead);
        }
        return results;
    }

    private List<Typeahead> getResultsForAreaAbove(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        Typeahead typeahead;
        for (int x : areaPointsAbove) {
            typeahead = new Typeahead();
            typeahead.setDisplayText(String.format(genericAboveBelowTextArea, templateText, x, areaUnit, city));
            typeahead.setRedirectUrl(String.format(genericUrlCity, city) + String.format(genericAreaFilter, x, maxArea));
            results.add(typeahead);
        }
        return results;
    }

    private List<Typeahead> getResultsForAreaBetween(String templateText, String city) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        Typeahead typeahead;
        for (int[] x : areaRanges) {
            typeahead = new Typeahead();
            typeahead.setDisplayText(String.format(genericBetweenTextArea, templateText, x[0], x[1], areaUnit, city));
            typeahead.setRedirectUrl(String.format(genericUrlCity, city) + String.format(genericAreaFilter, x[0], x[1]));
            results.add(typeahead);
        }
        return results;
    }

    @Override
    public Typeahead getTopResult(Typeahead typeahead, String city) {
        List<Typeahead> results = getResults(typeahead, city, 1);
        if(results.isEmpty()){
            return null;
        }
        else{
            return results.get(0);
        }
    }

}
