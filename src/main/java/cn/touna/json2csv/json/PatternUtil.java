package cn.touna.json2csv.json;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {

    /**
     * 函数开头，例如： ".entry("
     */
    public static final String FUNC_PREFIX = "\\.[\\p{Alpha}_]\\w*\\(";

    /**
     * 函数结尾，例如： ")"
     */
    public static final String FUNC_SUFFIX = "\\)";
    /**
     * 函数最后一个参数，不带逗号，例如： "'lastName'"
     */
    public static final String FUNC_PARAM_LAST = "'[\\w\\p{Punct}\\s]+'";

    /**
     * 函数的参数,例如:  "'lastName',"
     */
    public static final String FUNC_PARAM = FUNC_PARAM_LAST + ",";

    /**
     * 函数的匹配
     */
    public static final String FUNC = FUNC_PREFIX + "(" + FUNC_PARAM + ")" + "*(" + FUNC_PARAM_LAST + ")?" + FUNC_SUFFIX;

    public static final String NUMBER = "\\d*";

    public static final String JSON_OBJECT = "^\\{.*\\}$";

    public static final String JSON_ARRAY = "^\\[.*\\]$";

    public static final String ARRAY_RANGE = "^\\[.*\\)$|^\\(.*\\]$";


    /**
     * 用于获取函数调用的参数
     */
    private static final String FUNC_PARAM_SPLIT = FUNC_PREFIX + "|" + FUNC_SUFFIX;

    /**
     * 指定方法名和参数个数来获取Pattern
     *
     * @param funcName  方法名
     * @param numParams 参数个数
     * @return
     */
    public static String getFuncPattern(String funcName, int numParams) {
        StringBuilder sb = new StringBuilder("\\." + funcName + "\\(");
        if (numParams == 1) {
            sb.append(FUNC_PARAM_LAST);
        } else if (numParams > 1) {
            sb.append("(" + FUNC_PARAM + "){" + --numParams + "}");
            sb.append(FUNC_PARAM_LAST);
        }
        sb.append(FUNC_SUFFIX);
        return sb.toString();
    }

    public static String getFuncWithMinParamsPattern(String funcName, int minNumParams) {
        StringBuilder sb = new StringBuilder("\\." + funcName + "\\(");
        sb.append("(" + FUNC_PARAM + "){" + --minNumParams + ",}");
        sb.append(FUNC_PARAM_LAST);
        sb.append(FUNC_SUFFIX);
        return sb.toString();
    }

    /**
     * 从callStr解析出参数
     *
     * @param callStr
     * @return
     */
    public static List<String> extractParams(String callStr) {
        String[] arr1 = callStr.split(FUNC_PARAM_SPLIT);
        if (arr1.length < 2)
            return null;
        Pattern pattern = Pattern.compile(FUNC_PARAM_LAST);
        Matcher matcher = pattern.matcher(arr1[1]);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group();
            group = group.substring(1, group.length() - 1);
            list.add(group);
        }
        return list;
    }
}
