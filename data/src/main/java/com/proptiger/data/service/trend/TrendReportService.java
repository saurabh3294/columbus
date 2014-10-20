package com.proptiger.data.service.trend;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.util.WorkbookUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.trend.CatchmentTrendReportElement;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.trend.TrendReportDao;
import com.proptiger.data.util.ApachePoiUtils;

@Service
public class TrendReportService {
    
    @Autowired
    TrendReportDao trendReportDao;
    
    @Autowired
    ApachePoiUtils apachePoiUtils;
    
    String workSheetName = "sheet1";
    
    public File getCatchmentTrendReport(ActiveUser userInfo, Integer catchmentId, FIQLSelector selector)
    {
        
        String workBookName = "trend_report_" + System.currentTimeMillis() + ".xls";
        String sheetName = WorkbookUtil.createSafeSheetName(workSheetName);
        
        List<CatchmentTrendReportElement> ctreList = trendReportDao.getCatchmentTrendReport(catchmentId, selector, userInfo);
        List<List<Object>> reportData = new ArrayList<List<Object>>();
        
        List<String> sortedMonthList = getMonthList(selector);
        
        List<Object[]> reportColumns = CatchmentTrendReportElement.getReportColumns(sortedMonthList);

        for(CatchmentTrendReportElement ctre : ctreList){
            reportData.addAll(ctre.getReportRows(sortedMonthList));
        }

        File file = apachePoiUtils.exportToMsExcelSheet(workBookName, sheetName, reportColumns, reportData);
        return file;
    }

    private List<String> getMonthList(FIQLSelector selector) {
        
//        List<String> sortedMonthList = new List<String>();
//        
//        String startMonth;
//        String endMonth;
//        for(String field : selector.getFieldSet()){
//            if
//        }
//        return null;
        
        return (Arrays.asList(new String[]{"2014-02-01", "2014-03-01", "2014-04-01", "2014-05-01", "2014-06-01"}));
    }
    
}
