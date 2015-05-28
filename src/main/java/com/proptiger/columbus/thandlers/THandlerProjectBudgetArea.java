package com.proptiger.columbus.thandlers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.hadoop.util.StringUtils;
import org.springframework.stereotype.Component;

import com.proptiger.columbus.model.TemplateInfo;
import com.proptiger.core.model.Typeahead;

@Component
public class THandlerProjectBudgetArea extends RootTHandler {

    /* BUDGET related fields */
    private int          minBudget                   = 1000000;
    private int          maxBudget                   = 300000000;
    private int[][]      budgetRanges                = {
            { 4000000, 5000000 },
            { 6000000, 8000000 },
            { 10000000, 20000000 }                  };

    private int[]        budgetPointsBelow           = { 5000000, 7000000 };
    private int[]        budgetPointsAbove           = { 10000000, 20000000 };
    private String       budgetUnit                  = "";
    private String       genericBudgetFilter         = "filters?budget=%s,%s";

    /* template for : <projects between> <Rs.> <5000000> to <6000000> in <noida> */
    private String       genericBetweenTextBudget    = "%s %s %s to %s in %s";

    /* template for : <projects below> <Rs.> <5000000> in <noida> */
    private String       genericAboveBelowTextBudget = "%s %s %s in %s";

    /* AREA related fields */

    private int          minArea                     = 100;
    private int          maxArea                     = 10000;
    private int[][]      areaRanges                  = { { 800, 1200 }, { 1400, 1600 } };
    private int[]        areaPointsBelow             = { 800, 1200 };
    private int[]        areaPointsAbove             = { 1400, 2000 };
    private String       areaUnit                    = "sq ft";
    private String       genericAreaFilter           = "filters?size=%s,%s";

    /* template for : <projects between> <1000> to <2000> <SqFt> in <noida> */
    private String       genericBetweenTextArea      = "%s %s to %s %s in %s";

    /* template for : <projects below> <2000> <SqFt> in <noida> */
    private String       genericAboveBelowTextArea   = "%s %s %s in %s";

    private String       selectorFieldBudget         = "primaryOrResaleBudget";
    private String       selectorFieldArea           = "size";

    private TemplateInfo templateInfo;

    @PostConstruct
    public void initialize() {
        /* TODO :: remove hardcoded string from here. */
        templateInfo = templateInfoDao.findByTemplateType("ProjectBudgetArea");
    }

    @Override
    public List<Typeahead> getResults(String query, Typeahead template, String city, int cityId, int rows) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String templateText = template.getTemplateText();
        String keyword = StringUtils.split(templateText, ' ')[1];
        switch (keyword.toLowerCase()) {
            case "below":
            case "under":
                results.addAll(getResultsForAreaUnder(templateText, city, cityId));
                results.addAll(getResultsForBudgetUnder(templateText, city, cityId));
                return results;
            case "above":
                results.addAll(getResultsForAreaAbove(templateText, city, cityId));
                results.addAll(getResultsForBudgetAbove(templateText, city, cityId));
                return results;
            case "between":
                results.addAll(getResultsForAreaBetween(templateText, city, cityId));
                results.addAll(getResultsForBudgetBetween(templateText, city, cityId));
                return results;
            default:
                return results;
        }
    }

    /* BUDGET methods */

    private List<Typeahead> getResultsForBudgetUnder(String templateText, String city, int cityId) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String entityFilter = URLGenerationConstants.getCityLocalityFilter(cityId, null);
        String displayText, redirectUrl, redirectUrlFilters;
        for (int x : budgetPointsBelow) {
            displayText = (String.format(
                    genericAboveBelowTextBudget,
                    templateText,
                    budgetUnit,
                    convertBudgetAmountInWords(x),
                    city));
            redirectUrl = (String.format(URLGenerationConstants.GenericUrlCity, city.toLowerCase()) + String.format(
                    genericBudgetFilter,
                    minBudget,
                    x));
            redirectUrlFilters = String.format(
                    templateInfo.getRedirectUrlFilters(),
                    entityFilter,
                    selectorFieldBudget,
                    minBudget,
                    x);
            results.add(getTypeaheadObjectByIdTextAndURL(
                    getTemplateType(templateText).toString(),
                    displayText,
                    redirectUrl,
                    redirectUrlFilters));
        }
        return results;
    }

    private List<Typeahead> getResultsForBudgetAbove(String templateText, String city, int cityId) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String entityFilter = URLGenerationConstants.getCityLocalityFilter(cityId, null);
        String displayText, redirectUrl, redirectUrlFilters;
        for (int x : budgetPointsAbove) {
            displayText = (String.format(
                    genericAboveBelowTextBudget,
                    templateText,
                    budgetUnit,
                    convertBudgetAmountInWords(x),
                    city));
            redirectUrl = (String.format(URLGenerationConstants.GenericUrlCity, city.toLowerCase()) + String.format(
                    genericBudgetFilter,
                    x,
                    maxBudget));
            redirectUrlFilters = String.format(
                    templateInfo.getRedirectUrlFilters(),
                    entityFilter,
                    selectorFieldBudget,
                    x,
                    maxBudget);
            results.add(getTypeaheadObjectByIdTextAndURL(
                    getTemplateType(templateText).toString(),
                    displayText,
                    redirectUrl,
                    redirectUrlFilters));
        }
        return results;
    }

    private List<Typeahead> getResultsForBudgetBetween(String templateText, String city, int cityId) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String entityFilter = URLGenerationConstants.getCityLocalityFilter(cityId, null);
        String displayText, redirectUrl, redirectUrlFilters;
        for (int[] x : budgetRanges) {
            displayText = (String.format(
                    genericBetweenTextBudget,
                    templateText,
                    budgetUnit,
                    convertBudgetAmountInWords(x[0]),
                    convertBudgetAmountInWords(x[1]),
                    city));
            redirectUrl = (String.format(URLGenerationConstants.GenericUrlCity, city.toLowerCase()) + String.format(
                    genericBudgetFilter,
                    x[0],
                    x[1]));
            redirectUrlFilters = String.format(
                    templateInfo.getRedirectUrlFilters(),
                    entityFilter,
                    selectorFieldBudget,
                    x[0],
                    x[1]);
            results.add(getTypeaheadObjectByIdTextAndURL(
                    getTemplateType(templateText).toString(),
                    displayText,
                    redirectUrl,
                    redirectUrlFilters));
        }
        return results;
    }

    /* AREA methods */

    private List<Typeahead> getResultsForAreaUnder(String templateText, String city, int cityId) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String entityFilter = URLGenerationConstants.getCityLocalityFilter(cityId, null);
        String displayText, redirectUrl, redirectUrlFilters;
        for (int x : areaPointsBelow) {
            displayText = (String.format(genericAboveBelowTextArea, templateText, x, areaUnit, city));
            redirectUrl = (String.format(URLGenerationConstants.GenericUrlCity, city.toLowerCase()) + String.format(
                    genericAreaFilter,
                    minArea,
                    x));
            redirectUrlFilters = String.format(
                    templateInfo.getRedirectUrlFilters(),
                    entityFilter,
                    selectorFieldArea,
                    minArea,
                    x);
            results.add(getTypeaheadObjectByIdTextAndURL(
                    getTemplateType(templateText).toString(),
                    displayText,
                    redirectUrl,
                    redirectUrlFilters));
        }
        return results;
    }

    private List<Typeahead> getResultsForAreaAbove(String templateText, String city, int cityId) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String entityFilter = URLGenerationConstants.getCityLocalityFilter(cityId, null);
        String displayText, redirectUrl, redirectUrlFilters;
        for (int x : areaPointsAbove) {
            displayText = (String.format(genericAboveBelowTextArea, templateText, x, areaUnit, city));
            redirectUrl = (String.format(URLGenerationConstants.GenericUrlCity, city.toLowerCase()) + String.format(
                    genericAreaFilter,
                    x,
                    maxArea));
            redirectUrlFilters = String.format(
                    templateInfo.getRedirectUrlFilters(),
                    entityFilter,
                    selectorFieldArea,
                    x,
                    maxArea);
            results.add(getTypeaheadObjectByIdTextAndURL(
                    getTemplateType(templateText).toString(),
                    displayText,
                    redirectUrl,
                    redirectUrlFilters));
        }
        return results;
    }

    private List<Typeahead> getResultsForAreaBetween(String templateText, String city, int cityId) {
        List<Typeahead> results = new ArrayList<Typeahead>();

        String entityFilter = URLGenerationConstants.getCityLocalityFilter(cityId, null);
        String displayText, redirectUrl, redirectUrlFilters;
        for (int[] x : areaRanges) {
            displayText = (String.format(genericBetweenTextArea, templateText, x[0], x[1], areaUnit, city));
            redirectUrl = (String.format(URLGenerationConstants.GenericUrlCity, city.toLowerCase()) + String.format(
                    genericAreaFilter,
                    x[0],
                    x[1]));
            redirectUrlFilters = String.format(
                    templateInfo.getRedirectUrlFilters(),
                    entityFilter,
                    selectorFieldArea,
                    x[0],
                    x[1]);
            results.add(getTypeaheadObjectByIdTextAndURL(
                    getTemplateType(templateText).toString(),
                    displayText,
                    redirectUrl,
                    redirectUrlFilters));
        }
        return results;
    }

    @Override
    public Typeahead getTopResult(String query, Typeahead template, String city, int cityId) {
        List<Typeahead> results = getResults(query, template, city, cityId, 1);
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
