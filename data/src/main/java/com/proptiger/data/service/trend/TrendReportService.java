package com.proptiger.data.service.trend;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.cxf.jaxrs.ext.search.ConditionType;
import org.apache.cxf.jaxrs.ext.search.PrimitiveStatement;
import org.apache.cxf.jaxrs.ext.search.SearchBean;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;
import org.apache.cxf.jaxrs.ext.search.fiql.FiqlParser;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.model.trend.CatchmentTrendReportElement;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.trend.TrendReportDao;
import com.proptiger.data.util.DateUtil;
import com.proptiger.data.util.MSExcelUtils;
import com.proptiger.exception.ProAPIException;

@Service
public class TrendReportService {

    @Autowired
    TrendReportDao        trendReportDao;

    @Autowired
    MSExcelUtils          msExcelUtils;

    String                workSheetName             = "sheet1";

    @Value("${trendReportTempPath}")
    private String        trendReportDirPath;

    private File          trendReportDir;

    private static String ERR_MSG_InvalidTimePeriod = "Invalid or no time period specified for trend report generation.";

    @PostConstruct
    private void init() {
        trendReportDir = new File(trendReportDirPath);
        if (!trendReportDir.exists()) {
            trendReportDir.mkdir();
        }
    }

    public File getCatchmentTrendReport(ActiveUser userInfo, Integer catchmentId, FIQLSelector selector) {
        List<Date> sortedMonthList = getMonthList(selector);
        if (sortedMonthList == null || sortedMonthList.isEmpty()) {
            throw new ProAPIException(TrendReportService.ERR_MSG_InvalidTimePeriod);
        }

        List<CatchmentTrendReportElement> ctreList = trendReportDao.getCatchmentTrendReport(
                catchmentId,
                selector,
                userInfo);
        List<List<Object>> reportData = new ArrayList<List<Object>>();

        List<Object[]> reportColumns = CatchmentTrendReportElement.getReportColumns(sortedMonthList);

        for (CatchmentTrendReportElement ctre : ctreList) {
            reportData.addAll(ctre.getReportRows(sortedMonthList));
        }

        /* Export to file */

        String reportFileName = trendReportDir + "/trend_report_" + System.currentTimeMillis() + ".xls";
        String sheetName = WorkbookUtil.createSafeSheetName(workSheetName);

        Workbook msExcelWorkbook = msExcelUtils.exportToMsExcelSheet(
                reportFileName,
                sheetName,
                reportColumns,
                reportData);
        File reportFile = msExcelUtils.exportWorkBookToFile(msExcelWorkbook, reportFileName);

        return reportFile;
    }

    private List<Date> getMonthList(FIQLSelector selector) {

        /* Parsing FIQL Selector to get start and end months */

        String filters = selector.getFilters();
        FiqlParser<SearchBean> fiqlParser = new FiqlParser<SearchBean>(SearchBean.class);
        SearchCondition<SearchBean> searchCondition = fiqlParser.parse(filters);
        List<SearchCondition<SearchBean>> list = searchCondition.getSearchConditions();
        Date startMonth = null, endMonth = null;
        for (SearchCondition<SearchBean> sc : list) {
            PrimitiveStatement ps = sc.getStatement();
            String value;
            if (ps != null && ps.getProperty().equals("month")) {
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
        }

        /* Generating Month List in increasing sorted order */

        if (startMonth == null || endMonth == null) {
            throw new ProAPIException(TrendReportService.ERR_MSG_InvalidTimePeriod);
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
