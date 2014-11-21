package com.proptiger.data.service.trend;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.cxf.jaxrs.ext.search.ConditionType;
import org.apache.cxf.jaxrs.ext.search.PrimitiveStatement;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.proptiger.core.constants.ResponseCodes;
import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.model.cms.Trend;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.pojo.response.PaginatedResponse;
import com.proptiger.core.util.DateUtil;
import com.proptiger.core.util.UtilityClass;
import com.proptiger.data.model.Catchment;
import com.proptiger.data.model.trend.CatchmentTrendReportElement;
import com.proptiger.data.service.B2BAttributeService;
import com.proptiger.data.service.user.CatchmentService;
import com.proptiger.data.util.FIQLUtils;
import com.proptiger.data.util.MSExcelUtils;

@Service
public class TrendReportService {

    /** Discuss before changing this limit **/
    public static final int     MaxTrendFetchLimit        = 10000;

    /** Discuss before changing this limit **/
    public static final int     MaxObjectFetchLimit       = 10000;

    private static Logger       logger                    = LoggerFactory.getLogger(TrendReportService.class);

    @Autowired
    private B2BAttributeService b2bAttributeService;

    @Value("${b2b.price-inventory.max.month.dblabel}")
    private String              currentMonthDbLabel;

    private Date                b2bCurrentMonth;

    @Autowired
    TrendReportAggregator       trendReportAggregator;

    @Autowired
    private CatchmentService    catchmentService;

    @Autowired
    private TrendService        trendService;

    @Autowired
    MSExcelUtils                msExcelUtils;

    String                      workSheetName             = "sheet1";

    @Value("${path.temp.trend.report}")
    private String              trendReportDirPath;

    private File                trendReportDir;

    private static String       ERR_MSG_InvalidTimePeriod = "Invalid or no time period specified for trend report generation.";

    @PostConstruct
    private void init() {
        trendReportDir = new File(trendReportDirPath);
        if (!trendReportDir.exists()) {
            trendReportDir.mkdir();
        }
        b2bCurrentMonth = DateUtil.parseYYYYmmddStringToDate(b2bAttributeService
                .getAttributeByName(currentMonthDbLabel));
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

        String tempObjStorageFileName = trendReportDir + "/par_temp_"
                + System.currentTimeMillis()
                + "_"
                + (Math.random() * 100000)
                + ".tmp";
        File tempObjStoragefile = new File(tempObjStorageFileName);

        /** Fetch Information from TREND APIs **/
        PaginatedResponse<List<Trend>> paginatedResponse = null;
        List<Trend> trendList = null;
        List<CatchmentTrendReportElement> ctreList = null;

        int fetched = 0;
        while (true) {
            selector.setStart(fetched).setRows(MaxTrendFetchLimit);
            paginatedResponse = trendService.getPaginatedTrend(selector, null, null);
            if (paginatedResponse == null || paginatedResponse.getResults() == null) {
                break;
            }

            trendList = paginatedResponse.getResults();
            fetched += trendList.size();

            if (fetched >= paginatedResponse.getTotalCount() || trendList.isEmpty()) {
                break;
            }
            else {
                removeLastProjectFromTrendList(trendList);
            }

            ctreList = getPaginatedTrendReportByFIQLSelector(trendList, sortedMonthList);
            writeObjectListToFile(ctreList, tempObjStoragefile);
        }

        /** Export WorkBook to report data to File */
        File excelFile;
        try {
            excelFile = convertFileToExcelFile(tempObjStoragefile, sortedMonthList);
            return excelFile;
        }
        catch (IOException ioEx) {
            throw new ProAPIException(ioEx);
        }
    }

    @SuppressWarnings("unchecked")
    private List<CatchmentTrendReportElement> getPaginatedTrendReportByFIQLSelector(
            List<Trend> trendList,
            List<Date> sortedMonthList) {

        if (trendList == null || trendList.isEmpty()) {
            throw new ProAPIException(
                    ResponseCodes.EMPTY_REPORT_GENERATED,
                    "No projects were found for the given search criteria.");
        }

        // DebugUtils.exportToNewDebugFile(DebugUtils.getAsListOfStrings(trendList));

        /** Get trend list as a grouped map **/

        String[] groupFields = { "projectId", "phaseId", "bedrooms" };

        Map<Integer, Object> groupedTrendList = (Map<Integer, Object>) UtilityClass.groupFieldsAsPerKeys(
                trendList,
                Arrays.asList(groupFields));

        /** Fetch CatchmentTrendReportElement list from report aggregator **/
        List<CatchmentTrendReportElement> ctreList = trendReportAggregator.getCatchmentTrendReport(
                groupedTrendList,
                sortedMonthList);

        return ctreList;
    }

    private File convertFileToExcelFile(File file, List<Date> sortedMonthList) throws IOException{

        /** Get Report-Header column names **/
        List<Object[]> reportColumns = CatchmentTrendReportElement.getReportColumns(sortedMonthList);

        String excelFileName = trendReportDir + "/price-and-absorption-report_" + System.currentTimeMillis() + ".xls";
        String excelSheetName = WorkbookUtil.createSafeSheetName(workSheetName);
        Workbook excelWorkbook = new HSSFWorkbook();
        Sheet excelSheet = excelWorkbook.createSheet(excelSheetName);

        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);

        List<List<Object>> reportData = new ArrayList<List<Object>>();
        List<CatchmentTrendReportElement> ctreList = new ArrayList<CatchmentTrendReportElement>();
        CatchmentTrendReportElement ctrelem;
        boolean flag = false;
        while (true) {
            reportData.clear();
            try {
                for (int i = 0; i < MaxObjectFetchLimit; i++) {
                    ctrelem = (CatchmentTrendReportElement) (ois.readObject());
                    ctreList.add(ctrelem);
                }
            }
            catch (EOFException eofEx) {
                flag = true;        // All objects have been read
            }
            catch (ClassNotFoundException clEx){
                ois.close();
                throw new ProAPIException(clEx);
            }
 
            for (CatchmentTrendReportElement ctre : ctreList) {
                reportData.addAll(ctre.getReportRows(sortedMonthList));
            }
            msExcelUtils.appendToMsExcelSheet(excelWorkbook, excelSheet, reportColumns, reportData);

            if (flag) {
                break;
            }
        }
        ois.close();
        fis.close();

        /** Export WorkBook to report data to File */
        File finalExcelReportFile = msExcelUtils.exportWorkBookToFile(excelWorkbook, excelFileName);
        return finalExcelReportFile;
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
     * Returns a sorted list of months based on conditions specified in FIQL
     * Selector.
     * 
     * @param selector
     *            FIQL-Selector
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

        if (endMonth.after(b2bCurrentMonth)) {
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

    /**
     * This method will be called only when fetched-projects-till-now are less
     * than total-result-count. Assumption : one project will not occupy more
     * than @{MaxTrendFetchLimit} rows in trend table.
     **/
    private void removeLastProjectFromTrendList(List<Trend> trendList) {
        if (trendList == null || trendList.isEmpty()) {
            return;
        }

        int last = trendList.size() - 1;
        while (true) {
            if (last <= 0) {
                throw new ProAPIException("Problem while retrieving trend list page-wise. Only one project recieved");
            }
            if (trendList.get(last).getProjectId() == trendList.get(last - 1).getProjectId()) {
                trendList.remove(last);
                last--;
            }
            else {
                trendList.remove(last);
                break;
            }
        }
    }

    private <T> void writeObjectListToFile(List<T> objList, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            for (T t : objList) {
                try {
                    oos.writeObject(t);
                }
                catch (IOException ioex) {
                    logger.error("Exception while serializing object.", ioex);
                }
            }
            oos.close();
            fos.close();
        }
        catch (IOException ex) {
            logger.error("Exception while opening or closing temprary file to serialize CTRE objects.", ex);
        }
    }
}
