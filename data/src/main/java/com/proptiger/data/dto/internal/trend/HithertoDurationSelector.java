package com.proptiger.data.dto.internal.trend;

import com.proptiger.core.util.DateUtil;


/**
 * This class functions as a selector for duration in Hitherto-Trends. The
 * durations are mutually exclusive in a way that, any of them can be set but
 * only one of them will be valid (by current logic, its the one that was set
 * last) i.e every set operation will override the last one.
 */

public class HithertoDurationSelector {
    /* TODO :: Move these as a public variable in a constants file */
    private static int   MonthsInAYear                 = 12;
    public static int    MonthThresholdForNormalYear   = 6;
    public static int    MonthThresholdForFinacialYear = 6;

    /* Months are numbered 0-11 */
    public static int    StartMonthFinancialYear       = 3;           // April
    public static int    StartMonthYear                = 0;           // January

    private int          monthDuration                 = 0;
    private int          quarterDuration               = 0;
    private int          financialYearDuration         = 0;
    private int          yearDuration                  = 0;

    /* Set by TrendService */
    public static String currentMonth                  = "0000-00-00";

    private String       startMonth                    = "0000-00-00";
    private String       endMonth                      = "0000-00-00";

    private boolean      valid                         = false;

    public HithertoDurationSelector() {
    }

    /**** GET Methods *****/

    public String getStartMonth() {
        return startMonth;
    }

    public String getEndMonth() {
        return endMonth;
    }

    /**** Internal Methods ****/

    /*
     * Following calculations make 2 assumptions : 1. A DB entry for first day
     * of the month contains data for the whole month. 2. Trend service will
     * query [startDay, endDay].
     */
    private void recalulateMonthRangeUsingMonthDuration() {
        endMonth = currentMonth;
        startMonth = DateUtil.subtractMonths(endMonth, monthDuration);
        startMonth = DateUtil.shiftMonths(startMonth, 1);
    }

    private void recalulateMonthRangeUsingQuaterDuration() {
        /* Hack to include the current quarter if last quarter month is set as the cuurent_month */
        String quarterCurrentMonth = DateUtil.shiftMonths(currentMonth, 1);
        
        endMonth = DateUtil.getQuarterStartDateString(quarterCurrentMonth);
        startMonth = DateUtil.subtractMonths(endMonth, quarterDuration * DateUtil.MonthCountInQuarter);
        endMonth = DateUtil.subtractMonths(endMonth, 1);
    }

    private void recalulateMonthRangeUsingNormalYearDuration() {
        int monthNumber = DateUtil.getMonthNumberFromDateString(currentMonth);
        String yearStartMonth = DateUtil.subtractMonths(currentMonth, monthNumber);
        calculateMonthRangeForYearDuration(monthNumber, yearStartMonth, MonthThresholdForNormalYear, yearDuration);
    }

    private void recalulateMonthRangeUsingYearFinancialYearDuration() {
        int monthNumber = DateUtil.getMonthNumberFromDateString(currentMonth);
        int monthsElapsedInFinYear = ((monthNumber - StartMonthFinancialYear + MonthsInAYear) % MonthsInAYear) + 1;
        String yearStartMonth = DateUtil.subtractMonths(currentMonth, (monthsElapsedInFinYear - 1));
        calculateMonthRangeForYearDuration(
                monthsElapsedInFinYear,
                yearStartMonth,
                MonthThresholdForFinacialYear,
                financialYearDuration);
    }

    private void calculateMonthRangeForYearDuration(
            int monthsElapsed,
            String yearStartMonth,
            int monthThreshold,
            int duration) {
        /*
         * If number of months passed in current year are less than the
         * threshold.
         */
        if (monthsElapsed < monthThreshold) {
            /* Shift end month to end of prev year */
            endMonth = DateUtil.subtractMonths(yearStartMonth, 1);
            startMonth = DateUtil.subtractMonths(yearStartMonth, duration * MonthsInAYear);
        }
        else {
            endMonth = currentMonth;
            if (duration == 0) {
                startMonth = currentMonth;
            }
            else {
                startMonth = DateUtil.subtractMonths(yearStartMonth, ((duration - 1) * MonthsInAYear));
            }
        }
    }

    /**** SET Methods *****/

    public void setMonthDuration(int monthDuration) {
        valid = true;
        this.monthDuration = monthDuration;
        recalulateMonthRangeUsingMonthDuration();
    }

    public void setQuarterDuration(int quarterDuration) {
        valid = true;
        this.quarterDuration = quarterDuration;
        recalulateMonthRangeUsingQuaterDuration();
    }

    public void setYearDuration(int yearDuration) {
        valid = true;
        this.yearDuration = yearDuration;
        recalulateMonthRangeUsingNormalYearDuration();
    }

    public void setFinancialYearDuration(int financialYearDuration) {
        valid = true;
        this.financialYearDuration = financialYearDuration;
        recalulateMonthRangeUsingYearFinancialYearDuration();
    }

    public boolean isValid() {
        return valid;
    }
}
