package com.proptiger.data.repo.trend;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.trend.TrendReportLog;

public interface TrendReportLogDao extends PagingAndSortingRepository<TrendReportLog, Integer> {
    
    public TrendReportLog getById(int reportId);
    
    @Query("select count(*) from user.users as u "
            + "join fetch proptiger.user_subscription_mappings as usm ON (u.id = usm.user_id and u.id=?1) "
            + "join proptiger.trend_reports as tr On (usm.id = tr.usm_id and download_date>=?1 and download_date<=?3)")
    public int getCompanyDownloadCountBetweenDates(int usmid, Date date1, Date date2);
    
}