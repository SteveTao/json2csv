package cn.touna.json2csv.markdown.utils;

import cn.touna.json2csv.markdown.model.*;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 */
public class MarkdownResolver {

    private static Map<String, Integer> levelMap = new HashMap<>();

    static {
        levelMap.put("MdDocument", 1);
        levelMap.put("MdHeader", 2);
        levelMap.put("MdListItem", 3);
        levelMap.put("MdText", 4);
        levelMap.put("MdSegment", 4);
    }

    private static final String HEADER_ONE = "# ";
    private static final String HEADER_TWO = "## ";
    private static final String LIST_ITEM = "- ";
    private static final String SEGMENT = "```";

    public MdDocument load(String filename) throws IOException {
        return load(new FileInputStream(filename));
    }

    public MdDocument load(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        MdDocument doc = new MdDocument();
        MdElement current = null;
        LinkedList<MdElement> stack = new LinkedList<>();
        stack.push(doc);

        while ((line = reader.readLine()) != null) {
            current = resolveLine(line, doc, current, stack);
        }
        reader.close();
        resolveEmptyChilds(doc);
        return doc;
    }

    private MdElement resolveLine(String line, MdDocument doc, MdElement current, LinkedList<MdElement> stack) {
        if (current == null) {
            if (null == line || "".equals(line))
                return null;

            if (line.startsWith(HEADER_ONE)) {
                MdHeader header = doc.buildHeader(1, line.substring(HEADER_ONE.length()).trim());
                addChild(stack, header);
                current = null;
                return current;
            }
            if (line.startsWith(HEADER_TWO)) {
                MdHeader header = doc.buildHeader(2, line.substring(HEADER_TWO.length()).trim());
                addChild(stack, header);
                return null;
            }
            if (line.startsWith(LIST_ITEM)) {
                MdListItem listItem = doc.buildListItem(1, line.substring(LIST_ITEM.length()).trim());
                addChild(stack, listItem);
                current = null;
                return current;
            }
            if (line.startsWith(SEGMENT)) {
                MdSegment segment = doc.buildSegment(line.substring(SEGMENT.length()));
                addChild(stack, segment);
                current = segment;
                return current;
            }
            MdText text = doc.buildText();
            text.appendLine(line);
            addChild(stack, text);
            current = text;
            return current;
        } else {
            if (current instanceof MdSegment) {
                if (line.startsWith(SEGMENT)) {
                    current = null;
                    return current;
                } else {
                    ((MdSegment) current).appendLine(line);
                    return current;
                }
            }
            if (current instanceof MdText) {
                if (line.startsWith(HEADER_ONE) || line.startsWith(HEADER_TWO) || line.startsWith(LIST_ITEM)) {
                    resolveLine(line, doc, null, stack);
                } else {
                    if (null == line || "".equals(line))
                        return current;
                    ((MdText) current).appendLine(line);
                    return current;
                }
            }
            return null;
        }
    }

    private void addChild(LinkedList<MdElement> statck, MdElement current) {
        getParent(statck, current).addChild(current);
        statck.add(current);
    }

    private MdElement getParent(LinkedList<MdElement> statck, MdElement current) {
        if (current == null) {
            System.out.println("null");
        }
        String simpleName = current.getClass().getSimpleName();
        int level = levelMap.get(simpleName);
        while (levelMap.get(statck.getLast().getClass().getSimpleName()) > level) {
            statck.removeLast();
        }

        if (levelMap.get(statck.getLast().getClass().getSimpleName()) < level) {
            return statck.getLast();
        } else {
            if (MdLeveledLabel.class.isAssignableFrom(current.getClass())) {
                int level1 = ((MdLeveledLabel) current).getLevel();
                int level2 = ((MdLeveledLabel) statck.getLast()).getLevel();
                if (level2 < level1) {
                    return statck.getLast();
                } else {
                    statck.removeLast();
                    return getParent(statck, current);
                }
            } else {
                statck.removeLast();
                return statck.getLast();
            }
        }
    }

    private static void resolveEmptyChilds(MdElement element) {
        for (MdElement child : element.getChilds()) {
            if (child.getChilds().size() == 0) {
                child.setChilds(null);
            } else {
                resolveEmptyChilds(child);
            }
        }
    }

}
