package com.proptiger.app.config.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.proptiger.data.internal.dto.ActiveUser;
import com.proptiger.data.pojo.response.APIResponse;
import com.proptiger.data.util.Constants;

/**
 * Auth success handler to manage session and response after authentication. It
 * put the logged in user details to request session so that would be available
 * to controllers
 * 
 * @author Rajeev Pandey
 *
 */
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public AuthSuccessHandler() {
        super();
    }

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication) throws ServletException, IOException {

        ActiveUser userInfo = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof ActiveUser) {
            userInfo = (ActiveUser) principal;
            /*
             * putting in request session so it would be acessible to
             * controllers
             */
            request.getSession().setAttribute(Constants.LOGIN_INFO_OBJECT_NAME, userInfo);
        }
        // String json =
        // "{id:1,email:\"abc@xyz.com\",firstName:\"abc\",lastName:\"xyz\",contactNumber:\"011-29999999\",profileImageUrl:\"http://graph.facebook.com/1603447461/picture?type=large\",company_ids:[1,2,3],dashboards:[{id:1,dashboardType:\"PORTFOLIO\",dashboardDetails:\"__CUSTOM_JSON__\",name:\"portfolio\",totalRow:2,totalColumn:1,userId:57594,createdAt:1386095400000,updatedAt:1386095400000,widgets:[{widgetId:1,widgetRowPosition:1,widgetColumnPosition:1,status:\"MAX\"},{widgetId:2,widgetRowPosition:2,widgetColumnPosition:1,status:\"MAX\"}]},{id:2,dashboardType:\"B2B\",dashboardDetails:\"__CUSTOM_JSON__\",name:\"test\",userId:57594,createdAt:1386095400000,updatedAt:1386095400000}],appDetails:{b2b:{subscriptions:[{sections:[\"Dashboard\",\"Catchment\",\"Builder\"],cities:[{id:1,name:\"Noida\",localities:[{id:1,name:\"Noida Extension\"},{id:2,name:\"Yamuna Express way\"}]},{id:2,name:\"New Delhi\",localities:[{id:3,name:\"Janakpuri\"},{id:4,name:\"Rohini\"}]}],cityCount:2,localityCount:4,projectCount:400,expiryDate:1364754600000,userType:\"locality\"}],preferences:{majorMovementPercentage:{monthly:10,quarterly:10,yearly:10},areaUnit:\"sqft\",lengthUnit:\"m\",catchmentRadius:5000,budgetRange:{2:[1000,2000],3:[1000,1500,2100]},yearType:\"Calendar\"}}}}";
        String json = "{\"statusCode\":\"2XX\",\"data\":{\"id\":1,\"email\":\"abc@xyz.com\",\"firstName\":\"abc\",\"lastName\":\"xyz\",\"contactNumber\":\"011-29999999\",\"profileImageUrl\":\"http://graph.facebook.com/1603447461/picture?type=large\",\"company_ids\":[1,2,3],\"dashboards\":[{\"id\":1,\"dashboardType\":\"PORTFOLIO\",\"dashboardDetails\":\"__CUSTOM_JSON__\",\"name\":\"portfolio\",\"totalRow\":2,\"totalColumn\":1,\"userId\":57594,\"createdAt\":1386095400000,\"updatedAt\":1386095400000,\"widgets\":[{\"widgetId\":1,\"widgetRowPosition\":1,\"widgetColumnPosition\":1,\"status\":\"MAX\"},{\"widgetId\":2,\"widgetRowPosition\":2,\"widgetColumnPosition\":1,\"status\":\"MAX\"}]},{\"id\":2,\"dashboardType\":\"B2B\",\"dashboardDetails\":\"__CUSTOM_JSON__\",\"name\":\"test\",\"userId\":57594,\"createdAt\":1386095400000,\"updatedAt\":1386095400000}],\"appDetails\":{\"b2b\":{\"subscriptions\":[{\"sections\":[\"Dashboard\",\"Catchment\",\"Builder\"],\"cities\":[{\"id\":1,\"name\":\"Noida\",\"localities\":[{\"id\":1,\"name\":\"NoidaExtension\"},{\"id\":2,\"name\":\"YamunaExpressway\"}]},{\"id\":2,\"name\":\"Hyderabad\",\"localities\":[{\"id\":3,\"name\":\"Janakpuri\"},{\"id\":4,\"name\":\"Rohini\"}]},{\"id\":3,\"name\":\"Gurgaon\",\"localities\":[{\"id\":3,\"name\":\"Janakpuri\"},{\"id\":4,\"name\":\"Rohini\"}]},{\"id\":4,\"name\":\"Mumbai\",\"localities\":[{\"id\":3,\"name\":\"Janakpuri\"},{\"id\":4,\"name\":\"Rohini\"}]},{\"id\":5,\"name\":\"Pune\",\"localities\":[{\"id\":3,\"name\":\"Janakpuri\"},{\"id\":4,\"name\":\"Rohini\"}]},{\"id\":6,\"name\":\"Bangalore\",\"localities\":[{\"id\":3,\"name\":\"Janakpuri\"},{\"id\":4,\"name\":\"Rohini\"}]},{\"id\":7,\"name\":\"Chennai\",\"localities\":[{\"id\":3,\"name\":\"Janakpuri\"},{\"id\":4,\"name\":\"Rohini\"}]},{\"id\":8,\"name\":\"Kolkata\",\"localities\":[{\"id\":3,\"name\":\"Janakpuri\"},{\"id\":4,\"name\":\"Rohini\"}]}],\"cityCount\":2,\"localityCount\":4,\"projectCount\":400,\"expiryDate\":1364754600000,\"userType\":\"locality\"}],\"preferences\":{\"majorMovementPercentage\":{\"monthly\":10,\"quarterly\":10,\"yearly\":10},\"areaUnit\":\"sqft\",\"lengthUnit\":\"m\",\"catchmentRadius\":5000,\"budgetRange\":{\"2\":[1000,2000],\"3\":[1000,1500,2100]},\"yearType\":\"Calendar\",\"unitTypes\":[\"Appartment\",\"Villa\"]}}}}}";
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        Object finalJson = gson.fromJson(reader, Object.class);
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter out = response.getWriter();
        out.println(mapper.writeValueAsString(new APIResponse(finalJson)));
        clearAuthenticationAttributes(request);

    }
}
