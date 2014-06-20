package com.proptiger.data.util;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

public class DateUtilTest {

	/* 'testCases' Array format :: {{<Input>, <ExpectedOutput>}, {}, ...} */
	
	/* TODO :: Should we write all cases istead of a loop ?? */
	
	@Test
	public void TestGetQuarterStartDateString() 
	{
		String[][] testCases = {{"2013-11-01", "2013-10-01"},
								{"2014-01-01", "2014-01-01"},
								{"2014-02-01", "2014-01-01"},
								{"2014-06-01", "2014-04-01"},
								{"2014-09-11", "2014-07-01"},
								{"2014-01-01", "2014-01-01"},
								{"2014-01-01", "2014-01-01"}};
		
		for(int i=0; i<testCases.length; i++)
		{
			assertEquals("Test failed for method GetQuaterStartMonth (date = " + testCases[i][0] + ")", 
							testCases[i][1], DateUtil.getQuarterStartDateString(testCases[i][0]));
		}
	}
	
	@Test
	public void TestGetMonthNumberFromDateString()
	{
		String[][] testCases = {{"2013-01-01", "0"},
								{"2014-04-04", "3"},
								{"2014-07-31", "6"}};

		for(int i=0; i<testCases.length; i++)
		{
			assertEquals("Test failed for method GetMonthNumberFromDateString (date = " + testCases[i][0] + ")", 
					Integer.parseInt(testCases[i][1]), DateUtil.getMonthNumberFromDateString(testCases[i][0]));
		}
		
	}
}
