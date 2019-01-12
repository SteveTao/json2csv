package cn.touna.json2csv.markdown.utils;


import cn.touna.json2csv.markdown.model.MdElement;
import cn.touna.json2csv.markdown.model.MdListItem;
import cn.touna.json2csv.markdown.model.MdSegment;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResolverUtil {

    private static final String S_PATTERN_VARIABLE = "\\$\\{[a-zA-Z0-9\\.]+}";
    private static final Pattern PATTERN_VARIABLE = Pattern.compile(S_PATTERN_VARIABLE);

    public static List<String> extractVariables(String value) {
        List<String> list = new LinkedList<>();
        Matcher matcher = PATTERN_VARIABLE.matcher(value);
        while (matcher.find()) {
            String str = matcher.group(0);
            list.add(str.substring(2, str.length() - 1));
        }
        return list;
    }


    public static String getSegmentString(MdListItem listItem) {
        Optional<MdElement> element = listItem.getChilds().stream().filter(child -> child instanceof MdSegment).findFirst();
        if (element.isPresent()) {
            return ((MdSegment) element.get()).getContent().trim();
        } else {
            return "";
        }
    }

    public static void convertToProperties(String key, Map<String, ?> map, Properties p) {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String keyName = key.length() == 0 ? entry.getKey() : key + "." + entry.getKey();
            if (Map.class.isAssignableFrom(entry.getValue().getClass())) {
                convertToProperties(keyName, (Map) entry.getValue(), p);
            } else {
                p.put(keyName, entry.getValue());
            }
        }

    }
}
