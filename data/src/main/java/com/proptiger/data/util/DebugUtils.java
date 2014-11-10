package com.proptiger.data.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class DebugUtils {

    public static <T> List<String> getAsListOfStrings(List<T> abcd){
        Gson gson = new GsonBuilder().serializeNulls().create();
        Type typeOfSrc = new TypeToken<Collection<T>>(){}.getType();
        JsonArray jsonArr = new Gson().fromJson(gson.toJson(abcd, typeOfSrc), JsonElement.class).getAsJsonArray();

        StringBuilder sb = new StringBuilder();
        List<String> output = new ArrayList<String>();
        
        JsonElement jsonEle =  jsonArr.get(0);
        JsonObject jsonObj = jsonEle.getAsJsonObject();
        sb.setLength(0);
        for(Entry<String, JsonElement> entry : jsonObj.entrySet()){
            sb.append(entry.getKey().toString() + ",");
        }
        output.add(sb.toString());
        
        for(JsonElement je : jsonArr){
            JsonObject jo = je.getAsJsonObject();
            sb.setLength(0);
            for(Entry<String, JsonElement> entry : jo.entrySet()){
                sb.append(entry.getValue().toString() + ",");
            }
            output.add(sb.toString());
        }
        
        return output;
    }
    
    public static void exportToNewDebugFile(Collection<?> collection){
        String filename = "/tmp/midl-debug/" + System.currentTimeMillis() + ".csv";
        File file = new File(filename);
        try {
            FileUtils.writeLines(file, collection);
        }
        catch (IOException e) {
        }
    }
}
