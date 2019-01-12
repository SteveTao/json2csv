package cn.touna.json2csv.resolve;


import cn.touna.json2csv.model.ResolveResult;
import com.alibaba.fastjson.JSONObject;

/**
 * 三方数据解析
 */
public interface JsonDataResolver {

    /**
     * 解析数据
     *
     * @param data:被解析的数据
     */
    ResolveResult resolve(JSONObject data);
}
