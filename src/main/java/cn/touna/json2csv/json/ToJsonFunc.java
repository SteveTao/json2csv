package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class ToJsonFunc extends JSONPathFunc {

    public ToJsonFunc() {
        super("toJson()", PatternUtil.getFuncPattern("toJson", 0));
    }

    @Override
    public Object func(Object obj, List<String> params) {
        if (obj == null)
            return null;
        if (obj instanceof JSON) {
            return obj;
        }
        if (obj instanceof CharSequence) {
            String str = String.valueOf(obj);

            if (str.matches(PatternUtil.JSON_OBJECT)) {
                return JSONObject.parseObject(str);
            } else if (str.matches(PatternUtil.JSON_ARRAY)) {
                return JSONArray.parseArray(str);
            }
        }
        return null;
    }
}
