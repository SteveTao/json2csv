package cn.touna.json2csv;

import cn.touna.json2csv.model.Json2CsvMapping;
import cn.touna.json2csv.resolve.Json2CsvMappingAdaptor;
import cn.touna.json2csv.resolve.Json2CsvResolver;
import cn.touna.json2csv.resolve.ResolverConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * Json2Csv示例。
 * 用于处理jsonline文件，每一行为一个json对象，用\n做换行符
 */
public class Json2CsvApp {

    private Json2CsvMappingMarkdownLoader loader;
    private Json2CsvResolver resolver;

    public Json2CsvApp(){
        ResolverConfig resolverConfig = new ResolverConfig();
        loader = new Json2CsvMappingMarkdownLoader();
        resolver = new Json2CsvResolver(resolverConfig);
    }

    public void json2csv(String mdFile,String jsonFile,String csvDir) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(mdFile);
        List<Json2CsvMapping> mappingList = loader.load("file:" + mdFile, fileInputStream);

        Json2CsvMappingAdaptor adaptor = new Json2CsvMappingAdaptor(resolver, mappingList);
        String json = new String(Files.readAllBytes(new File(jsonFile).toPath()));
        JSONObject data = JSON.parseObject(json);
        Map<String, List<String>> map = adaptor.resolve(data).getResolvedData();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String outfile = csvDir + "/" + entry.getKey() + ".csv";
            BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
            for (String s : entry.getValue()) {
                bw.append(s);
            }
            bw.close();
        }
    }
}
