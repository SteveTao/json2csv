package cn.touna.json2csv.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析结果
 */
public class ResolveResult {
    //正确的解析结果
    private final Map<String, List<String>> resolvedData = new HashMap<>();
    //解析错误
    private final Map<String, ErrorMessage> errorData = new HashMap<>();


    public void putLines(String tableName, List<String> lines) {
        this.resolvedData.put(tableName, lines);
    }

    public void putErrorMsg(String tableName, ErrorMessage errorMessage) {
        this.errorData.put(tableName, errorMessage);
    }


    public Map<String, List<String>> getResolvedData() {
        return resolvedData;
    }

    public Map<String, ErrorMessage> getErrorData() {
        return errorData;
    }
}
