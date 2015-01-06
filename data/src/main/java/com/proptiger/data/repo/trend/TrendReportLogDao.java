package com.proptiger.data.repo.trend;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.trend.TrendReportLog;

public interface TrendReportLogDao extends PagingAndSortingRepository<TrendReportLog, Integer> {

    public TrendReportLog getById(int reportId);

    /**
     * select count(*) from proptiger.trend_reports as tr join
     * proptiger.user_subscription_mappings as usm ON (tr.usm_id = usm.id) where
     * (usm.id = 2 and download_date>="2014-11-27" and
     * download_date<="2014-11-27");
     **/
    @Query("SELECT COUNT(*) FROM TrendReportLog TR JOIN TR.usms AS USM "
            + "where USM.id = ?1 AND download_date >= ?2 AND download_date <= ?3 AND success=true")
    public long getCompanyDownloadCountBetweenDates(int usmId, Date dateStart, Date dateEnd);

}