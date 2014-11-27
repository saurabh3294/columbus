package com.proptiger.data.service.trend;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.cxf.jaxrs.ext.search.ConditionType;
import org.apache.cxf.jaxrs.ext.search.PrimitiveStatement;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.util.DateUtil;
import com.proptiger.data.model.Catchment;
import com.proptiger.data.model.trend.CatchmentTrendReportElement;
import com.proptiger.data.service.B2BAttributeService;
import com.proptiger.data.service.user.CatchmentService;
import com.proptiger.data.util.FIQLUtils;
import com.proptiger.data.util.MSExcelUtils;

@Service
public class TrendReportService {

    @Autowired
    private B2BAttributeService b2bAttributeService;

    @Value("${b2b.price-inventory.max.month.dblabel}")
    private String              currentMonthDbLabel;

    private Date                b2bCurrentMonth;

    @Autowired
    TrendReportAggregator        trendReportDao;

    @Autowired
    private CatchmentService catchmentService;
    
    @Autowired
    MSExcelUtils          msExcelUtils;

    String                workSheetName             = "sheet1";

    @Value("${path.temp.trend.report}")
    private String        trendReportDirPath;

    private File          trendReportDir;

    private static String ERR_MSG_InvalidTimePeriod = "Invalid or no time period specified for trend report generation.";

    @PostConstruct
    private void init() {
        trendReportDir = new File(trendReportDirPath);
        if (!trendReportDir.exists()) {
            trendReportDir.mkdir();
        }
        b2bCurrentMonth = DateUtil.parseYYYYmmddStringToDate(b2bAttributeService.getAttributeByName(currentMonthDbLabel));
    }

    public File getTrendReportByCatchmentId(Integer catchmentId, FIQLSelector selector, ActiveUser userInfo) {
        updateFIQLSelectorBasedOnCatchmentId(catchmentId, selector, userInfo);
        return (getTrendReportByFIQLSelector(selector));
    }
    
    public File getTrendReport(FIQLSelector selector) {
        return (getTrendReportByFIQLSelector(selector));
    }

    private File getTrendReportByFIQLSelector(FIQLSelector selector) {
        /** Generate a sorted list of months given in FIQL Selector **/
        List<Date> sortedMonthList = getMonthList(selector);
        if (sortedMonthList == null || sortedMonthList.isEmpty()) {
            throw new ProAPIException(TrendReportService.ERR_MSG_InvalidTimePeriod);
        }

        /** Fetch CatchmentTrendReportElement list from Dao **/
        List<CatchmentTrendReportElement> ctreList = trendReportDao.getCatchmentTrendReport(
                selector,
                sortedMonthList);
        
        /** Get Report-Header column names **/
        List<Object[]> reportColumns = CatchmentTrendReportElement.getReportColumns(sortedMonthList);

        /** Get Report-Data **/
        List<List<Object>> reportData = new ArrayList<List<Object>>();
        for (CatchmentTrendReportElement ctre : ctreList) {
            reportData.addAll(ctre.getReportRows(sortedMonthList));
        }

        /** Format report data as MS-Excel Workbook */
        String reportFileName = trendReportDir + "/price-and-absorption-report_" + System.currentTimeMillis() + ".xls";
        String sheetName = WorkbookUtil.createSafeSheetName(workSheetName);
        Workbook msExcelWorkbook = msExcelUtils.exportToMsExcelSheet(
                reportFileName,
                sheetName,
                reportColumns,
                reportData);
        
        /** Export WorkBook to report data to File*/
        File reportFile = msExcelUtils.exportWorkBookToFile(msExcelWorkbook, reportFileName);
        return reportFile;
        
    }
    
    private void updateFIQLSelectorBasedOnCatchmentId(Integer catchmentId, FIQLSelector selector, ActiveUser userInfo) {
        List<Catchment> catchmentList = catchmentService.getCatchment(new FIQLSelector()
                .addAndConditionToFilter("id==" + catchmentId));
        if (catchmentList.isEmpty()) {
            throw new ProAPIException("Invalid Catchment ID");
        }
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
    }

    /**
     * Returns a sorted list of months based on conditions specified in FIQL Selector.
     * @param selector FIQL-Selector
     * @return
     */
    private List<Date> getMonthList(FIQLSelector selector) {

        /* Parsing FIQL Selector to get start and end months */

        List<PrimitiveStatement> psList = FIQLUtils.getPrimitiveStatementsFromSelector(selector, "month");
        Date startMonth = null, endMonth = null;
        for (PrimitiveStatement ps : psList) {
            String value;
            value = ps.getValue().toString();
            if (ps.getCondition() == ConditionType.GREATER_OR_EQUALS) {
                startMonth = DateUtil.parseYYYYmmddStringToDate(value);
            }
            else if (ps.getCondition() == ConditionType.GREATER_THAN) {
                startMonth = DateUtil.parseYYYYmmddStringToDate(value);
                startMonth = DateUtil.shiftMonths(startMonth, 1);
            }
            else if (ps.getCondition() == ConditionType.LESS_OR_EQUALS) {
                endMonth = DateUtil.parseYYYYmmddStringToDate(value);
            }
            else if (ps.getCondition() == ConditionType.LESS_THAN) {
                endMonth = DateUtil.parseYYYYmmddStringToDate(value);
                endMonth = DateUtil.shiftMonths(endMonth, -1);
            }
        }

        /* Generating Month List in increasing sorted order */

        if (startMonth == null || endMonth == null) {
            throw new ProAPIException(TrendReportService.ERR_MSG_InvalidTimePeriod);
        }
        
        if(endMonth.after(b2bCurrentMonth)){
            endMonth = b2bCurrentMonth;
        }

        List<Date> monthList = new ArrayList<Date>();
        Date month = new Date(startMonth.getTime());
        while (month.compareTo(endMonth) <= 0) {
            monthList.add(month);
            month = DateUtil.shiftMonths(month, 1);
        }

        return monthList;
    }
}