package com.proptiger.data.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.proptiger.data.constants.ResponseErrorMessages;

/**
 * Service class to get result from individual API and put that into a map
 * against url as key
 * 
 * @author Rajeev Pandey
 * 
 */
@Service
public class CompositeAPIService {

    private static final String          FORWARD_SLASH             = "/";
    private static Logger                logger                    = LoggerFactory.getLogger(CompositeAPIService.class);
    @Value("${composite.api.base.url}")
    private String                       BASE_URL;
    private RestTemplate                 restTemplate              = new RestTemplate();

    private List<HandlerMapping>         handlerMappings;
    private List<HandlerAdapter>         handlerAdapters;
    public static final String           HANDLER_MAPPING_BEAN_NAME = "handlerMapping";
    public static final String           HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";

    @Autowired
    private ApplicationContext           context;
    private RequestMappingHandlerMapping handlerMapping;

    @PostConstruct
    public void init() {
        if (context.getApplicationName() != null && !context.getApplicationName().isEmpty()) {
            String contextName = context.getApplicationName().replace(FORWARD_SLASH, "");
            BASE_URL = BASE_URL + contextName + FORWARD_SLASH;
        }
        initHandlerMappings();
        initHandlerAdapters(context);
        handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
    }

    /**
     * Get response for url passed in request parameter and create map of
     * response keeping url as key and response as value
     * 
     * @param apis
     * @return
     */
    public Map<String, Object> getResponseForApis(List<String> apis) {
        Map<String, Object> response = null;
        if (apis != null && apis.size() > 0) {
            response = new HashMap<String, Object>();

            ExecutorService executors = Executors.newFixedThreadPool(apis.size());
            Map<String, Future<Object>> futureObjMap = new HashMap<>();
            for (String api : apis) {
                final String completeUrl = getCompleteUrl(api);
                Future<Object> future = executors.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return restTemplate.getForObject(completeUrl, Object.class);
                    }
                });
                futureObjMap.put(api, future);
            }
            response = new HashMap<>();
            for (String key : futureObjMap.keySet()) {
                Object responseObj = null;
                try {
                    responseObj = futureObjMap.get(key).get();
                }
                catch (InterruptedException | ExecutionException e) {
                    logger.error("Error while geting resource api {}", key, e);
                    ;
                }
                if (responseObj == null) {
                    responseObj = ResponseErrorMessages.SOME_ERROR_OCCURED;
                }
                response.put(key, responseObj);
            }
        }
        return response;
    }

    /**
     * Get complete url. if url passed have forward slash at start then remove
     * that since we already have forward slash in base url part
     * 
     * @param api
     * @return
     */
    private String getCompleteUrl(String api) {

        if (api.startsWith(FORWARD_SLASH)) {
            api = api.replace(FORWARD_SLASH, "");
        }
        return BASE_URL + api;
    }

    /**
     * This method is to use spring's internal architecture to hit required
     * controller for a api
     * 
     * @param request
     * @param response
     * @param apis
     * @return
     */
    private Map<String, Object> getResponseForApisUsingInternalMapping(
            HttpServletRequest request,
            HttpServletResponse response,
            List<String> apis) {
        Map<String, Object> responseMap = null;
        if (apis != null && apis.size() > 0) {
            responseMap = new HashMap<String, Object>();
            for (String api : apis) {
                HandlerMethod handlerMethod = getHandlerMethodByPath(request, api);
                try {
                    HandlerAdapter handlerAdapter = getHandlerAdapter(handlerMethod);
                    handlerAdapter.handle(request, response, handlerMethod);
                    responseMap.put(api, response);
                    System.out.println();
                }
                catch (ServletException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return responseMap;
    }

    public HandlerMethod getHandlerMethodByPath(HttpServletRequest request, String path) {
        Pattern userIdPattern = Pattern.compile("\\{\\w+\\}");
        if (!path.startsWith(FORWARD_SLASH)) {
            path = FORWARD_SLASH + path;
        }
        HandlerMethod handlerMethod = null;
        for (final Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethodTemp = entry.getValue();
            for (final String pattern : requestMappingInfo.getPatternsCondition().getPatterns()) {
                Matcher matcher = userIdPattern.matcher(pattern);
                String modifiedPattern = pattern;
                if (matcher.find()) {
                    modifiedPattern = pattern.replaceAll("\\{\\w+\\}", "\\\\d+");
                }
                path.matches(modifiedPattern);
                if (path.matches(modifiedPattern)) {
                    handlerMethod = handlerMethodTemp;
                    break;
                }
            }
            if (handlerMethod != null) {
                break;
            }
        }
        return handlerMethod;
    }

    private void initHandlerMappings() {
        this.handlerMappings = null;

        Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                context,
                HandlerMapping.class,
                true,
                false);
        if (!matchingBeans.isEmpty()) {
            this.handlerMappings = new ArrayList<HandlerMapping>(matchingBeans.values());
            // We keep HandlerMappings in sorted order.
            OrderComparator.sort(this.handlerMappings);
        }
        if (this.handlerMappings == null) {
            try {
                HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
                this.handlerMappings = Collections.singletonList(hm);
            }
            catch (NoSuchBeanDefinitionException ex) {
                // Ignore, we'll add a default HandlerMapping later.
            }
        }
    }

    private void initHandlerAdapters(ApplicationContext context) {
        this.handlerAdapters = null;

        Map<String, HandlerAdapter> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                context,
                HandlerAdapter.class,
                true,
                false);
        if (!matchingBeans.isEmpty()) {
            this.handlerAdapters = new ArrayList<HandlerAdapter>(matchingBeans.values());
            // We keep HandlerAdapters in sorted order.
            OrderComparator.sort(this.handlerAdapters);
        }
        if (this.handlerAdapters == null) {
            try {
                HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
                this.handlerAdapters = Collections.singletonList(ha);
            }
            catch (NoSuchBeanDefinitionException ex) {
                // Ignore, we'll add a default HandlerAdapter later.
            }
        }
    }

    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        for (HandlerAdapter ha : this.handlerAdapters) {
            if (ha.supports(handler)) {
                return ha;
            }
        }
        throw new ServletException("No adapter for handler [" + handler
                + "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }

}
