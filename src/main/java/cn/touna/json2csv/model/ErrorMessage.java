package cn.touna.json2csv.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class ErrorMessage implements Serializable {
    private String data;
    private String table;
    private String errorMsg;
    private String location;
    private String logTime;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}