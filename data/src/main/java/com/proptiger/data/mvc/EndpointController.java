package com.proptiger.data.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.proptiger.data.meta.DisableCaching;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.util.Constants;

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
    private static final String AMPERSAND = "&";
    private static final String          EQUAL                 = "=";
    private static final String          SQUARE_BRACKET_END    = "]";
    private static final String          SQUARE_BRACKET_START  = "[";
    private static final String          ANGULAR_BRACKET_END   = ">";
    private static final String          ANGULAR_BRACKET_START = "<";
    private static final String          SPACE                 = " ";
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @RequestMapping(value = "/data/apilist", method = RequestMethod.GET)
    @ResponseBody
    public ProAPIResponse show() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = this.handlerMapping.getHandlerMethods();

        List<String> apiList = new ArrayList<String>();

        Iterator<RequestMappingInfo> keyItr = handlerMethodMap.keySet().iterator();
        while (keyItr.hasNext()) {
            
            RequestMappingInfo mappingInfo = keyItr.next();
            Set<String> apiUrls = getAPIUrl(mappingInfo);
            
            for (String url : apiUrls) {
                StringBuilder api = new StringBuilder();
                api.append(getMethodName(api, mappingInfo)).append(SPACE);
                
                api.append(url);

                if(api.toString().contains("data/v1/trend/current")){
                    System.out.println();
                }
                HandlerMethod handlerMethod = handlerMethodMap.get(mappingInfo);
                /*
                 * making shallow copy here as we are not modifying internal
                 * variable/reference of MethodParameter object, just sorting the
                 * array, so new array will be sorted and no internal variable will
                 * be modified that is being referenced by MethodParameter object in
                 * handlerMethod.getMethodParameters() array. So just shallow copy
                 * work here rather than deep copy
                 */
                MethodParameter[] methodParameters = Arrays.copyOf(
                        handlerMethod.getMethodParameters(),
                        handlerMethod.getMethodParameters().length);
                Set<NameValueExpression<String>> paramInfo = null;
                if(mappingInfo.getParamsCondition().getExpressions() != null && !mappingInfo.getParamsCondition().getExpressions().isEmpty()){
                    paramInfo = mappingInfo.getParamsCondition().getExpressions();
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
                        ModelAttribute modelAttribute = methodParameter.getParameterAnnotation(ModelAttribute.class);
                        
                        if(modelAttribute != null && modelAttribute.value().equals(Constants.LOGIN_INFO_OBJECT_NAME)){
                            //no need for user info object in api
                            modelAttribute = null;
                        }
                        
                        if(count == 0 && (requestParam != null || modelAttribute != null)){
                            if (count == 0) {
                                api.append("?");
                                api.append(getParamConditon(paramInfo));
                            }
                        }
                        if(modelAttribute != null){
                            parameterName = methodParameter.getParameterName();
                            //ModelAttribute is mandatory
                            required = true;
                            if (count > 0) {
                                api.append(AMPERSAND);
                            }
                            api.append(SQUARE_BRACKET_START).append(parameterName).append(EQUAL)
                            .append(ANGULAR_BRACKET_START).append(parameterName).append(ANGULAR_BRACKET_END)
                            .append(SQUARE_BRACKET_END);
                            count++;
                        }
                        else if(requestParam != null) {
                            parameterName = getRequestParameterName(methodParameter, requestParam);
                            required = requestParam.required();

                            if (!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
                                required = false;
                            }
                            if (count > 0) {
                                api.append(AMPERSAND);
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

            
        }
        return new ProAPISuccessCountResponse(apiList, apiList.size());
    }

    private String getRequestParameterName(MethodParameter methodParameter, RequestParam requestParam) {
        String parameterName;
        if (requestParam.value() == null || "".equals(requestParam.value())) {
            parameterName = methodParameter.getParameterName();
        }
        else {
            parameterName = requestParam.value();
        }
        return parameterName;
    }

    public String getParamConditon(Set<NameValueExpression<String>> paramInfo){
        StringBuilder val = new StringBuilder("");
        if(paramInfo != null && !paramInfo.isEmpty()){
            int counter = 0;
            for(NameValueExpression<String> expression: paramInfo){
                if(expression.toString().contains("=")){
                    if(counter > 0){
                        val = val.append(AMPERSAND);
                    }
                    val.append(expression.toString());
                }
            }
        }
        if(!val.toString().equals("")){
            val = val.append(AMPERSAND);
        }
        return val.toString();
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
                ModelAttribute modelAttribute1 = o1.getParameterAnnotation(ModelAttribute.class);
                ModelAttribute modelAttribute2 = o2.getParameterAnnotation(ModelAttribute.class);
                if (modelAttribute1 != null && modelAttribute1.value() != null
                        && !modelAttribute1.value().equals(Constants.LOGIN_INFO_OBJECT_NAME)) {
                    return -1;
                }
                else if (modelAttribute2 != null && modelAttribute2.value() != null
                        && !modelAttribute2.value().equals(Constants.LOGIN_INFO_OBJECT_NAME)) {
                    return 1;
                }
                else if (modelAttribute1 != null && modelAttribute1 != null
                        && !modelAttribute1.value().equals(Constants.LOGIN_INFO_OBJECT_NAME)
                        && !modelAttribute2.value().equals(Constants.LOGIN_INFO_OBJECT_NAME)) {
                    return 0;
                }
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
    private  Set<String> getAPIUrl(RequestMappingInfo mappingInfo) {
        Set<String> urls = new HashSet<>();
        Set<String> patterns = mappingInfo.getPatternsCondition().getPatterns();
        for (String str : patterns) {
            urls.add(str);
        }
        return urls;
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
