package cn.touna.json2csv.resolve;

import cn.touna.json2csv.Json2CsvMappingMarkdownLoader;
import cn.touna.json2csv.model.Json2CsvMapping;
import cn.touna.json2csv.model.ResolveResult;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Json2CsvMappingAdaptorTest {

    @Test
    public void resolve() throws IOException {
        //解析配置
        ResolverConfig resolverConfig = new ResolverConfig();
        resolverConfig.setNullResolver(new NullResolver() {
            @Override
            public boolean isNull(String value) {
                if(value == null || value.length() == 0 || "{}".equals(value) || "[]".equals(value))
                    return true;
                return false;
            }
        });
        resolverConfig.setNullString("\\N");
        resolverConfig.setTypeConverter(new TypeConverter() {

            private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            @Override
            public String convert(String type, String value) {
                if("timestamp".equals(type)){
                    if(value.matches("\\d+"))
                        return value;
                    try{
                        Date date = sdf.parse(value);
                        return String.valueOf(date.getTime());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
        resolverConfig.setCellSeparator("\001");

        //解析器
        Json2CsvResolver resolver = new Json2CsvResolver(resolverConfig);

        //映射文件加载器
        Json2CsvMappingMarkdownLoader loader= new Json2CsvMappingMarkdownLoader();
        String mappingPath = "file://yourdir/mapping.md";
        List<Json2CsvMapping> mappingList = loader.loadFromFile(mappingPath);

        JsonDataResolver jsonDataResolver = new Json2CsvMappingAdaptor(resolver,mappingList);

        String jsonStr = "...";
        JSONObject jo = JSONObject.parseObject(jsonStr);

        ResolveResult resolveResult = jsonDataResolver.resolve(jo);
        Map<String, List<String>> resolvedData = resolveResult.getResolvedData();
        for (Map.Entry<String, List<String>> entry : resolvedData.entrySet()) {
            System.out.println("Table: " + entry.getKey());
            System.out.println("---------------------------------");
            for (String line : entry.getValue()) {
                System.out.println(line);
            }
            System.out.println();
        }
    }
}