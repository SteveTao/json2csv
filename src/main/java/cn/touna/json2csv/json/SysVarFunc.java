package cn.touna.json2csv.json;

import java.util.LinkedList;
import java.util.List;

public class SysVarFunc extends JSONPathFunc {

    public SysVarFunc() {
        super("sysVar('VariableName')", PatternUtil.getFuncPattern("sysVar", 1));
    }

    public static DefaultSysVarHandler defaultSysVarHandler = new DefaultSysVarHandler();
    private static List<SysVarHandler> sysVarHandlerList = new LinkedList<>();

    @Override
    public Object func(Object value, List<String> params) {
        String varName = params.get(0);
        Object var = defaultSysVarHandler.value(varName);
        if (var == null){
            for (SysVarHandler handler : sysVarHandlerList) {
                var = handler.value(varName);
                if(var != null)
                    break;
            }
        }
        return var;
    }

    public static void addSysVarHandler(SysVarHandler handler){
        sysVarHandlerList.add(handler);
    }
}
