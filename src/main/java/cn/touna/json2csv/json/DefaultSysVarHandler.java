package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultSysVarHandler implements SysVarHandler {

    /**
     * 当前时间，格式为yyyy-MM-dd HH:mm:ss
     */
    public static final String NOW = "NOW";
    /**
     * 当前年
     */
    public static final String YEAR = "YEAR";
    /**
     * 当前月
     */
    public static final String MONTH = "MONTH";
    /**
     * 当前日期
     */
    public static final String DATE = "DATE";
    /**
     * 时间戳
     */
    public static final String TIMESTAMP = "TIMESTAMP";
    /**
     * 原始的json行
     */
    public static final String SOURCE = "SOURCE";
    /**
     * 映射对象
     */
    public static final String MAPPING = "MAPPING";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private JSONObject row = null;

    private Object mapping;

    public void setRow(JSONObject row) {
        this.row = row;
    }

    public void setMapping(Object mapping) {
        this.mapping = mapping;
    }

    @Override
    public Object value(String variableName) {
        if(NOW.equals(variableName)){
            return sdf.format(new Date());
        }else if(YEAR.equals(variableName)){
            return String.valueOf(new Date().getYear());
        }else if(MONTH.equals(variableName)){
            return String.valueOf(new Date().getMonth() + 1);
        }else if(DATE.equals(variableName)){
            return String.valueOf(new Date().getDate());
        }else if(TIMESTAMP.equals(variableName)){
            return String.valueOf(System.currentTimeMillis());
        }else if(SOURCE.equals(variableName)){
            return row;
        }else if(MAPPING.equals(variableName)){
            return JSONObject.toJSON(mapping);
        }
        return null;
    }
}
