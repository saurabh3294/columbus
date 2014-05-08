package com.proptiger.data.mvc.b2b;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.model.b2b.InventoryPriceTrend;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.service.b2b.CatchmentService;
import com.proptiger.data.service.b2b.TrendService;
import com.proptiger.data.util.Constants;

@Controller
@RequestMapping
public class TrendController extends BaseController {
    @Value("${b2b.price-inventory.max.month}")
    private String           currentMonth;

    @Autowired
    private TrendService     trendService;

    @Autowired
    private CatchmentService catchmentService;

    @RequestMapping("data/v1/trend")
    @ResponseBody
    public APIResponse getTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {

        Object response = new Object();
        if (rangeField == null || rangeValue == null) {
            response = super.groupFieldsAsPerSelector(trendService.getTrend(selector), selector);
        }
        else {
            Map<String, List<InventoryPriceTrend>> serviceResponse = trendService.getBudgetSplitTrend(
                    selector,
                    rangeField,
                    rangeValue);
            Map<String, Object> finalResponse = new HashMap<>();
            for (String key : serviceResponse.keySet()) {
                finalResponse.put(key, super.groupFieldsAsPerSelector(serviceResponse.get(key), selector));
            }
            response = finalResponse;
        }
        return new APIResponse(response);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend")
    @ResponseBody
    public APIResponse getUserTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        return getTrends(selector, rangeField, rangeValue);
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/trend.csv")
    @ResponseBody
    public String getCsvTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {

        List<InventoryPriceTrend> response = new ArrayList<>();
        if (rangeField == null || rangeValue == null) {
            response = trendService.getTrend(selector);
        }
        else {
            Map<String, List<InventoryPriceTrend>> serviceResponse = trendService.getBudgetSplitTrend(
                    selector,
                    rangeField,
                    rangeValue);
            for (String key : serviceResponse.keySet()) {
                response.addAll(serviceResponse.get(key));
            }
            selector.addGroupByAtBeginning("rangeValue");
        }
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(response), selector);
    }

    @RequestMapping("data/v1/trend/current")
    @ResponseBody
    public APIResponse getCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return getTrends(getCurrentDateAppendedSelector(selector), rangeField, rangeValue);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend/current")
    @ResponseBody
    public APIResponse getUserCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        return getCurrentTrend(getCurrentDateAppendedSelector(selector), rangeField, rangeValue);
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/trend/current.csv")
    @ResponseBody
    public String getCsvCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return getCsvTrends(getCurrentDateAppendedSelector(selector), rangeField, rangeValue);
    }

    @RequestMapping("data/v1/trend/hitherto")
    @ResponseBody
    public APIResponse getHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return getTrends(getHithertoDateAppendedSelector(selector, monthDuration), rangeField, rangeValue);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend/hitherto")
    @ResponseBody
    public APIResponse getUserHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        return getHithertoTrend(getCurrentDateAppendedSelector(selector), rangeField, rangeValue, monthDuration);
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/trend/hitherto.csv")
    @ResponseBody
    public String getCsvHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return getCsvTrends(getHithertoDateAppendedSelector(selector, monthDuration), rangeField, rangeValue);
    }

    @RequestMapping("data/v1/price-trend")
    public @ResponseBody
    APIResponse getPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return getTrends(getDominantSupplyAppendedSelector(selector), rangeField, rangeValue);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend")
    public @ResponseBody
    APIResponse getUserPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        return getPriceTrends(selector, rangeField, rangeValue);
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/price-trend.csv")
    public @ResponseBody
    String getCsvPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return getCsvTrends(getDominantSupplyAppendedSelector(selector), rangeField, rangeValue);
    }

    @RequestMapping("data/v1/price-trend/current")
    public @ResponseBody
    APIResponse getCurrentPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return getTrends(
                getDominantSupplyAppendedSelector(getCurrentDateAppendedSelector(selector)),
                rangeField,
                rangeValue);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend/current")
    public @ResponseBody
    APIResponse getUserCurrentPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        return getCurrentPriceTrends(selector, rangeField, rangeValue);
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/price-trend/current.csv")
    public @ResponseBody
    String getCsvCurrentPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return getCsvTrends(
                getDominantSupplyAppendedSelector(getCurrentDateAppendedSelector(selector)),
                rangeField,
                rangeValue);
    }

    @RequestMapping("data/v1/price-trend/hitherto")
    public @ResponseBody
    APIResponse getHithertoPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return getTrends(
                getDominantSupplyAppendedSelector(getHithertoDateAppendedSelector(selector, monthDuration)),
                rangeField,
                rangeValue);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend/hitherto")
    public @ResponseBody
    APIResponse getUserHithertoPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @RequestParam Integer monthDuration,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        return getHithertoPriceTrends(selector, rangeField, rangeValue, monthDuration);
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/price-trend/hitherto")
    public @ResponseBody
    String getCsvHithertoPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return getCsvTrends(
                getDominantSupplyAppendedSelector(getHithertoDateAppendedSelector(selector, monthDuration)),
                rangeField,
                rangeValue);
    }

    private FIQLSelector getDominantSupplyAppendedSelector(FIQLSelector selector) {
        selector.addAndConditionToFilter("unitType==" + trendService.getDominantSupply(selector));
        selector.addField("unitType");
        return selector;
    }

    private FIQLSelector getCurrentDateAppendedSelector(FIQLSelector selector) {
        selector.addAndConditionToFilter("month==" + currentMonth);
        return selector;
    }

    private FIQLSelector getHithertoDateAppendedSelector(FIQLSelector selector, Integer monthDuration) {
        selector.addAndConditionToFilter("month=le=" + currentMonth);
        if (monthDuration != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = format.parse(currentMonth);
            }
            catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, -1 * monthDuration);
            selector.addAndConditionToFilter("month=gt=" + format.format(calendar.getTime()));
        }
        return selector;
    }
}