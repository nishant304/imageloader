package com.example.nishant.imageloader.network.parser;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 11/30/2016.
 */

public class JsonParser implements Parser {

    @Override
    public  <T> T parse(String json, Class<T> tClass) {
        return new Gson().fromJson(json, tClass);
    }

    @Override
    public <T> List<T> toList(String json, Class<T> tClass) {
        Gson gson = new Gson();
        ArrayList<T> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i=0;i<jsonArray.length();i++){
             list.add(gson.fromJson(jsonArray.get(i).toString(),tClass));
            }
        } catch (JSONException e) {

        }

        return list;
    }

}
