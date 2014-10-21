package com.proptiger.data.mvc.trend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.repo.trend.TrendReportDao;
import com.proptiger.data.service.trend.TrendReportService;
import com.proptiger.data.util.Constants;
import com.proptiger.exception.ProAPIException;

@Controller
@RequestMapping
public class TrendReportController extends BaseController {

    @Autowired
    TrendReportService trendReportService;

    @Autowired
    TrendReportDao     trendReportDao;

    @RequestMapping("app/v1/trendreport/{catchmentId}")
    @ResponseBody
    public void getTrendReport(
            HttpServletResponse response,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo,
            FIQLSelector selector) throws Exception {

        File file = trendReportService.getCatchmentTrendReport(userInfo, catchmentId, selector);

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
