package com.proptiger.data.repo.trend;

import java.util.Date;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.proptiger.data.model.trend.TrendReportLog;

public interface TrendReportLogDao extends PagingAndSortingRepository<TrendReportLog, Integer> {
    
    public int getCompanyDownloadCountBetweenDates(int userId, Date date1, Date date2);
    
}