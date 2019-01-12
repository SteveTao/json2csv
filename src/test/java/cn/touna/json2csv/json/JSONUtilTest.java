package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

public class JSONUtilTest {

    @Test
    public void flat(){
        String value = "{\"collection_contact\":[{\"end_date\":\"2018-07-0219: 36: 25\",\"contact_name\":\"曹德权\",\"contact_details\":[{\"phone_num_loc\":\"四川\",\"call_cnt\":233,\"trans_start\":\"2018-02-0107: 22: 59\"},{\"phone_num_loc\":\"广东\",\"call_cnt\":20,\"trans_start\":\"2018-02-0107: 22: 59\"}]}]}";
        JSONArray obj = JSONObject.parseObject(value).getJSONArray("collection_contact");

        JSONArray jsonArray = new FlatFunc().flat(obj, "contact_details");
        System.out.println(jsonArray);

        Object out = JSONUtil.eval(obj, "$.flat('contact_details')");
        System.out.println(out);
    }

    @Test
    public void flatDeep(){
        String str = null;
        str = "[{\"hitRules\":[{\"detail\":[{\"firstType\":\"信贷行业11\"},{\"firstType\":\"信贷行业12\"}],\"ruleId\":\"126\"},{\"detail\":[{\"firstType\":\"信贷行业21\"},{\"firstType\":\"信贷行业22\"}],\"ruleId\":\"127\"}],\"strategyId\":\"88d8b4785e99467393e559464c8b8540\"}]";
        JSONArray root = JSONArray.parseArray(str);
        JSONPathFunc func = new FlatFunc();
        Object v = null;

        v = func.func(root, Arrays.asList("hitRules","detail"));
        System.out.println(v);
    }

    @Test
    public void flatDeep2(){
        String str = null;
        str = "{\"hitRules\":[{\"detail\":[{\"firstType\":\"信贷行业11\"},{\"firstType\":\"信贷行业12\"}],\"ruleId\":\"126\"},{\"detail\":[{\"firstType\":\"信贷行业21\"},{\"firstType\":\"信贷行业22\"}],\"ruleId\":\"127\"}],\"strategyId\":\"88d8b4785e99467393e559464c8b8540\"}";
        JSONObject root = JSONObject.parseObject(str);
        JSONPathFunc func = new FlatFunc();
        Object v = null;

        v = func.func(root, Arrays.asList("hitRules","detail"));
        System.out.println(v);
    }

    @Test
    public void flatDeep3(){
        String str = null;
        str = "[{\"hitRules\":[{\"detail\":[{\"firstType\":\"信贷行业11\"},{\"firstType\":\"信贷行业12\"}],\"ruleId\":\"126\"},{\"detail\":{\"firstType\":\"信贷行业21\"},\"ruleId\":\"127\"}],\"strategyId\":\"88d8b4785e99467393e559464c8b8540\"}]";
        JSONArray root = JSONArray.parseArray(str);
        JSONPathFunc func = new FlatFunc();
        Object v = null;

        v = func.func(root, Arrays.asList("hitRules","detail"));
        System.out.println(v);
    }

    @Test
    public void split(){
        String str = null;
        str = "{\"blackDetails\":\"A01:B02\"}";
        JSONObject root = JSONObject.parseObject(str);

        SplitFunc splitFunc = new SplitFunc();
        Object v = splitFunc.func(root.getString("blackDetails"),Arrays.asList(":","key"));
        System.out.println(v);

    }

    @Test
    public void dateFormatTest(){
        JSONObject data = new JSONObject();
        data.put("now",new Date());
        data.put("time",System.currentTimeMillis());

        System.out.println(JSONUtil.eval(data,"$.now.dateFormat('yyyy-MM-dd HH:mm:ss')"));
        System.out.println(JSONUtil.eval(data,"$.time.dateFormat('yyyy-MM-dd HH:mm:ss')"));
    }
}
