package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FlatFunc extends JSONPathFunc {

    public FlatFunc() {
        super("flat", PatternUtil.getFuncWithMinParamsPattern("flat", 1));
    }

    @Override
    public Object func(Object value, List<String> params) {
        if (value == null)
            return null;
        if (value instanceof JSON) {
            JSON json = (JSON) value;
            return flat(json, params, 0);
        }
        return value;
    }

    public JSONArray flat(JSON json, List<String> fields, int index) {
        boolean last = index == (fields.size() - 1);
        String flatField = fields.get(index);
        int nextIndex = index + 1;
        JSONArray ja = new JSONArray();
        if (json instanceof JSONObject) {
            JSONObject jo = (JSONObject) json;
            Object obj = jo.get(flatField);
            if (obj instanceof JSONArray) {
                JSONArray ja2 = (JSONArray) obj;

                for (int i = 0; i < ja2.size(); i++) {
                    Object flatValue = ja2.get(i);
                    if (last) {
                        JSONObject row = copyExcept(jo, flatField);
                        row.put(flatField, flatValue);
                        ja.add(row);
                    } else {
                        if (flatValue instanceof JSON) {
                            JSONArray ja3 = flat((JSON) flatValue, fields, nextIndex);
                            for (int j = 0; j < ja3.size(); j++) {
                                JSONObject row = copyExcept(jo, flatField);
                                row.put(flatField, ja3.get(j));
                                ja.add(row);
                            }
                        } else {
                            JSONObject row = copyExcept(jo, flatField);
                            row.put(flatField, flatValue);
                            ja.add(row);
                        }
                    }
                }
            } else {
                ja.add(json);
            }
        } else if (json instanceof JSONArray) {
            JSONArray ja2 = (JSONArray) json;
            for (int i = 0; i < ja2.size(); i++) {
                JSONObject jo = ja2.getJSONObject(i);
                if (jo != null) {
                    JSONArray ja3 = flat(jo, fields, index);
                    ja.addAll(ja3);
                }
            }
        }
        return ja;
    }

    public JSONArray flat(JSON json, String flatField) {
        JSONArray ja = new JSONArray();
        if (json instanceof JSONObject) {
            JSONObject jo = (JSONObject) json;
            Object obj = jo.get(flatField);
            if (obj instanceof JSONArray) {
                JSONArray ja2 = (JSONArray) obj;
                JSONObject row = null;
                for (int i = 0; i < ja2.size(); i++) {
                    row = copyExcept(jo, flatField);
                    row.put(flatField, ja2.get(i));
                    ja.add(row);
                }
            } else {
                ja.add(json);
            }
        } else if (json instanceof JSONArray) {
            JSONArray ja2 = (JSONArray) json;
            for (int i = 0; i < ja2.size(); i++) {
                JSONObject jo = ja2.getJSONObject(i);
                if (jo != null) {
                    JSONArray ja3 = flat(jo, flatField);
                    ja.addAll(ja3);
                }
            }
        }
        return ja;
    }


    private JSONObject copyExcept(JSONObject jo, String field) {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, Object> entry : jo.entrySet()) {
            if (!field.equals(entry.getKey())) {
                obj.put(entry.getKey(), entry.getValue());
            }
        }
        return obj;
    }

    public static void main(String[] args) {
        String str = "[{\"hitRules\":[{\"detail\":[{\"firstType\":\"信贷行业\"}],\"ruleId\":\"126\"}],\"strategyId\":\"88d8b4785e99467393e559464c8b8540\"}]";
        str = "{\"hitRules\":[{\"detail\":[{\"firstType\":\"信贷行业11\"},{\"firstType\":\"信贷行业12\"}],\"ruleId\":\"126\"},{\"detail\":[{\"firstType\":\"信贷行业21\"},{\"firstType\":\"信贷行业22\"}],\"ruleId\":\"127\"}],\"strategyId\":\"88d8b4785e99467393e559464c8b8540\"}";
//        JSONArray root = JSONArray.parseArray(str);
        JSONObject root = JSONObject.parseObject(str);
        JSONPathFunc func = new FlatFunc();
        Object v = null;

        v = func.func(root,Arrays.asList("hitRules","detail"));
//        v = func.func(root, Arrays.asList("hitRules"));
        System.out.println(v);
    }
}
