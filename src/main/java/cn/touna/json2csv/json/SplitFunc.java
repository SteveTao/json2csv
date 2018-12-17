package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 分隔字符串，并转化成JSONArray<br/>
 * 调用方法 .split('seperator','key')
 */
public class SplitFunc extends JSONPathFunc {

    public SplitFunc() {
        super("split", PatternUtil.getFuncPattern("split", 2));
    }

    @Override
    public Object func(Object value, List<String> params) {
        if (value == null)
            return null;
        if (value instanceof CharSequence) {
            String sValue = String.valueOf(value);
            String[] arr = sValue.split(params.get(0));
            JSONArray ja = new JSONArray();
            for (String str : arr) {
                JSONObject jo = new JSONObject();
                jo.put(params.get(1), str);
                ja.add(jo);
            }
            return ja;
        }
        if (value instanceof JSONArray)
            return value;
        return null;
    }
}
