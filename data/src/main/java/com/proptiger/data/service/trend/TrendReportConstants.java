package com.proptiger.data.service.trend;

public class TrendReportConstants {

    public static String    ErrMsg_NoProjectsFound              = "No projects were found for the given search criteria.";

    public static String    ErrMsg_MaxProjectLimitExceeded      = "Maximum allowed projects limit exeeded. Please try reducing the number of projects to %s.";

    public static String    ErrMsg_InvalidTimePeriod            = "Invalid or no time period specified for trend report generation.";

    public static String    FinalOutputExcelFileNameFormat      = "Catchment_Report_%s";

    public static String    ErrMsg_DailyDownloadLimitExceeded   = "Daily download limit exceeded";

    public static String    ErrMsg_MonthlyDownloadLimitExceeded = "Monthly download limit exceeded";

    /** How many trend rows to fetch from DB in one iteration **/
    /** Discuss before changing this limit **/
    public static final int PageSize_TrendObjectsFetch          = 10000;

    /** How many objects to fetch from temp-object-storage-file in one iteration **/
    /** Discuss before changing this limit **/
    public static final int PageSize_SerializedObjectsToExcel   = 5000;
}
