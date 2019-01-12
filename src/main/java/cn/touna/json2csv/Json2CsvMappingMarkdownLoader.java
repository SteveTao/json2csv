package cn.touna.json2csv;

import cn.touna.json2csv.json.ItemFilter;
import cn.touna.json2csv.markdown.model.*;
import cn.touna.json2csv.markdown.utils.MarkdownResolver;
import cn.touna.json2csv.markdown.utils.ResolverUtil;
import cn.touna.json2csv.model.Json2CsvMapping;
import cn.touna.json2csv.model.Json2CsvMappingField;
import cn.touna.json2csv.json.JsonFilterExpression;
import cn.touna.json2csv.utils.LittleUnderlineNameStrategy;
import cn.touna.json2csv.utils.NameStrategy;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Json2CsvMappingMarkdownLoader {

    public static final String FIELDS = "fields";
    public static final String CONFIG = "config";
    public static final String COMMENT = "comment";
    public static final String FILTER = "filter";
    public static final String ITEM_FILTER = "itemFilter";
    public static final String ROOT = "root";
    public static final String OPTIONAL_ROOTS = "optionalRoots";
    public static final String ENCRYPT_COLUMNS = "encryptColumns";

    public static final String FIELD = "field";
    public static final String COLUMN = "column";
    public static final String TYPE = "type";
    private static final String DEFAULT_TYPE = "varchar(100)";

    private static final Pattern FIELD_PATTERN = Pattern.compile("^[a-zA-Z_][\\w_]+$");

    private static final Logger logger = LoggerFactory.getLogger(Json2CsvMappingMarkdownLoader.class);

    public List<Json2CsvMapping> loadFromFile(String path) throws IOException {
        String[] ps = path.split(";|,");
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        List<Json2CsvMapping> mappingList = new LinkedList<>();
        for (String p : ps) {
            if (p.startsWith("/")) {
                p = "file:" + p;
            }
            Resource[] resources = patternResolver.getResources(p);
            for (Resource resource : resources) {
                List<Json2CsvMapping> mappings = load(resource.getFilename(), resource.getInputStream());
                if (mappings != null && mappings.size() > 0)
                    mappingList.addAll(mappings);
            }
        }
        return mappingList;
    }

    public List<Json2CsvMapping> load(String uri, InputStream is) throws IOException {
        final List<Json2CsvMapping> list = new LinkedList<>();
        MarkdownResolver markdownResolver = new MarkdownResolver();
        MdDocument document = markdownResolver.load(is);

        document.getChilds().stream()
                .filter(header1 -> header1 instanceof MdHeader && ((MdHeader) header1).getLevel() == 1)
                .forEach(header1 -> {
                    header1.getChilds().stream()
                            .filter(header2 -> header2 instanceof MdHeader && ((MdHeader) header2).getLevel() == 2)
                            .forEach(header2 -> {
                                Json2CsvMapping mapping = loadFromHeader2(uri, (MdHeader) header2);
                                list.add(mapping);
                            });
                });
        return list;
    }

    private Json2CsvMapping loadFromHeader2(final String uri, MdHeader mdHeader) {
        Json2CsvMapping mapping = new Json2CsvMapping();
        mapping.setTable(mdHeader.getTitle());
        mdHeader.getChilds().stream()
                .filter(listItem -> listItem instanceof MdListItem && ((MdListItem) listItem).getLevel() == 1)
                .forEach(child -> {
                    MdListItem listItem = (MdListItem) child;
                    if (CONFIG.equals(listItem.getTitle())) {
                        Optional<MdElement> element = listItem.getChilds().stream().filter(child1 -> child1 instanceof MdSegment).findFirst();
                        if (element.isPresent()) {
                            MdSegment segment = (MdSegment) element.get();
                            if ("yaml".equals(segment.getLanguage()) || "yml".equals(segment.getLanguage())) {
                                //TODO 支持yaml的解析方式

                            } else {  //if("json".equals(segment.getLanguage()))
                                try {
                                    JSONObject configObj = JSONObject.parseObject(ResolverUtil.getSegmentString(listItem));
                                    fillMappingWithConfig(configObj, mapping);
                                } catch (Exception e) {
                                    logger.error("uri: " + uri + " , header: " + mdHeader.getTitle() + " " + e.getMessage());
                                    throw e;
                                }
                            }
                        }
                    } else if (FIELDS.equals(listItem.getTitle())) {
                        try {
                            mapping.setFields(getFields(uri, mdHeader.getTitle(), ResolverUtil.getSegmentString(listItem)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


        //validate fields of Json2CsvMapping
        if (StringUtils.isEmpty(mapping.getTable())) {
            throw new IllegalArgumentException("Json2CsvMapping.tableName cannot be null or empty! in file : " + uri);
        }
        if (mapping.getFields() == null || mapping.getFields().size() == 0) {
            throw new IllegalArgumentException(mapping.getTable() + " fields cannot be null or empty! in file: " + uri);
        }
        if (StringUtils.isEmpty(mapping.getComment())) {
            mapping.setComment(mapping.getTable());
        }
        return mapping;
    }

    private void fillMappingWithConfig(JSONObject configObj, Json2CsvMapping mapping) {
        mapping.setComment(configObj.getString(COMMENT));
        mapping.setRoot(configObj.getString(ROOT));
        JSONArray jsonArray = configObj.getJSONArray(OPTIONAL_ROOTS);
        if (jsonArray != null) {
            Set<String> set = jsonArray.toJavaList(String.class).stream().collect(Collectors.toSet());
            mapping.setOptionalRoots(set);
        }
        mapping.setFilter(new JsonFilterExpression(configObj.getJSONObject(FILTER)));
        mapping.setItemFilter(new ItemFilter(configObj.getJSONObject(ITEM_FILTER)));
        JSONObject encryptColumns = configObj.getJSONObject(ENCRYPT_COLUMNS);
        if (encryptColumns != null) {
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, Object> entry : encryptColumns.entrySet()) {
                map.put(entry.getKey(), (String) entry.getValue());
            }
            mapping.setEncryptColumns(map);
        }
    }

    private List<Json2CsvMappingField> getFields(String uri, String tableName, String content) throws IOException {
        StringReader in = new StringReader(content);
        Iterable<CSVRecord> records = CSVFormat
                .RFC4180
                .withHeader(FIELD, COLUMN, TYPE, COMMENT)
                .withCommentMarker('#')
                .parse(in);
        List<Json2CsvMappingField> fields = new LinkedList<>();
        Set<String> fieldNameSet = new HashSet<>();
        String field = null;
        try {
            for (CSVRecord record : records) {
                Json2CsvMappingField fieldObj = new Json2CsvMappingField();
                field = record.get(FIELD);
                String column = record.get(COLUMN);
                String type = record.get(TYPE);
                String comment = record.get(COMMENT);

                if (StringUtils.isEmpty(column)) {
                    column = getDefaultColumn(field);
                }
                if (!FIELD_PATTERN.matcher(column).matches()) {
                    throw new IllegalArgumentException(uri + "中" + tableName + "存在非法的列名: " + column);
                }

                if (StringUtils.isEmpty(type)) {
                    type = DEFAULT_TYPE;
                }

                if (!StringUtils.isEmpty(comment)) {
                    comment = comment.replace(",", "，");
                }

                fieldObj.setField(field);
                fieldObj.setColumn(column);
                fieldObj.setType(type);
                fieldObj.setComment(comment);
                fields.add(fieldObj);

                if (!fieldNameSet.contains(column))
                    fieldNameSet.add(column);
                else {
                    //检测是否存在重复的列名
                    throw new IllegalArgumentException(uri + "中" + tableName + "存在重复的列名: " + column);
                }
            }
        } catch (IllegalArgumentException e) {
            String msg = uri + " # " + tableName + " # " + field + " error: " + e.getMessage();
            throw new IllegalArgumentException(msg);
        }

        if (fields.size() == 0)
            return null;
        return fields;
    }

    public String getDefaultColumn(String field) {
        int index = field.lastIndexOf(".");
        if (index == -1)
            return nameStrategy.getName(field);
        return nameStrategy.getName(field.substring(index + 1));
    }

    private NameStrategy nameStrategy = new LittleUnderlineNameStrategy();

    public NameStrategy getNameStrategy() {
        return nameStrategy;
    }

    public void setNameStrategy(NameStrategy nameStrategy) {
        this.nameStrategy = nameStrategy;
    }
}
