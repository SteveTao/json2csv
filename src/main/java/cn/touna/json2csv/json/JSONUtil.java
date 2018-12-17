package cn.touna.json2csv.json;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONUtil {

    private static List<JSONPathFunc> funcList = new ArrayList<>(Arrays.asList(
            new EntrySetFunc(),
            new FlatFunc(),
            new SplitFunc(),
            new DefaultValueFunc(),
            new SysVarFunc(),
            new DateConvertFunc(),
            new ToJsonFunc(),
            new DateFormatFunc(),
            new TimestampFunc()
    ));

    public static void addJSONPathFunc(JSONPathFunc func) {
        funcList.add(func);
    }

    /**
     * 直接从root或者field读取
     *
     * @param obj
     * @param field
     * @param root
     * @return
     */
    public static String readFieldValue(JSONObject obj, String field, String root) {
        if (obj == null) {
            return null;
        }
        String path = "$";
        if (!StringUtils.isEmpty(root))
            path = concatPath(path, root);
        path = concatPath(path, field);
        if (contains(obj, path)) {
            Object eval = eval(obj, path);
            //todo 这里判断空的原因是在主根里面没有查找到，还可以从可选根里面查找（针对自定义的JsonPath函数做的改进）
            return eval != null ? eval.toString() : null;
        }
        return null;
    }

    public static String readFieldValue2(JSONObject obj, String field, Set<String> optionalRoots) {
        if (CollectionUtils.isEmpty(optionalRoots)) {
            return null;
        }
        for (String optionalRoot : optionalRoots) {
            String value = readFieldValue(obj, field, optionalRoot);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static String concatPath(String parent, String path) {
        if (path.startsWith("[")) {
            return parent + path;
        }
        return parent + "." + path;
    }

    /**
     * 封装了JSONPath.eval，添加了.entrySet()语法
     *
     * @param rootObject
     * @param path
     * @return
     */
    public static Object eval(Object rootObject, String path) {
        Object obj = rootObject;
        Matcher matcher = Pattern.compile(PatternUtil.FUNC).matcher(path);
        int segStart = 0, segStop = -1;
        String seg = "";
        JSONPathFunc curFunc = null;
        while (matcher.find()) {
            curFunc = null;
            String group = matcher.group();
            for (JSONPathFunc func : funcList) {
                if (group.matches(func.getPattern())) {
                    curFunc = func;
                    break;
                }
            }

            if (curFunc == null) {
                throw new IllegalArgumentException("不支持的函数" + group);
            }
            segStop = matcher.start();
            seg = path.substring(segStart, segStop);
            if (seg != null && seg.length() > 0)
                obj = JSONPath.eval(obj, seg);
            obj = curFunc.func(obj, PatternUtil.extractParams(group));
            segStart = matcher.end();
        }

        if (segStart < path.length() - 1) {
            seg = path.substring(segStart);
            obj = JSONPath.eval(obj, seg);
        }
        return obj;
    }

    /**
     * 封装了JSONPath.contains，添加了.entrySet()语法
     *
     * @param rootObject
     * @param path
     * @return
     */
    public static boolean contains(Object rootObject, String path) {
        Object obj = rootObject;
//        String funcRegex = "\\.[a-zA-Z][a-zA-Z0-9]*\\([a-z-A-Z0-9_]*\\)";
        Matcher matcher = Pattern.compile(PatternUtil.FUNC).matcher(path);
        int segStart = 0, segStop = -1;
        String seg = "";
        JSONPathFunc curFunc = null;
        while (matcher.find()) {
            curFunc = null;
            String group = matcher.group();
            for (JSONPathFunc func : funcList) {
                if (group.matches(func.getPattern())) {
                    curFunc = func;
                    break;
                }
            }

            if (curFunc == null) {
                throw new IllegalArgumentException("不支持的函数" + group);
            }
            segStop = matcher.start();
            seg = path.substring(segStart, segStop);
            if (!JSONPath.contains(obj, seg))
                return false;
            obj = JSONPath.eval(obj, seg);
            obj = curFunc.func(obj, PatternUtil.extractParams(group));
            segStart = matcher.end();
        }

        if (segStart < path.length() - 1) {
            seg = path.substring(segStart);
            if (!JSONPath.contains(obj, seg))
                return false;
        }
        return true;
    }

    /**
     * 获取指定根的json对象
     *
     * @param data
     * @param rootPath
     * @return
     */
    public static Object getRoot(JSONObject data, String rootPath) {
        String path = "$";
        if (!StringUtils.isEmpty(rootPath)) {
            path = concatPath(path, rootPath);
        }
        return eval(data, path);
    }
}
