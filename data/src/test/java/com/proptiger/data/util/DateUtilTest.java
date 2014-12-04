package com.proptiger.data.util;

import static org.testng.AssertJUnit.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.restlet.util.Triple;
import org.testng.annotations.Test;

import com.proptiger.core.service.AbstractTest;
import com.proptiger.core.util.DateUtil;

public class DateUtilTest extends AbstractTest {

    /* 'testCases' Array format :: {{<Input>, <ExpectedOutput>}, {}, ...} */

    /* TODO :: Should we write all cases istead of a loop ?? */

    @Test
    public void TestGetQuarterStartDateString() {
        String[][] testCases = {
                { "2013-11-01", "2013-10-01" },
                { "2014-01-01", "2014-01-01" },
                { "2014-02-01", "2014-01-01" },
                { "2014-06-01", "2014-04-01" },
                { "2014-09-11", "2014-07-01" },
                { "2014-01-01", "2014-01-01" },
                { "2014-01-01", "2014-01-01" } };

        for (int i = 0; i < testCases.length; i++) {
            assertEquals(
                    "Test failed for method GetQuaterStartMonth (date = " + testCases[i][0] + ")",
                    testCases[i][1],
                    DateUtil.getQuarterStartDateString(testCases[i][0]));
        }
    }

    @Test
    public void TestGetMonthNumberFromDateString() {
        String[][] testCases = { { "2013-01-01", "0" }, { "2014-04-04", "3" }, { "2014-07-31", "6" } };

        for (int i = 0; i < testCases.length; i++) {
            assertEquals(
                    "Test failed for method GetMonthNumberFromDateString (date = " + testCases[i][0] + ")",
                    Integer.parseInt(testCases[i][1]),
                    DateUtil.getMonthNumberFromDateString(testCases[i][0]));
        }

    }

    @Test
    @SuppressWarnings("deprecation")
    public void TestGetWorkingTimeAddedIntoDate() {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(formatter.format(date));

        List<Triple<Date, Integer, Date>> fromDateDelayToDate = new ArrayList<>();

        try {
            fromDateDelayToDate.add(new Triple<Date, Integer, Date>(
                    formatter.parse("2014-09-11 11:00:00"),
                    -3600 * 16,
                    formatter.parse("2014-09-09 17:00:00")));
            fromDateDelayToDate.add(new Triple<Date, Integer, Date>(
                    formatter.parse("2014-09-11 22:00:00"),
                    600,
                    formatter.parse("2014-09-12 09:10:00")));
            fromDateDelayToDate.add(new Triple<Date, Integer, Date>(
                    formatter.parse("2014-09-11 05:00:00"),
                    600,
                    formatter.parse("2014-09-11 09:10:00")));
            fromDateDelayToDate.add(new Triple<Date, Integer, Date>(
                    formatter.parse("2014-09-11 22:00:00"),
                    -600,
                    formatter.parse("2014-09-11 19:50:00")));
            fromDateDelayToDate.add(new Triple<Date, Integer, Date>(
                    formatter.parse("2014-09-11 05:00:00"),
                    -600,
                    formatter.parse("2014-09-10 19:50:00")));
            fromDateDelayToDate.add(new Triple<Date, Integer, Date>(
                    formatter.parse("2014-09-11 10:00:00"),
                    -3600 * 2,
                    formatter.parse("2014-09-10 19:00:00")));
            fromDateDelayToDate.add(new Triple<Date, Integer, Date>(
                    formatter.parse("2014-09-11 19:00:00"),
                    3600 * 2,
                    formatter.parse("2014-09-12 10:00:00")));
            fromDateDelayToDate.add(new Triple<Date, Integer, Date>(
                    formatter.parse("2014-09-11 09:00:00"),
                    3600 * 11,
                    formatter.parse("2014-09-12 09:00:00")));
            fromDateDelayToDate.add(new Triple<Date, Integer, Date>(
                    formatter.parse("2014-09-11 19:59:59"),
                    1,
                    formatter.parse("2014-09-12 09:00:00")));
        }
        catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        for (Triple<Date, Integer, Date> triple : fromDateDelayToDate) {
            assertEquals(DateUtil.getWorkingTimeAddedIntoDate(triple.getFirst(), triple.getSecond()), triple.getThird());
        }
    }
}