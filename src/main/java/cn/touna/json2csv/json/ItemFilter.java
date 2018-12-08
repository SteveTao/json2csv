package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Map;

public class ItemFilter {
    /**
     * itemFilter根对应的json
     */
    private JSONObject itemFilterObject = null;
    /**
     * itemFilter节点下match节点
     */
    private JSONObject itemMatchObject = null;


    public ItemFilter(String json) {
        this(JSON.parseObject(json));
    }

    public ItemFilter(JSONObject itemFilterObject) {
        this.itemFilterObject = itemFilterObject;
        if(itemFilterObject != null) {
            this.itemMatchObject = itemFilterObject.getJSONObject("match");
        }
    }

    public boolean filter(JSONObject data) {
        if (itemMatchObject == null) {
            //注意这里如果没有配置那么默认过滤通过
            return true;
        }
        Iterator<Map.Entry<String, Object>> iterator = itemMatchObject.entrySet().iterator();
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
}
