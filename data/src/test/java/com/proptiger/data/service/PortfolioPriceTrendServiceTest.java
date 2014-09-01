/**
 * 
 */
package com.proptiger.data.service;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.data.internal.dto.PortfolioPriceTrend;
import com.proptiger.data.internal.dto.PriceDetail;
import com.proptiger.data.internal.dto.ProjectPriceTrend;
import com.proptiger.data.service.user.portfolio.PortfolioPriceTrendService;
import com.proptiger.data.util.DateUtil;

public class PortfolioPriceTrendServiceTest extends AbstractTest{
    @Autowired
    private PortfolioPriceTrendService portfolioPriceTrendService;

    @Test
    public void testLatestEntryDateOfProjectPriceTrend() throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Date trendCurDate = DateUtil.parseYYYYmmddStringToDate(portfolioPriceTrendService.trendCurrentMonth);
        int specifiedMonth = getMonthFromDate(trendCurDate);
        Integer userId = 154993;
        Integer noOfMonths = 9;
        PortfolioPriceTrend priceTrend = portfolioPriceTrendService.getPortfolioPriceTrend(userId, noOfMonths, null);
        List<ProjectPriceTrend> projectPriceTrends = priceTrend.getProjectPriceTrend();
        Iterator<ProjectPriceTrend> it = projectPriceTrends.iterator();
        while (it.hasNext()) {
            ProjectPriceTrend projectPriceTrend = it.next();
            List<PriceDetail> prices = projectPriceTrend.getPrices();
            if (prices != null && !prices.isEmpty()) {
                int month = getMonthFromDate(prices.get(prices.size() - 1).getEffectiveDate());
                Assert.assertEquals(specifiedMonth, month);
            }
        }

        testBeforeSpecifiedDate();
        testAfterSpecifiedDate();
    }

    private void testBeforeSpecifiedDate() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        Field aField = portfolioPriceTrendService.getClass().getDeclaredField("trendCurrentMonth");
        aField.set(portfolioPriceTrendService, "2013-10-01");
        Date trendCurDate = DateUtil.parseYYYYmmddStringToDate(portfolioPriceTrendService.trendCurrentMonth);
        int specifiedMonth = getMonthFromDate(trendCurDate);
        Integer userId = 154993;
        Integer noOfMonths = 9;
        PortfolioPriceTrend priceTrend = portfolioPriceTrendService.getPortfolioPriceTrend(userId, noOfMonths, null);
        List<ProjectPriceTrend> projectPriceTrends = priceTrend.getProjectPriceTrend();
        Iterator<ProjectPriceTrend> it = projectPriceTrends.iterator();
        while (it.hasNext()) {
            ProjectPriceTrend projectPriceTrend = it.next();
            List<PriceDetail> prices = projectPriceTrend.getPrices();
            if (prices != null && !prices.isEmpty()) {
                int month = getMonthFromDate(prices.get(prices.size() - 1).getEffectiveDate());
                Assert.assertEquals(specifiedMonth, month);
            }
        }
    }

    private void testAfterSpecifiedDate() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        Field aField = portfolioPriceTrendService.getClass().getDeclaredField("trendCurrentMonth");
        aField.set(portfolioPriceTrendService, "2014-04-01");
        Date trendCurDate = DateUtil.parseYYYYmmddStringToDate(portfolioPriceTrendService.trendCurrentMonth);
        int specifiedMonth = getMonthFromDate(trendCurDate);
        Integer userId = 154993;
        Integer noOfMonths = 9;
        PortfolioPriceTrend priceTrend = portfolioPriceTrendService.getPortfolioPriceTrend(userId, noOfMonths, null);
        List<ProjectPriceTrend> projectPriceTrends = priceTrend.getProjectPriceTrend();
        Iterator<ProjectPriceTrend> it = projectPriceTrends.iterator();
        while (it.hasNext()) {
            ProjectPriceTrend projectPriceTrend = it.next();
            List<PriceDetail> prices = projectPriceTrend.getPrices();
            if (prices != null && !prices.isEmpty()) {
                int month = getMonthFromDate(prices.get(prices.size() - 1).getEffectiveDate());
                Assert.assertEquals(specifiedMonth, month);
            }
        }
    }

    private int getMonthFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // Month start from 0 so adding 1 will give correct month
        return cal.get(Calendar.MONTH) + 1;
    }
}
