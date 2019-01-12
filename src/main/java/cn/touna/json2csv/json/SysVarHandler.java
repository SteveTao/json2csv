package cn.touna.json2csv.json;

public interface SysVarHandler {
    /**
     * 根据系统变量名解析出值
     * @param variableName
     * @return 返回字符串或者JSON对象
     */
    Object value(String variableName);
}
