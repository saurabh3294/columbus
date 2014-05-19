package com.proptiger.data.init;

import java.io.StringReader;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.proptiger.data.pojo.response.APIResponse;

@Controller
@RequestMapping(value = "data/test.json")
public class testJson {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public APIResponse returnJson(){
        String json = "{id:1,email:\"abc@xyz.com\",firstName:\"abc\",lastName:\"xyz\",contactNumber:\"011-29999999\",profileImageUrl:\"http://graph.facebook.com/1603447461/picture?type=large\",company_ids:[1,2,3],dashboards:[{id:1,dashboardType:\"PORTFOLIO\",dashboardDetails:\"__CUSTOM_JSON__\",name:\"portfolio\",totalRow:2,totalColumn:1,userId:57594,createdAt:1386095400000,updatedAt:1386095400000,widgets:[{widgetId:1,widgetRowPosition:1,widgetColumnPosition:1,status:\"MAX\"},{widgetId:2,widgetRowPosition:2,widgetColumnPosition:1,status:\"MAX\"}]},{id:2,dashboardType:\"B2B\",dashboardDetails:\"__CUSTOM_JSON__\",name:\"test\",userId:57594,createdAt:1386095400000,updatedAt:1386095400000}],appDetails:{b2b:{subscriptions:[{sections:[\"Dashboard\",\"Catchment\",\"Builder\"],cities:[{id:1,name:\"Noida\",localities:[{id:1,name:\"Noida Extension\"},{id:2,name:\"Yamuna Express way\"}]},{id:2,name:\"New Delhi\",localities:[{id:3,name:\"Janakpuri\"},{id:4,name:\"Rohini\"}]}],cityCount:2,localityCount:4,projectCount:400,expiryDate:1364754600000,userType:\"locality\"}],preferences:{majorMovementPercentage:{monthly:10,quarterly:10,yearly:10},areaUnit:\"sqft\",lengthUnit:\"m\",catchmentRadius:5000,budgetRange:{2:[1000,2000],3:[1000,1500,2100]},yearType:\"Calendar\"}}}}";
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        Object finalJson = gson.fromJson(reader, Object.class);
        return new APIResponse(finalJson);
    }
}
