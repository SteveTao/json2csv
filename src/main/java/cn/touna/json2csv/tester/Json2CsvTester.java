package cn.touna.json2csv.tester;

import cn.touna.json2csv.Json2CsvMappingMarkdownLoader;
import cn.touna.json2csv.model.Json2CsvMapping;
import cn.touna.json2csv.resolve.Json2CsvMappingAdaptor;
import cn.touna.json2csv.resolve.Json2CsvResolver;
import cn.touna.json2csv.resolve.ResolverConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Json2CsvTester {

    private Json2CsvMappingMarkdownLoader loader;
    private Json2CsvResolver resolver;

    public Json2CsvTester() {
        ResolverConfig resolverConfig = new ResolverConfig();
        loader = new Json2CsvMappingMarkdownLoader();
        resolver = new Json2CsvResolver(resolverConfig);
    }

    public Json2CsvTester(ResolverConfig resolverConfig) {
        loader = new Json2CsvMappingMarkdownLoader();
        resolver = new Json2CsvResolver(resolverConfig);
    }

    public void json2csv(String mdFile,String jsonFile) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(mdFile);
        List<Json2CsvMapping> mappingList = loader.load("file:" + mdFile, fileInputStream);
        Map<String, Json2CsvMapping> mmap = new HashMap<>();
        for (Json2CsvMapping mapping : mappingList) {
            mmap.put(mapping.getTable(), mapping);
        }

        Json2CsvMappingAdaptor adaptor = new Json2CsvMappingAdaptor(resolver, mappingList);
        String json = new String(Files.readAllBytes(new File(jsonFile).toPath()));
        JSONObject data = JSON.parseObject(json);
        Map<String, List<String>> map = adaptor.resolve(data).getResolvedData();
        List<String> tables = new LinkedList<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println("\n");
            String tableName = entry.getKey();
            tableName = tableName.substring(0, tableName.length() - 11);
            System.out.println("#\ttableName : " + tableName);
            for (String line : entry.getValue()) {
                System.out.print(line);
                Json2CsvMapping mapping = mmap.get(tableName);
                statNull(line, mapping);
            }
            if (entry.getValue() != null && entry.getValue().size() > 0)
                tables.add(entry.getKey());
        }
    }

    private int statNull(String line, Json2CsvMapping mapping) {
        String[] cells = line.split(",");
        String cell = null;
        int cnt = 0;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < cells.length; i++) {
            cell = cells[i];
            if (ResolverConfig.DEFAULT_NULL_STRING.equals(cell)) {
                cnt++;
                if (i >= mapping.getFields().size()) {
                    System.out.println("#\t!!!!exists json or , in string ");
                } else {
                    list.add(mapping.getFields().get(i).getField());
                }
            }
        }
        if (cnt > 1)
            System.out.println("#\tnullColumnCount: " + cnt + "\tColumns:" + list);
        return cnt;
    }
}
