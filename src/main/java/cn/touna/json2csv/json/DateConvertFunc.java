package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateConvertFunc extends JSONPathFunc {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DateConvertFunc() {
        super("dateConvert(params)", PatternUtil.getFuncPattern("dateConvert", 1));
    }

    @Override
    public Object func(Object value, List<String> params) {
        if (value == null)
            return null;

        if (value instanceof JSONObject) {
            String varName = params.get(0);
            JSONObject jsonObject = (JSONObject) value;
            Object obj = jsonObject.get(varName);
            if(obj == null) {
                return null;
            }
            //兼容string,long类型的时间戳转换
            String str = obj.toString();
            if(str.length() > 13) {
                str = str.substring(0,13);
            }
            Long time = Long.parseLong(str);
            String date = SDF.format(new Date(time));
            return date;
        }
        return null;
    }


    public static void main(String[] args) {
        String l = String.valueOf(System.currentTimeMillis());
        String date = SDF.format(new Date(Long.parseLong(l)));
        System.out.println(date);
    }
}
