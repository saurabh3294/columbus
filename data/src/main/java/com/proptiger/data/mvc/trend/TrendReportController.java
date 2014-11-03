package com.proptiger.data.mvc.trend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.core.dto.internal.ActiveUser;
import com.proptiger.core.exception.ProAPIException;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.FIQLSelector;
import com.proptiger.core.util.SecurityContextUtils;
import com.proptiger.data.service.trend.TrendReportAggregator;
import com.proptiger.data.service.trend.TrendReportService;

@Controller
@RequestMapping
public class TrendReportController extends BaseController {

    @Autowired
    TrendReportService trendReportService;

    @Autowired
    TrendReportAggregator     trendReportDao;

    @RequestMapping("app/v1/trendreport/")
    @ResponseBody
    public void getTrendReport(
            HttpServletResponse response,
            FIQLSelector selector) throws Exception {

        File file = trendReportService.getTrendReport(selector);
        makeHTTPServletResponse(response, file);
    }
    
    @RequestMapping("app/v1/trendreport/catchment/{catchmentId}")
    @ResponseBody
    public void getTrendReportByCatchmentId(
            HttpServletResponse response,
            @PathVariable Integer catchmentId,
            FIQLSelector selector) throws Exception {

        ActiveUser user = SecurityContextUtils.getActiveUser();
        File file = trendReportService.getTrendReportByCatchmentId(catchmentId, selector, user);
        makeHTTPServletResponse(response, file);
    }

    private void makeHTTPServletResponse(HttpServletResponse response, File file) {
        String fileName = file.getName();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try {
            OutputStream outStream = response.getOutputStream();
            FileInputStream inputStream = new FileInputStream(file);
            IOUtils.copy(inputStream, outStream);
            response.flushBuffer();
            inputStream.close();
            outStream.close();
        }
        catch (IOException ex) {
            throw new ProAPIException("IOError writing file to output stream");
        }
    }
}
