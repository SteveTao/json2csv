package cn.touna.json2csv.json;

import java.util.List;

public class DefaultValueFunc extends JSONPathFunc {

    public DefaultValueFunc() {
        super("defaultValue('value')", PatternUtil.getFuncPattern("defaultValue", 1));
    }

    @Override
    public Object func(Object json, List<String> params) {
        return params.get(0);
    }
}
