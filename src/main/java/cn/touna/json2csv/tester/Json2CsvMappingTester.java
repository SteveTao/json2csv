package cn.touna.json2csv.tester;

import cn.touna.json2csv.Json2CsvMappingMarkdownLoader;
import cn.touna.json2csv.model.Json2CsvMapping;
import cn.touna.json2csv.model.Json2CsvMappingField;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * mapping文件检查，检查项<br/>
 * 1. 重复的表名，表描述<br/>
 * 2. 空root<br/>
 * 3. 是否存在必填的加密列<br/>
 * 4. 必填的首列，尾列<br/>
 * 5. 是否符合期望的列类型<br/>
 * 6. 列，列描述是否重复<br/>
 * 7. 列类型是否在指定的范围内<br/>
 */
public class Json2CsvMappingTester {

    private BufferedWriter bw = null;
    private static final String NEW_LINE = "\n";

    private List<String> commonHeadColumns = null;
    private List<String> commonTailColumns = null;
    private List<String> recormendTypes = null;
    private List<String> encryedColumns = null;

    public Json2CsvMappingTester(Writer writer) {
        bw = new BufferedWriter(writer);
    }

    private Json2CsvMappingMarkdownLoader loader = new Json2CsvMappingMarkdownLoader();

    private Map<String, String> columnTypeMatchMap = new HashMap<>();
    private Map<String, String> commentTypeMatchMap = new HashMap<>();

    public void addColumnTypeMatch(String type, String... columns) {
        for (String column : columns) {
            columnTypeMatchMap.put(column, type);
            columnTypeMatchMap.put(StringUtils.capitalize(column), type);
        }
    }

    public void addCommentTypeMatch(String type, String... comments) {
        for (String comment : comments)
            commentTypeMatchMap.put(comment, type);
    }

    public void setCommonHeadColumns(List<String> commonHeadColumns) {
        this.commonHeadColumns = commonHeadColumns;
    }

    public void setCommonTailColumns(List<String> commonTailColumns) {
        this.commonTailColumns = commonTailColumns;
    }

    public void setRecormendTypes(List<String> recormendTypes) {
        this.recormendTypes = recormendTypes;
    }

    public void setEncryedColumns(List<String> encryedColumns) {
        this.encryedColumns = encryedColumns;
    }

    /**
     * @param mdFiless
     */
    public void checkSchema(String mdFiless) throws IOException {
        appendLine("");
        bw.append("-----------------   ").append("CheckSchema").append("---------------------\n");
        List<Json2CsvMapping> mappingList = loader.loadFromFile(mdFiless);
        Map<String, Integer> tableNameCntMap = new HashMap<>();
        Map<String, Integer> tableCommentCntMap = new HashMap<>();
        List<String> emptyRootTables = new LinkedList<>();

        for (Json2CsvMapping mapping : mappingList) {
            increment(tableNameCntMap, mapping.getTable());
            increment(tableCommentCntMap, mapping.getComment());

            if (StringUtils.isEmpty(mapping.getRoot())) {
                emptyRootTables.add(mapping.getTable());
            }

            List<String> columns = mapping.getFields().stream().map(Json2CsvMappingField::getColumn).collect(Collectors.toList());
            if (encryedColumns != null && encryedColumns.size() > 0) {
                if (mapping.getEncryptColumns() == null || mapping.getEncryptColumns().size() == 0) {
//                    appendLine(mapping.getTable() + "dotnot contain encryedColumn", encryedColumns);
                } else {
                    List<String> unmatchedEncryedColumns = new LinkedList<>();
                    for (String encryedColumn : encryedColumns) {
                        if (columns.contains(encryedColumn) && !mapping.getEncryptColumns().containsKey(encryedColumn)) {
                            unmatchedEncryedColumns.add(encryedColumn);
                        }
                    }
                    if (unmatchedEncryedColumns.size() > 0)
                        appendLine(mapping.getTable() + "dotnot contain encryedColumn", unmatchedEncryedColumns);
                }
            }
        }

        //#
        checkRepeatedKeys(tableNameCntMap, "RepeatedTableNames");
        checkRepeatedKeys(tableCommentCntMap, "RepeatedTableComments");
        bw.flush();

        //#
        if (emptyRootTables.size() > 0) {
            appendLine("EmptyRootTables", emptyRootTables);
        }

        //#
        if (commonHeadColumns == null)
            commonHeadColumns = new ArrayList<>();
        int commonColumnsSize = commonHeadColumns == null ? 0 : commonHeadColumns.size();
        commonColumnsSize += commonTailColumns == null ? 0 : commonTailColumns.size();
        for (Json2CsvMapping mapping : mappingList) {
            bw.append("\n# check table ").append(mapping.getTable()).append("----------------------\n");

            List<Json2CsvMappingField> fields = mapping.getFields();

            if (commonColumnsSize > 0) {
                if (fields.size() < commonColumnsSize) {
                    appendLine(mapping.getTable() + "\tField Count is small");
                } else {
                    List<String> unmatchedField = new LinkedList<>();
                    if (commonHeadColumns != null && commonHeadColumns.size() > 0) {
                        for (int i = 0; i < commonHeadColumns.size(); i++) {
                            Json2CsvMappingField field = fields.get(i);
                            if (!commonHeadColumns.get(i).equals(field.getColumn())) {
                                unmatchedField.add("-\t" + field.getColumn() + " expect " + commonHeadColumns.get(i));
                            }
                        }
                    }
                    if (commonTailColumns != null && commonTailColumns.size() > 0) {
                        List<Json2CsvMappingField> tailFields = fields.subList(fields.size() - commonTailColumns.size(), fields.size());
                        for (int i = 0; i < commonTailColumns.size(); i++) {
                            Json2CsvMappingField field = tailFields.get(i);
                            if (!commonTailColumns.get(i).equals(field.getColumn())) {
                                unmatchedField.add("-\t" + field.getColumn() + " expect " + tailFields.get(i).getColumn());
                            }
                        }
                    }
                    if (unmatchedField.size() > 0) {
                        bw.append("-\t").append(" Unmatch Common Fields").append("\n");
                        for (String s : unmatchedField) {
                            bw.append(s).append(NEW_LINE);
                        }
                        bw.append(NEW_LINE);
                    }
                }
            }

            Map<String, Integer> fieldCountMap = new HashMap<>();
            Map<String, Integer> fieldCommentCountMap = new HashMap<>();
            for (Json2CsvMappingField field : mapping.getFields()) {
                increment(fieldCountMap, field.getColumn());
                increment(fieldCommentCountMap, field.getComment());

                for (Map.Entry<String, String> entry : columnTypeMatchMap.entrySet()) {
                    if (field.getColumn().contains(entry.getKey())) {
                        if (!entry.getValue().equals(field.getType())) {
                            bw.append("-\tcolumn: ").append(field.getColumn()).
                                    append(", type: ").append(field.getType())
                                    .append(", expectType: ").append(entry.getValue())
                                    .append(", keyword: ").append(entry.getKey())
                                    .append("\n");
                        }
                    }
                }

                //commentTypeMatchMap
                for (Map.Entry<String, String> entry : commentTypeMatchMap.entrySet()) {
                    if (field.getComment().contains(entry.getKey())) {
                        if (!entry.getValue().equals(field.getType())) {
                            bw.append("-\tcolumn: ").append(field.getColumn()).
                                    append(", type: ").append(field.getType())
                                    .append(", expectType: ").append(entry.getValue())
                                    .append(", keyword: ").append(entry.getKey())
                                    .append("\n");
                        }
                    }
                }

                String type = field.getType();
                if (!type.startsWith("varchar")) {
                    if (recormendTypes != null && !recormendTypes.contains(type)) {
                        bw.append("-\tcolumn ").append(field.getColumn()).append("'s type ").append(type).append(" is not recormend!").append("\n");
                    }
                }
            }
            checkRepeatedKeys(fieldCountMap, "duplicatedFields");
            checkRepeatedKeys(fieldCommentCountMap, "duplicatedFieldComments");
        }
    }

    private void checkRepeatedKeys(Map<String, Integer> map, String title) throws IOException {
        List<String> repeatedTableNames =
                map.entrySet().stream().
                        filter(entry -> entry.getValue() > 1)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
        if (repeatedTableNames.size() > 0) {
            appendLine(title, repeatedTableNames);
        }
    }

    private void increment(Map<String, Integer> map, String key) {
        if (!map.containsKey(key)) {
            map.put(key, 1);
        } else {
            map.put(key, map.get(key) + 1);
        }
    }

    private BufferedWriter appendLine(String title, Object v) throws IOException {
        bw.append("-\t").append(title).append(": ").append(String.valueOf(v)).append(NEW_LINE);
        return bw;
    }

    private BufferedWriter appendLine(String v) throws IOException {
        bw.append(v).append(NEW_LINE);
        return bw;
    }

    public void querySql(String mdFiles) throws IOException {
        List<Json2CsvMapping> mappingList = loader.load(mdFiles, new FileInputStream(mdFiles));
        for (Json2CsvMapping mapping : mappingList) {
            System.out.println("select * from provider_api_db." + mapping.getTable() + " limit 100");
//            System.out.println("truncate table provider_api_db." + mapping.getTable() +";");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Json2CsvMappingTester checker = new Json2CsvMappingTester(new PrintWriter(System.out));
        checker.setCommonHeadColumns(Arrays.asList("cr_no", "id_card", "mobile", "apply_no"));
        checker.setCommonTailColumns(Arrays.asList("query_time", "update_time"));
        checker.setRecormendTypes(Arrays.asList("bigint", "int", "boolean", "double", "timestamp"));
        checker.setEncryedColumns(Arrays.asList("id_card","mobile","cert_no"));

        //detail,body,content,evidence,result,remark,reason
        checker.addColumnTypeMatch("varchar(65355)", "content",
                "body", "reason", "detail", "evidence", "result", "remark", "reason");
        //详情,内容,概要,证据,结果,备注，原因
        checker.addCommentTypeMatch("varchar(65355)", "详情", "内容", "概要", "证据", "结果", "备注", "原因");

//        - 链接设为varchar(500)
//                - link,web,website
//                - 链接
        checker.addColumnTypeMatch("varchar(500)", "link", "web", "website");
        checker.addCommentTypeMatch("varchar(500)", "链接", "网站", "官网");
        //条例使用varchar(1000)
        checker.addCommentTypeMatch("varchar(1000)", "条例");
//        checker.querySql(mdFiles);

        String mdFiles = "file:/home/appuser/workspace/java/tbd/tbd-etl/doc/tmpl/*.md";
        checker.checkSchema(mdFiles);
    }
}
