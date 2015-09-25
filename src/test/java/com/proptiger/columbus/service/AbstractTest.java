package com.proptiger.columbus.service;

import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.gson.Gson;
import com.proptiger.columbus.response.ColumbusAPIResponse;
import com.proptiger.core.config.WebMvcConfig;
import com.proptiger.core.init.CustomObjectMapper;
import com.proptiger.core.mvc.BaseController;
import com.proptiger.core.pojo.response.APIResponse;

/**
 * Extends This abstarct class in test classes to initialize spring system
 * 
 * @author Rajeev Pandey
 * 
 */
@Test
@ContextConfiguration(classes = WebMvcConfig.class)
@WebAppConfiguration
public abstract class AbstractTest extends AbstractTestNGSpringContextTests {

    private CustomObjectMapper mapper;

    @PostConstruct
    private void initilize() {
        mapper = new CustomObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public MockHttpServletResponse mockRequestAndGetResponse(BaseController baseController, String url) {
        MockHttpServletResponse mhsr = null;
        try {
            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(baseController).build();
            mhsr = mockMvc.perform(MockMvcRequestBuilders.get(url)).andReturn().getResponse();
        }
        catch (Exception ex) {
            Assert.assertTrue(false, "Exception while getting mockMvc response. Url = " + url);
        }
        Assert.assertNotNull(mhsr, "Null mockMvc response. Url = " + url);
        return mhsr;
    }

    public APIResponse mockRequestAndGetAPIResponse(BaseController baseController, String url) {
        MockHttpServletResponse mhsr = null;
        String response;
        APIResponse apiResponse = null;
        try {
            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(baseController).build();
            mhsr = mockMvc.perform(MockMvcRequestBuilders.get(url)).andReturn().getResponse();
            Assert.assertNotNull(mhsr, "Null mockMvc response. Url = " + url);
            response = mhsr.getContentAsString();
            apiResponse = mapper.readValue(response, APIResponse.class);
        }
        catch (Exception ex) {
            Assert.assertTrue(false, "Exception while getting response. Url = " + url);
        }
        Assert.assertNotNull(apiResponse, "Null apiResponse. Url = " + url);
        return apiResponse;
    }

    public ColumbusAPIResponse mockRequestAndGetColumbusAPIResponse(BaseController baseController, String url) {
        MockHttpServletResponse mhsr = null;
        String response;
        ColumbusAPIResponse apiResponse = null;
        try {
            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(baseController).build();
            mhsr = mockMvc.perform(MockMvcRequestBuilders.get(url)).andReturn().getResponse();
            Assert.assertNotNull(mhsr, "Null mockMvc response. Url = " + url);
            response = mhsr.getContentAsString();
            Assert.assertTrue(!response.trim().isEmpty());
            apiResponse = mapper.readValue(response, ColumbusAPIResponse.class);
        }
        catch (Exception ex) {
            logger.error(ex);
            Assert.assertTrue(false, "Exception while getting response. Url = " + url);
        }
        Assert.assertNotNull(apiResponse, "Null apiResponse. Url = " + url);
        return apiResponse;
    }

    public <T> List<T> getDataAsObjectList(Object data, Type type) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        List<T> list = gson.fromJson(json, type);
        return list;
    }
}
