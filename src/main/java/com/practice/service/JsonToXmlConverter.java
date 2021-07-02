package com.practice.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonToXmlConverter {
    public String convert(String jsonString, String rootTagElement) {
        return XML.toString(XML.toJSONObject(jsonString), rootTagElement);
    }

    public String convert(List<String> jsonData, String rootTagName, String xmlArrayTag) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (String jsonDatum : jsonData) {
            JSONObject jsonObject = new JSONObject(jsonDatum);
            jsonObjects.add(jsonObject);
        }

        final JSONArray array = new JSONArray(jsonObjects);
        HashMap<String, JSONArray> arrayHashMap = new HashMap<>();
        arrayHashMap.put(xmlArrayTag, array);

        JSONObject object = new JSONObject(arrayHashMap);
        return XML.toString(object, rootTagName);
    }
}
