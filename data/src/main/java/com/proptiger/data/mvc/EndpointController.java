package com.proptiger.data.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;

/**
 * This class finds all the API in dal project, with method name, and parameters
 * details
 * 
 * @author Rajeev Pandey
 * 
 */
@Controller
@DisableCaching
public class EndpointController {
    private static Logger             logger = LoggerFactory.getLogger(EndpointController.class);
    private static final String          EQUAL                 = "=";
    private static final String          SQUARE_BRACKET_END    = "]";
    private static final String          SQUARE_BRACKET_START  = "[";
    private static final String          ANGULAR_BRACKET_END   = ">";
    private static final String          ANGULAR_BRACKET_START = "<";
    private static final String          SPACE                 = " ";
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    // @RequestMapping(value = "/data/apilist", method = RequestMethod.GET)
    // @ResponseBody
    public ProAPIResponse show() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = this.handlerMapping.getHandlerMethods();

        List<String> apiList = new ArrayList<String>();

        Iterator<RequestMappingInfo> keyItr = handlerMethodMap.keySet().iterator();
        while (keyItr.hasNext()) {
            StringBuilder api = new StringBuilder();
            RequestMappingInfo mappingInfo = keyItr.next();

            api.append(getMethodName(api, mappingInfo)).append(SPACE);
            api.append(getAPIUrl(mappingInfo));

            HandlerMethod handlerMethod = handlerMethodMap.get(mappingInfo);
            MethodParameter[] methodParameters = null;
            try {
                methodParameters = (MethodParameter[]) BeanUtils.cloneBean(handlerMethod.getMethodParameters());
            }
            catch (Exception e) {
                logger.error("Could not clone method parameters array for method {}",getMethodName(api, mappingInfo));;
            }
            if (methodParameters != null && methodParameters.length > 0) {
                int count = 0;
                sortMethodParamsByRequired(methodParameters);
                for (MethodParameter methodParameter : methodParameters) {
                    StringBuilder parameters = new StringBuilder();
                    boolean required = true;
                    methodParameter.initParameterNameDiscovery(new LocalVariableTableParameterNameDiscoverer());
                    parameters.append(getParameterType(methodParameter)).append(SPACE);
                    String parameterName = null;
                    RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
                    if (requestParam != null) {
                        if (requestParam.value() == null || "".equals(requestParam.value())) {
                            parameterName = methodParameter.getParameterName();
                        }
                        else {
                            parameterName = requestParam.value();
                        }
                        required = requestParam.required();

                        if (!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
                            required = false;
                        }

                        if (count == 0) {
                            api.append("?");
                        }
                        if (count > 0) {
                            api.append("&");
                        }
                        if (!required) {
                            api.append(SQUARE_BRACKET_START).append(parameterName).append(EQUAL)
                                    .append(ANGULAR_BRACKET_START).append(parameterName).append(ANGULAR_BRACKET_END)
                                    .append(SQUARE_BRACKET_END);
                        }
                        else {
                            api.append(parameterName).append(EQUAL).append(ANGULAR_BRACKET_START).append(parameterName)
                                    .append(ANGULAR_BRACKET_END);
                        }
                        count++;
                    }

                }
            }
            apiList.add(api.toString());
        }
        return new ProAPISuccessCountResponse(apiList, apiList.size());
    }

    /**
     * Sort method parameters by required in API
     * 
     * @param methodParameters
     */
    private void sortMethodParamsByRequired(MethodParameter[] methodParameters) {
        Collections.sort(Arrays.asList(methodParameters), new Comparator<MethodParameter>() {
            @Override
            public int compare(MethodParameter o1, MethodParameter o2) {
                RequestParam requestParam1 = o1.getParameterAnnotation(RequestParam.class);
                RequestParam requestParam2 = o2.getParameterAnnotation(RequestParam.class);
                if (requestParam1 != null && requestParam2 != null) {
                    return new Boolean(requestParam2.required()).compareTo(new Boolean(requestParam1.required()));
                }
                return 0;
            }
        });
    }

    /**
     * @param mappingInfo
     * @return
     */
    private String getAPIUrl(RequestMappingInfo mappingInfo) {
        String url = "";
        Set<String> patterns = mappingInfo.getPatternsCondition().getPatterns();
        for (String str : patterns) {
            url = url + str;
        }
        return url;
    }

    /**
     * Get method name, if no method name specified in @RequestMapping then
     * using GET
     * 
     * @param api
     * @param mappingInfo
     * @return
     */
    private String getMethodName(StringBuilder api, RequestMappingInfo mappingInfo) {
        String method = "";
        if (mappingInfo.getMethodsCondition() == null || mappingInfo.getMethodsCondition().getMethods() == null
                || mappingInfo.getMethodsCondition().getMethods().size() == 0) {
            method = "GET";
        }
        else {
            for (RequestMethod m : mappingInfo.getMethodsCondition().getMethods()) {
                method = method + m;
            }
        }
        return method;
    }

    /**
     * Get parameter type of API method, in case of class name with package
     * returning only class name like String instead of java.lang.String
     * 
     * @param methodParameter
     * @return
     */
    private String getParameterType(MethodParameter methodParameter) {
        String type = "";
        if (methodParameter.getParameterType().getName().contains(".")) {
            String[] list = methodParameter.getParameterType().getName().split("\\.");
            type = list[list.length - 1];
        }
        else {
            type = methodParameter.getParameterType().getName();
        }
        return type;
    }
}
