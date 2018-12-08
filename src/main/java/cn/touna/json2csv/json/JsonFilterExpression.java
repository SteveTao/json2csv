package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Map;

public class JsonFilterExpression {

    private JSONObject filterObj = null;
    private JSONObject matchObj = null;

    public JsonFilterExpression(String json) {
        this(JSONObject.parseObject(json));
    }

    public JsonFilterExpression(JSONObject filterObj) {
        this.filterObj = filterObj;
        if(filterObj != null) {
            this.matchObj = filterObj.getJSONObject("match");
        }
    }

    /**
     * 按照过滤条件，筛选可过滤的json数据
     *
     * @param data json数据
     * @return
     */
    public boolean filter(JSONObject data) {
        if (matchObj == null)
            return false;
        Iterator<Map.Entry<String, Object>> iterator = matchObj.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String path = entry.getKey();
            String jValue = JSONUtil.readFieldValue(data, path, null);
            String value = (String) entry.getValue();
            if (StringUtils.isEmpty(jValue) || StringUtils.isEmpty(value)) {
                return false;
            }
            if (!value.equalsIgnoreCase(jValue)) {
                if (jValue.matches(value))
                    continue;
                return false;
            }
        }
        return true;
    }

    public JSONObject getFilterObj() {
        return filterObj;
    }

    @Override
    public String toString() {
        return filterObj.toString();
    }
}
