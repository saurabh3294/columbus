package com.proptiger.data.mvc.trend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.dto.internal.trend.HithertoDurationSelector;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.model.trend.Trend;
import com.proptiger.data.mvc.BaseController;
import com.proptiger.data.pojo.FIQLSelector;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.trend.TrendService;
import com.proptiger.data.service.user.CatchmentService;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.UtilityClass;

/**
 * 
 * @author azi
 * 
 */
@Controller
@RequestMapping
@DisableCaching
public class TrendController extends BaseController {
    @Autowired
    private TrendService     trendService;

    @Autowired
    private CatchmentService catchmentService;

    @RequestMapping("data/v1/trend")
    @ResponseBody
    public APIResponse getTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getPaginatedTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("app/v1/trend")
    @ResponseBody
    public APIResponse getAppTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(getResultsWithMandatoryGroupValues(
                trendService.getTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/trend-list")
    @ResponseBody
    public APIResponse getListTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(trendService.getPaginatedTrend(selector, rangeField, rangeValue));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/trend.csv")
    @ResponseBody
    public String getCsvTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return super.getCsvFromMapListAndFIQL(
                trendService.getFlattenedList(trendService.getTrend(selector, rangeField, rangeValue)),
                selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend")
    @ResponseBody
    public APIResponse getCatchmentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getCatchmentPaginatedTrend(selector, rangeField, rangeValue, catchmentId, userInfo),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("/app/v1/entity/user/catchment/{catchmentId}/trend")
    @ResponseBody
    public APIResponse getAppCatchmentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(getResultsWithMandatoryGroupValues(
                trendService.getCatchmentTrend(selector, rangeField, rangeValue, catchmentId, userInfo),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend-list")
    @ResponseBody
    public APIResponse getListCatchmentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(trendService.getCatchmentPaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo));
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend.csv")
    @ResponseBody
    public String getCsvCatchmentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService.getCatchmentTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo)), selector);
    }

    @RequestMapping("data/v1/trend/current")
    @ResponseBody
    public APIResponse getCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getCurrentPaginatedTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("app/v1/trend/current")
    @ResponseBody
    public APIResponse getAppCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(getResultsWithMandatoryGroupValues(
                trendService.getCurrentTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/trend-list/current")
    @ResponseBody
    public APIResponse getListCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(trendService.getCurrentPaginatedTrend(selector, rangeField, rangeValue));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/trend/current.csv")
    @ResponseBody
    public String getCsvCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return super.getCsvFromMapListAndFIQL(
                trendService.getFlattenedList(trendService.getCurrentTrend(selector, rangeField, rangeValue)),
                selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend/current")
    @ResponseBody
    public APIResponse getCatchmentCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(
                getPaginatedResultsWithMandatoryRangeKeys(trendService.getCatchmentCurrentPaginatedTrend(
                        selector,
                        rangeField,
                        rangeValue,
                        catchmentId,
                        userInfo), rangeField, rangeValue, selector));
    }

    @RequestMapping("/app/v1/entity/user/catchment/{catchmentId}/trend/current")
    @ResponseBody
    public APIResponse getAppCatchmentCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(getResultsWithMandatoryGroupValues(
                trendService.getCatchmentCurrentTrend(selector, rangeField, rangeValue, catchmentId, userInfo),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend/current.csv")
    @ResponseBody
    public String getCsvCatchmentCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService.getCatchmentCurrentTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo)), selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend-list/current")
    @ResponseBody
    public APIResponse getListCatchmentCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(trendService.getCatchmentCurrentPaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo));
    }

    @RequestMapping("data/v1/trend/hitherto")
    @ResponseBody
    public APIResponse getHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector) throws Exception {
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getHithertoPaginatedTrend(selector, rangeField, rangeValue, hithertoDurationSelector),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("app/v1/trend/hitherto")
    @ResponseBody
    public APIResponse getAppHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector) throws Exception {
        return new APIResponse(getResultsWithMandatoryGroupValues(
                trendService.getHithertoTrend(selector, rangeField, rangeValue, hithertoDurationSelector),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/trend-list/hitherto")
    @ResponseBody
    public APIResponse getListHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector) throws Exception {
        return new APIResponse(trendService.getHithertoPaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                hithertoDurationSelector));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/trend/hitherto.csv")
    @ResponseBody
    public String getCsvHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector) throws Exception {
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService.getHithertoTrend(
                selector,
                rangeField,
                rangeValue,
                hithertoDurationSelector)), selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend/hitherto")
    @ResponseBody
    public APIResponse getCatchmentHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getCatchmentHithertoPaginatedTrend(
                        selector,
                        rangeField,
                        rangeValue,
                        hithertoDurationSelector,
                        catchmentId,
                        userInfo),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("/app/v1/entity/user/catchment/{catchmentId}/trend/hitherto")
    @ResponseBody
    public APIResponse getAppCatchmentHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(getResultsWithMandatoryGroupValues(trendService.getCatchmentHithertoTrend(
                selector,
                rangeField,
                rangeValue,
                hithertoDurationSelector,
                catchmentId,
                userInfo), rangeField, rangeValue, selector));
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend/hitherto.csv")
    @ResponseBody
    public String getCsvCatchmentHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService.getCatchmentHithertoTrend(
                selector,
                rangeField,
                rangeValue,
                hithertoDurationSelector,
                catchmentId,
                userInfo)), selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend-list/hitherto")
    @ResponseBody
    public APIResponse getListCatchmentHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(trendService.getCatchmentHithertoPaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                hithertoDurationSelector,
                catchmentId,
                userInfo));
    }

    @RequestMapping("data/v1/price-trend")
    public @ResponseBody
    APIResponse getPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getPricePaginatedTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/price-trend-list")
    @ResponseBody
    public APIResponse getListPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(trendService.getPricePaginatedTrend(selector, rangeField, rangeValue));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/price-trend.csv")
    public @ResponseBody
    String getCsvPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return super.getCsvFromMapListAndFIQL(
                trendService.getFlattenedList(trendService.getPriceTrend(selector, rangeField, rangeValue)),
                selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend")
    public @ResponseBody
    APIResponse getCatchmentPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getCatchmentPricePaginatedTrend(selector, rangeField, rangeValue, catchmentId, userInfo),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend.csv")
    public @ResponseBody
    String getCsvCatchmentPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService.getCatchmentPriceTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo)), selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend-list")
    public @ResponseBody
    APIResponse getListCatchmentPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(trendService.getCatchmentPricePaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo));
    }

    @RequestMapping("data/v1/price-trend/current")
    public @ResponseBody
    APIResponse getCurrentPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getCurrentPricePaginatedTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/price-trend-list/current")
    @ResponseBody
    public APIResponse getListCurrentPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(trendService.getCurrentPricePaginatedTrend(selector, rangeField, rangeValue));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/price-trend/current.csv")
    public @ResponseBody
    String getCsvCurrentPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return super.getCsvFromMapListAndFIQL(
                trendService.getFlattenedList(trendService.getCurrentPriceTrend(selector, rangeField, rangeValue)),
                selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend/current")
    public @ResponseBody
    APIResponse getCatchmentCurrentPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getCatchmentCurrentPricePaginatedTrend(
                        selector,
                        rangeField,
                        rangeValue,
                        catchmentId,
                        userInfo),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend/current.csv")
    public @ResponseBody
    String getCsvCatchmentCurrentPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService.getCatchmentCurrentPriceTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo)), selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend-list/current")
    public @ResponseBody
    APIResponse getListCatchmentCurrentPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        return new APIResponse(trendService.getCatchmentCurrentPricePaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo));
    }

    @RequestMapping("data/v1/price-trend/hitherto")
    public @ResponseBody
    APIResponse getHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector) throws Exception {
        return new APIResponse(
                getPaginatedResultsWithMandatoryRangeKeys(trendService.getHithertoPricePaginatedTrend(
                        selector,
                        rangeField,
                        rangeValue,
                        hithertoDurationSelector), rangeField, rangeValue, selector));
    }

    @RequestMapping("data/v1/price-trend-list/hitherto")
    public @ResponseBody
    APIResponse getListHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector) throws Exception {
        return new APIResponse(trendService.getHithertoPricePaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                hithertoDurationSelector));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/price-trend/hitherto")
    public @ResponseBody
    String getCsvHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector) throws Exception {
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService.getHithertoPriceTrend(
                selector,
                rangeField,
                rangeValue,
                hithertoDurationSelector)), selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend/hitherto")
    public @ResponseBody
    APIResponse getCatchmmentHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        return new APIResponse(getPaginatedResultsWithMandatoryRangeKeys(
                trendService.getCatchmentHithertoPricePaginatedTrend(
                        selector,
                        rangeField,
                        rangeValue,
                        hithertoDurationSelector,
                        catchmentId,
                        userInfo),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend-list/hitherto")
    public @ResponseBody
    APIResponse getListCatchmmentHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        return new APIResponse(trendService.getCatchmentHithertoPricePaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                hithertoDurationSelector,
                catchmentId,
                userInfo));
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend/hitherto.csv")
    public @ResponseBody
    String getCsvCatchmmentHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @ModelAttribute HithertoDurationSelector hithertoDurationSelector,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) ActiveUser userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));

        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService
                .getCatchmentHithertoPriceTrend(
                        selector,
                        rangeField,
                        rangeValue,
                        hithertoDurationSelector,
                        catchmentId,
                        userInfo)), selector);
    }

    private PaginatedResponse<Object> getPaginatedResultsWithMandatoryRangeKeys(
            PaginatedResponse<List<Trend>> trends,
            String rangeField,
            String rangeValue,
            FIQLSelector selector) {
        PaginatedResponse<Object> result = new PaginatedResponse<>();
        result.setTotalCount(trends.getTotalCount());
        result.setResults(getResultsWithMandatoryRangeKeys(trends.getResults(), rangeField, rangeValue, selector));
        return result;
    }

    private Object getResultsWithMandatoryRangeKeys(
            List<Trend> trends,
            String rangeField,
            String rangeValue,
            FIQLSelector selector) {
        Object result = null;

        List<String> groupKeys = getGroupKeysFromUserInput(selector, rangeField, rangeValue);

        if (!groupKeys.isEmpty()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> serviceResponse = (Map<String, Object>) UtilityClass.groupFieldsAsPerKeys(
                    trends,
                    getGroupKeysFromUserInput(selector, rangeField, rangeValue));

            if (rangeField != null || rangeValue != null) {
                Set<String> keys = trendService.getRangeValueKeySetFromUserInput(rangeValue);
                Iterator<String> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (!serviceResponse.keySet().contains(key)) {
                        serviceResponse.put(key, new HashMap<>());
                    }
                }
            }
            result = serviceResponse;
        }
        return result;
    }

    /**
     * gets results as hash with all group values being mandatory
     * 
     * @param inventoryPriceTrends
     * @param rangeField
     * @param rangeValue
     * @param selector
     * @return
     */
    private Object getResultsWithMandatoryGroupValues(
            List<Trend> inventoryPriceTrends,
            String rangeField,
            String rangeValue,
            FIQLSelector selector) {

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) getResultsWithMandatoryRangeKeys(
                inventoryPriceTrends,
                rangeField,
                rangeValue,
                selector);

        List<String> groupKeys = getGroupKeysFromUserInput(selector, rangeField, rangeValue);

        if (!groupKeys.isEmpty()) {
            String groupField = new ArrayList<String>(selector.getGroupSet()).get(0);
            Map<String, Set<Object>> allGroupValues = trendService.getAllGroupValues(inventoryPriceTrends, selector);
            LinkedHashMap<String, Object> valuesForDummyObject = new LinkedHashMap<>();
            if (rangeField != null || rangeValue != null) {
                for (String string : response.keySet()) {
                    valuesForDummyObject.put(trendService.RANGE_KEY, string);
                    validateGroupValues(
                            response.get(string),
                            allGroupValues,
                            selector,
                            valuesForDummyObject,
                            groupField);
                }
            }
            else {
                validateGroupValues(response, allGroupValues, selector, valuesForDummyObject, groupField);
            }
        }
        return response;
    }

    /**
     * validates if all group values are present in the result hash at a
     * particular level
     * 
     * @param object
     * @param allGroupValues
     * @param selector
     * @param valuesForDummyObject
     * @param groupField
     * @return
     */
    private Object validateGroupValues(
            Object object,
            Map<String, Set<Object>> allGroupValues,
            FIQLSelector selector,
            LinkedHashMap<String, Object> valuesForDummyObject,
            String groupField) {
        Map<Object, Object> map = (Map<Object, Object>) object;

        boolean nextLevelAvailable = false;
        String nextGroupField = null;
        Iterator<String> iterator = selector.getGroupSet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.equals(groupField) && iterator.hasNext()) {
                nextLevelAvailable = true;
                nextGroupField = iterator.next();
            }
        }

        for (Object key : allGroupValues.get(groupField)) {
            LinkedHashMap<String, Object> valuesForDummyObjectNew = (LinkedHashMap<String, Object>) valuesForDummyObject
                    .clone();
            valuesForDummyObjectNew.put(groupField, key);
            if (!map.containsKey(UtilityClass.getResponseGroupKey(key))) {
                map.put(
                        UtilityClass.getResponseGroupKey(key),
                        new ArrayList<>(Arrays.asList(trendService.getDummyObject(
                                allGroupValues,
                                selector,
                                valuesForDummyObjectNew))));
            }
            else if (nextLevelAvailable) {
                map.put(
                        UtilityClass.getResponseGroupKey(key),
                        validateGroupValues(
                                map.get(key),
                                allGroupValues,
                                selector,
                                valuesForDummyObjectNew,
                                nextGroupField));
            }
        }
        return map;
    }

    /**
     * method to get list of keys for which value needs to be populated in
     * 
     * @param selector
     * @param rangeField
     * @param rangeValue
     * @return
     */
    private List<String> getGroupKeysFromUserInput(FIQLSelector selector, String rangeField, String rangeValue) {
        List<String> result = new ArrayList<>();
        if (rangeField != null && rangeValue != null) {
            result.add(trendService.RANGE_KEY);
        }
        if (selector.getGroup() != null) {
            result.addAll(selector.getGroupSet());
        }
        return result;
    }
}