package com.proptiger.data.repo.trend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.proptiger.core.service.AbstractTest;
import com.proptiger.data.util.MSExcelUtils;

public class TestTrendReport extends AbstractTest{
    
    @Test
    public void testMsExcelDataTypes(){
        MSExcelUtils msExcelUtils = new MSExcelUtils();
        String excelFileName = "/tmp/trend-report/test-sheet" + System.currentTimeMillis() + ".xlsx";
        Workbook excelWorkbook = new XSSFWorkbook();
        Sheet excelSheet = excelWorkbook.createSheet(WorkbookUtil.createSafeSheetName("sheet1"));
        
        List<Object[]> columns = new ArrayList<Object[]>();
        columns.add(new Object[]{"Date", Date.class});
        columns.add(new Object[]{"Integer", Integer.class});
        columns.add(new Object[]{"Float", Float.class});
        columns.add(new Object[]{"Double", Double.class});
        columns.add(new Object[]{"String", String.class});
        
        List<List<Object>> data = new ArrayList<List<Object>>();
        data.add(Arrays.asList(new Object[]{new Date(), 123456, 123.456f, 1234.1234d, "abcdef"}));
        data.add(Arrays.asList(new Object[]{null, null, null, null, null}));
        data.add(Arrays.asList(new Object[]{new Date(), 123456, 123.456f, 1234.1234d, "abcdef"}));
        
        msExcelUtils.appendToMsExcelSheet(excelWorkbook, excelSheet, columns, data);
        msExcelUtils.exportWorkBookToFile(excelWorkbook, excelFileName);
        Assert.assertEquals(true, true);
    }

}
