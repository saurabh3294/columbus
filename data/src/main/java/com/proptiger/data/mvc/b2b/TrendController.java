package com.proptiger.data.mvc.b2b;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.proptiger.data.pojo.response.PaginatedResponse;
import com.proptiger.data.service.b2b.CatchmentService;
import com.proptiger.data.service.b2b.TrendService;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.UtilityClass;

@Controller
@RequestMapping
public class TrendController extends BaseController {
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
        return new APIResponse(getMappedResults(
                trendService.getPaginatedTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/trend-list")
    @ResponseBody
    public APIResponse getListTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(trendService.getPaginatedTrend(selector, rangeField, rangeValue));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/trend.csv")
    @ResponseBody
    public String getCsvTrends(
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
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        return new APIResponse(trendService.getCatchmentPaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo));
    }

    @RequestMapping("data/v1/trend/current")
    @ResponseBody
    public APIResponse getCurrentTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(getMappedResults(
                trendService.getCurrentPaginatedTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/trend/current-list")
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
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        return new APIResponse(
                getMappedResults(trendService.getCurrentCatchmentPaginatedTrend(
                        selector,
                        rangeField,
                        rangeValue,
                        catchmentId,
                        userInfo), rangeField, rangeValue, selector));
    }

    @RequestMapping("data/v1/trend/hitherto")
    @ResponseBody
    public APIResponse getHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return new APIResponse(getMappedResults(
                trendService.getHithertoPaginatedTrend(selector, rangeField, rangeValue, monthDuration),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/trend/hitherto-list")
    @ResponseBody
    public APIResponse getListHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return new APIResponse(trendService.getHithertoPaginatedTrend(selector, rangeField, rangeValue, monthDuration));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/trend/hitherto.csv")
    @ResponseBody
    public String getCsvHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService.getHithertoTrend(
                selector,
                rangeField,
                rangeValue,
                monthDuration)), selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/trend/hitherto")
    @ResponseBody
    public APIResponse getCatchmentHithertoTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration,
            @PathVariable Integer catchmentId,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        return new APIResponse(getMappedResults(trendService.getHithertoCatchmentPaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                monthDuration,
                catchmentId,
                userInfo), rangeField, rangeValue, selector));
    }

    @RequestMapping("data/v1/price-trend")
    public @ResponseBody
    APIResponse getPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(getMappedResults(
                trendService.getPricePaginatedTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/trend/data/v1/price-trend-list")
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
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        return new APIResponse(getMappedResults(
                trendService.getCatchmentPricePaginatedTrend(selector, rangeField, rangeValue, catchmentId, userInfo),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/price-trend/current")
    public @ResponseBody
    APIResponse getCurrentPriceTrends(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(getMappedResults(
                trendService.getCurrentPricePaginatedTrend(selector, rangeField, rangeValue),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/trend/data/v1/price-trend-list/current")
    @ResponseBody
    public APIResponse getCurrentListPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue) throws Exception {
        return new APIResponse(trendService.getCurrentPricePaginatedTrend(selector, rangeField, rangeValue));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/price-trend/current.csv")
    public @ResponseBody
    String getCsvCurrentPriceTrends(
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
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        return new APIResponse(getMappedResults(trendService.getCatchmentCurrentPricePaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                catchmentId,
                userInfo), rangeField, rangeValue, selector));
    }

    @RequestMapping("data/v1/price-trend/hitherto")
    public @ResponseBody
    APIResponse getHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return new APIResponse(getMappedResults(
                trendService.getHithertoPricePaginatedTrend(selector, rangeField, rangeValue, monthDuration),
                rangeField,
                rangeValue,
                selector));
    }

    @RequestMapping("data/v1/price-trend-list/hitherto")
    public @ResponseBody
    APIResponse getListHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return new APIResponse(trendService.getHithertoPricePaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                monthDuration));
    }

    @RequestMapping(produces = "text/csv; charset=utf-8", value = "data/v1/price-trend/hitherto")
    public @ResponseBody
    String getCsvHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @RequestParam(required = false) Integer monthDuration) throws Exception {
        return super.getCsvFromMapListAndFIQL(trendService.getFlattenedList(trendService.getHithertoPriceTrend(
                selector,
                rangeField,
                rangeValue,
                monthDuration)), selector);
    }

    @RequestMapping("/data/v1/entity/user/catchment/{catchmentId}/price-trend/hitherto")
    public @ResponseBody
    APIResponse getCatchmmentHithertoPriceTrend(
            @ModelAttribute FIQLSelector selector,
            @RequestParam(required = false) String rangeField,
            @RequestParam(required = false) String rangeValue,
            @PathVariable Integer catchmentId,
            @RequestParam Integer monthDuration,
            @ModelAttribute(Constants.LOGIN_INFO_OBJECT_NAME) UserInfo userInfo) throws Exception {
        selector.addAndConditionToFilter(catchmentService.getCatchmentFIQLFilter(catchmentId, userInfo));
        return new APIResponse(getMappedResults(trendService.getCatchmentHithertoPricePaginatedTrend(
                selector,
                rangeField,
                rangeValue,
                monthDuration,
                catchmentId,
                userInfo), rangeField, rangeValue, selector));
    }

    private PaginatedResponse<Object> getMappedResults(
            PaginatedResponse<List<InventoryPriceTrend>> inventoryPriceTrends,
            String rangeField,
            String rangeValue,
            FIQLSelector selector) {
        PaginatedResponse<Object> result = new PaginatedResponse<>();
        result.setResults(UtilityClass.groupFieldsAsPerKeys(
                inventoryPriceTrends.getResults(),
                getGroupKeysFromUserInput(selector, rangeField, rangeValue)));
        result.setTotalCount(inventoryPriceTrends.getTotalCount());
        return result;
    }

    private List<String> getGroupKeysFromUserInput(FIQLSelector selector, String rangeField, String rangeValue) {
        List<String> result = new ArrayList<>();
        if (rangeField != null && rangeValue != null) {
            result.add(trendService.RANGE_KEY);
        }
        if (selector.getGroup() != null) {
            result.addAll(Arrays.asList(selector.getGroup().split(",")));
        }
        return result;
    }
}