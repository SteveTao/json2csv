package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 将JSONObject或者JSONArray中的JSONObject的键值对取出，组装成JSONArray<br/>
 * 调用方法 .entrySet()
 */
public class EntrySetFunc extends JSONPathFunc {

    public EntrySetFunc() {
        super("entrySet()", PatternUtil.getFuncPattern("entrySet", 0));
    }

    @Override
    public Object func(Object value, List<String> params) {
        if (value == null)
            return null;
        if (value instanceof JSON) {
            JSON json = (JSON) value;
            return entrySet(json);
        }
        return null;
    }

    public JSONArray entrySet(JSON json) {
        if (json == null)
            return null;
        JSONArray array = new JSONArray();
        if (json instanceof JSONObject) {
            JSONObject jo = (JSONObject) json;
            for (Map.Entry<String, Object> entry : jo.entrySet()) {
                JSONObject obj = new JSONObject();
                obj.put("key", entry.getKey());
                obj.put("value", entry.getValue());
                array.add(obj);
            }
        } else if (json instanceof JSONArray) {
            JSONArray ja = (JSONArray) json;
            for (int i = 0; i < ja.size(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null) {
                    array.addAll(entrySet(jo));
                }
            }
        }
        return array;
    }
}
