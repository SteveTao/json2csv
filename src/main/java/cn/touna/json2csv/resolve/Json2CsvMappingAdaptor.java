package cn.touna.json2csv.resolve;

import cn.touna.json2csv.json.ItemFilter;
import cn.touna.json2csv.json.JSONUtil;
import cn.touna.json2csv.json.SysVarFunc;
import cn.touna.json2csv.model.ErrorMessage;
import cn.touna.json2csv.model.Json2CsvMapping;
import cn.touna.json2csv.model.ResolveResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Json2CsvMappingAdaptor implements JsonDataResolver {

    private final Json2CsvResolver json2CsvResolver;
    private final List<Json2CsvMapping> json2CsvMappingList;

    public Json2CsvMappingAdaptor(Json2CsvResolver json2CsvResolver, List<Json2CsvMapping> json2CsvMappingList) {
        this.json2CsvResolver = json2CsvResolver;
        this.json2CsvMappingList = json2CsvMappingList;
    }

    @Override
    public ResolveResult resolve(JSONObject data) {
        SysVarFunc.defaultSysVarHandler.setRow(data);

        ResolveResult result = new ResolveResult();
        if (!CollectionUtils.isEmpty(json2CsvMappingList)) {
            for (Json2CsvMapping json2CsvMapping : json2CsvMappingList) {
                String tableName = json2CsvMapping.getTable();
                if (StringUtils.isEmpty(tableName)) {
                    continue;
                }
                List<String> lines = new ArrayList<>();
                if (json2CsvMapping.getFilter().filter(data)) {
                    try {
                        Object root = JSONUtil.getRoot(data, json2CsvMapping.getRoot());
                        if (root == null) {
                            continue;
                        }
                        //这里ItemFilter一定不为null
                        ItemFilter itemFilter = json2CsvMapping.getItemFilter();
                        if (root instanceof JSONArray) {
                            JSONArray ja = (JSONArray) root;
                            for (int i = 0; i < ja.size(); i++) {
                                if (itemFilter.filter(ja.getJSONObject(i))) {
                                    SysVarFunc.defaultSysVarHandler.setMapping(json2CsvMapping);
                                    String line = json2CsvResolver.resolve(data, ja.getJSONObject(i), json2CsvMapping);
                                    lines.add(line);
                                }
                            }
                        } else if (root instanceof JSONObject) {
                            JSONObject jo = (JSONObject) root;
                            if (itemFilter.filter(jo)) {
                                SysVarFunc.defaultSysVarHandler.setMapping(json2CsvMapping);
                                String line = json2CsvResolver.resolve(data, jo, json2CsvMapping);
                                lines.add(line);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ErrorMessage errorMessage = new ErrorMessage();
                        errorMessage.setData(data.toString());
                        errorMessage.setErrorMsg(e.getMessage());
                        errorMessage.setTable(tableName);
                        result.putErrorMsg(tableName, errorMessage);
                    }
                    result.putLines(tableName, lines);
                }
            }
        }
        return result;
    }

}
