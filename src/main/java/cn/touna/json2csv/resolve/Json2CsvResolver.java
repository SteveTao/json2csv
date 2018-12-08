package cn.touna.json2csv.resolve;

import cn.touna.json2csv.json.JSONUtil;
import cn.touna.json2csv.model.Json2CsvMapping;
import cn.touna.json2csv.model.Json2CsvMappingField;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Json2CsvResolver {

    private ResolverConfig resolverConfig;

    public Json2CsvResolver(ResolverConfig resolverConfig) {
        this.resolverConfig = resolverConfig;
    }

    public ResolverConfig getResolverConfig() {
        return resolverConfig;
    }

    /**
     * 通过jsonpath解析field
     * @param data:原始json数据
     * @param root:本次搜索的目标根
     * @param json2CsvMapping
     * @return key: 属性名 value:该属性对应的值
     */
    public String resolve(JSONObject data, JSONObject root, Json2CsvMapping json2CsvMapping) throws ResolveException {
        StringBuilder sb = new StringBuilder();
        Map<String, String> encryptColumns = json2CsvMapping.getEncryptColumns();
        List<Json2CsvMappingField> fieldList = json2CsvMapping.getFields();
        int size = fieldList.size();
        for (int i = 0; i < size; i++) {
            String separator = (i == size - 1) ? ResolverConfig.ROW_SEPARATOR : resolverConfig.getCellSeparator();
            Json2CsvMappingField field = fieldList.get(i);
            //解析
            String fieldData = resolve0(data, root, field, json2CsvMapping.getOptionalRoots());
            //加密
            fieldData = encrypt(fieldData, field.getColumn(), encryptColumns);
            sb.append(fieldData + separator);
        }
        return sb.toString();
    }


    private String resolve0(JSONObject data, JSONObject root, Json2CsvMappingField json2CsvMappingField, Set<String> optionalRootsSet) throws ResolveException {
        String fieldData;
        String field = json2CsvMappingField.getField();
        try {
            //1. 尝试直接从根节点获取
            fieldData = JSONUtil.readFieldValue(root, field, null);
            //2. 直接从根里面读取，可能读取到到"",null。
            if (fieldData == null) {
                fieldData = JSONUtil.readFieldValue2(data, field, optionalRootsSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResolveException(field);
        }

        //空值处理
        if (resolverConfig.getNullResolver().isNull(fieldData)) {
            return resolverConfig.getNullString();
        }

        //类型处理
        if(resolverConfig.getTypeConverter() != null){
            fieldData =  resolverConfig.getTypeConverter().convert(json2CsvMappingField.getType(),fieldData);
        }
        return fieldData;
    }


    /**
     * 对解析出来的数据加密
     * @param value : 解除出来的值
     * @param column : 目标列
     * @param encryptColumns : 需要加密的列集合
     * @return
     */
    private String encrypt(String value, String column, Map<String, String> encryptColumns) {
        if (!StringUtils.isEmpty(column) && encryptColumns != null && encryptColumns.containsKey(column)) {
            String encryptMethod = encryptColumns.get(column);
            ResolverConfig.EncryptHandlerModel model = resolverConfig.getEncryptHandlerModel(encryptMethod);
            if (model != null) {
                return model.getHandler().handle(value, model.getAttach());
            }
        }
        return value;
    }
}



