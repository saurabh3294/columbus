package com.proptiger.data.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.proptiger.core.constants.ResponseErrorMessages;
import com.proptiger.data.util.Constants;
import com.proptiger.data.util.URLUtil;
import com.proptiger.exception.BadRequestException;

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
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                /*
                 * Return false in all cases even if there was a error while
                 * handling a url. This is to include original error message
                 * returned from server in the composite api response
                 */
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // this method will never be used because we are returning false
                // in each case from hasError method
            }
        });
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
     * @param request
     * @return
     */
    public Map<String, Object> getResponseForApis(List<String> apis, HttpServletRequest request) {
        if (apis != null && apis.size() > Constants.LIMIT_OF_COMPOSITE_APIs) {
            throw new BadRequestException(ResponseErrorMessages.LIMIT_OF_COMPOSITE_API_EXCEEDED);
        }
        Date start = new Date();
        Map<String, Long> timeTakenByApis = new HashMap<String, Long>();
        Map<String, Object> response = null;
        System.out.println("request:   " + request + "\n");
        Cookie[] requestCookies = request.getCookies();
        String phpsessId = null;
        String jsessionId = null;
        
        if (requestCookies != null) {
            for (Cookie c : requestCookies) {

                if (c.getName().equals(Constants.PHPSESSID_KEY)) {
                    phpsessId = c.getValue();
                }
                else if (c.getName().equals(Constants.JSESSIONID)) {
                    jsessionId = c.getValue();
                }
                else{
                    continue;
                }
            }
        }     
        
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "PHPSESSID=" + phpsessId);
        requestHeaders.add("Cookie", "JSESSIONID=" + jsessionId);

        final HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestHeaders);

        if (apis != null && apis.size() > 0) {
            response = new HashMap<String, Object>();

            ExecutorService executors = Executors.newFixedThreadPool(apis.size());
            Map<String, Future<CallableWithTime>> futureObjMap = new ConcurrentHashMap<String, Future<CallableWithTime>>();
            for (String api : apis) {
                final String completeUrl = URLUtil.getCompleteUrl(api, BASE_URL);
                Future<CallableWithTime> future = executors.submit(new Callable<CallableWithTime>() {
                    @Override
                    public CallableWithTime call() throws Exception {
                        Date start = new Date();
                        URI uri = new URI(completeUrl);
                        ResponseEntity<Object> res = restTemplate.exchange(
                                uri,
                                HttpMethod.GET,
                                requestEntity,
                                Object.class);

                        Date end = new Date();
                        return new CallableWithTime(end.getTime() - start.getTime(), res.getBody());
                    }
                });
                futureObjMap.put(api, future);
            }
            response = new HashMap<>();
            for (String key : futureObjMap.keySet()) {
                Object responseObj = null;
                long timeTaken = 0;
                try {
                    Future<CallableWithTime> future = futureObjMap.get(key);
                    CallableWithTime resWithTime = future.get();
                    timeTaken = resWithTime.getTimeTaken();
                    responseObj = resWithTime.getData();
                }
                catch (InterruptedException | ExecutionException e) {
                    logger.error("Error while geting resource api {}", key, e);
                }
                if (responseObj == null) {
                    responseObj = ResponseErrorMessages.SOME_ERROR_OCCURED;
                }
                timeTakenByApis.put(key.split("\\?")[0], timeTaken);
                response.put(key, responseObj);
            }
            executors.shutdown();
        }
        Date end = new Date();
        logger.debug(
                "Time taken by composite service {} miliseconds and individual API details {}",
                end.getTime() - start.getTime(),
                timeTakenByApis);
        return response;
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

    private static class CallableWithTime {
        private long   timeTaken;
        private Object data;

        public CallableWithTime(long time, Object data) {
            super();
            this.timeTaken = time;
            this.data = data;
        }

        public long getTimeTaken() {
            return timeTaken;
        }

        public Object getData() {
            return data;
        }
    }

}
