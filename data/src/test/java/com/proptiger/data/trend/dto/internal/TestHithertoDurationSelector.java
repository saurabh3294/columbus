package com.proptiger.data.trend.dto.internal;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import com.proptiger.data.dto.internal.trend.HithertoDurationSelector;

public class TestHithertoDurationSelector {

	@Test
	public void test() 
	{
		HithertoDurationSelector hds = new HithertoDurationSelector();
	
		String currentMonth = "";
		String message = "";
		
		/***** TestGroup_1 :: Month duration *****/
		
		currentMonth = "2014-04-01";
		message = "Test for : CurrentMonth = [" + currentMonth + "] & MonthDuration = ";
		HithertoDurationSelector.currentMonth = (currentMonth);
		
		assertEquals(("Validity Test"), false, hds.isValid());
		
		hds.setMonthDuration(0);
		assertEquals((message + "0" + " & StartMonth"), "2014-05-01", hds.getStartMonth());
		assertEquals((message + "0" + " & EndMonth"), "2014-04-01", hds.getEndMonth());
	
		assertEquals(("Validity Test"), true, hds.isValid());
		    
		hds.setMonthDuration(3);
		assertEquals((message + "3" + " & StartMonth"), "2014-02-01", hds.getStartMonth());
		assertEquals((message + "3" + " & EndMonth"), "2014-04-01", hds.getEndMonth());
		
	    
		
		hds.setMonthDuration(17);
		assertEquals((message + "17" + " & StartMonth"), "2012-12-01", hds.getStartMonth());
		assertEquals((message + "17" + " & EndMonth"), "2014-04-01", hds.getEndMonth());
	
		/***** TestGroup_2 :: Quarter duration *****/
		
		currentMonth = "2014-05-01";
		message = "Test for : CurrentMonth = [" + currentMonth + "] & QuarterDuration = ";
		HithertoDurationSelector.currentMonth = (currentMonth);
		
		hds.setQuarterDuration(0);
		assertEquals((message + "0" + " & StartMonth"), "2014-04-01", hds.getStartMonth());
		assertEquals((message + "0" + " & EndMonth"), "2014-03-01", hds.getEndMonth());

		/* within same year */
		hds.setQuarterDuration(1);
		assertEquals((message + "1" + " & StartMonth"), "2014-01-01", hds.getStartMonth());
		assertEquals((message + "1" + " & EndMonth"), "2014-03-01", hds.getEndMonth());
		
		/* spanning multiple years */
		hds.setQuarterDuration(10);
		assertEquals((message + "10" + " & StartMonth"), "2011-10-01", hds.getStartMonth());
		assertEquals((message + "10" + " & EndMonth"), "2014-03-01", hds.getEndMonth());

		/***** TestGroup_3 :: Year duration *****/
		
		currentMonth = "2014-06-01";
		message = "Test for : CurrentMonth = [" + currentMonth + "] & YearDuration = ";
		HithertoDurationSelector.currentMonth = currentMonth;
		HithertoDurationSelector.MonthThresholdForNormalYear = 5;
		
		hds.setYearDuration(0);
		assertEquals((message + "0" + " & Threshold = 5 & StartMonth"), "2014-06-01", hds.getStartMonth());
		assertEquals((message + "0" + " & Threshold = 5 & EndMonth"), "2014-06-01", hds.getEndMonth());

		hds.setYearDuration(1);
		assertEquals((message + "1" + " & Threshold = 5 & StartMonth"), "2014-01-01", hds.getStartMonth());
		assertEquals((message + "1" + " & Threshold = 5 & EndMonth"), "2014-06-01", hds.getEndMonth());

		hds.setYearDuration(2);
		assertEquals((message + "2" + " & Threshold = 5 & StartMonth"), "2013-01-01", hds.getStartMonth());
		assertEquals((message + "2" + " & Threshold = 5 & EndMonth"), "2014-06-01", hds.getEndMonth());

		HithertoDurationSelector.MonthThresholdForNormalYear = 9;
		
		hds.setYearDuration(2);
		assertEquals((message + "2" + " & Threshold = 9 & StartMonth"), "2012-01-01", hds.getStartMonth());
		assertEquals((message + "2" + " & Threshold = 9 & EndMonth"), "2013-12-01", hds.getEndMonth());
		
		/***** TestGroup_4 :: Financial Year duration *****/
		
		currentMonth = "2014-09-01";
		message = "Test for : CurrentMonth = [" + currentMonth + "] & FinancialYearDuration = ";
		
		HithertoDurationSelector.currentMonth = (currentMonth);
		HithertoDurationSelector.MonthThresholdForFinacialYear = 5;
		
		hds.setFinancialYearDuration(0);
		assertEquals((message + "0" + " & Threshold = 5 & StartMonth"), "2014-09-01", hds.getStartMonth());
		assertEquals((message + "0" + " & Threshold = 5 & EndMonth"), "2014-09-01", hds.getEndMonth());

		hds.setFinancialYearDuration(1);
		assertEquals((message + "1" + " & Threshold = 5 & StartMonth"), "2014-04-01", hds.getStartMonth());
		assertEquals((message + "1" + " & Threshold = 5 & EndMonth"), "2014-09-01", hds.getEndMonth());

		hds.setFinancialYearDuration(2);
		assertEquals((message + "2" + " & Threshold = 5 & StartMonth"), "2013-04-01", hds.getStartMonth());
		assertEquals((message + "2" + " & Threshold = 5 & EndMonth"), "2014-09-01", hds.getEndMonth());

		currentMonth = "2014-02-01";
		message = "Test for : CurrentMonth = [" + currentMonth + "] & FinancialYearDuration = ";
		HithertoDurationSelector.currentMonth = (currentMonth);
		
		hds.setFinancialYearDuration(2);
		assertEquals((message + "2" + " & Threshold = 5 & StartMonth"), "2012-04-01", hds.getStartMonth());
		assertEquals((message + "2" + " & Threshold = 5 & EndMonth"), "2014-02-01", hds.getEndMonth());
		
		HithertoDurationSelector.MonthThresholdForFinacialYear = 12;
		
		hds.setFinancialYearDuration(2);
		assertEquals((message + "2" + " & Threshold = 11 & StartMonth"), "2011-04-01", hds.getStartMonth());
		assertEquals((message + "2" + " & Threshold = 11 & EndMonth"), "2013-03-01", hds.getEndMonth());
		
		
	}

}
