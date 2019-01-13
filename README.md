# json2csv
## 概要
- 来源
 json2csv是源自投哪网大数据部门的一个数据处理工具。
- 场景
 来源与不同的第三方公司的数据汇聚一起成为公司的重要资产，这些数据各式各样，以json的格式存储在hdfs中。半结构化的json数据并不利于风控分析人员来使用，为此催生了将json转换，并存储到hive的需求。在hive中，每一个表数据都是以csv格式存在（单元格用\001区分）。
- 面临的挑战
  1. 市面上并没有较为完整的json2csv方案。传统的json2csv方案为自动推倒出模型结构，在遇到json数组的情况时，并没有做很好的处理。
  2. json2csv不仅是技术人员使用，数据分析人员也要了解数据的来龙去脉。
  3. 面对大量的不同来源，不同模型的数据，需要快速解析出不同的表数据。
- 目标
   json2csv致力于构建一个快速，高效，开放，完整的数据解析工具

## 入门
```text
# 图形表达

json - mapping - > csv

# 案例
案例1： Json2CsvApp
案例2： Json2CsvMappingAdaptor api使用
```
json2csv的整个过程入图：
![json2csv](doc/json2csv.jpg)

- 输入：jsonline文件，即每一行一个json。api中传JSONObject
- 输出：csv格式的字符串，以\n结尾
- 中间映射：markdown格式，方便编辑纷繁复杂的映射。可参考[TweeterUserMapping.md](src/test/resources/mapping/TweeterUserMapping.md)

在项目中大量使用了fastjson的JSONPath技术,您需要对jsonpath有足够的了解，才能更好的理解此项目。
 [w3cschool fastjson jsonpath](https://www.w3cschool.cn/fastjson/fastjson-jsonpath.html)

### 案例1：Json2CsvApp
```java
import org.junit.Test;
import java.io.IOException;

public class AppTest {

    @Test
    public void shouldAnswerWithTrue() throws IOException {
        Json2CsvApp app = new Json2CsvApp();
        String dataDir = System.getProperty("user.dir") + "/src/test/resources";
        String mappingFile = dataDir + "/mapping/TweeterUserMapping.md";
        String jsonFile = dataDir + "/json/tweeter-user.jl";
        String csvDir = dataDir + "/csv";
        app.json2csv(mappingFile, jsonFile,csvDir);
    }
}
```

### 案例2：Json2CsvMappingAdaptor api使用

输入的jsonline文件：[tweeter-user.jl](src/test/resources/json/tweeter-user.jl)
映射文件为：[TweeterUserMapping.md](src/test/resources/mapping/TweeterUserMapping.md)

```java
package cn.touna.json2csv.resolve;

import cn.touna.json2csv.Json2CsvMappingMarkdownLoader;
import cn.touna.json2csv.model.Json2CsvMapping;
import cn.touna.json2csv.model.ResolveResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                return value;
            }
        });
        resolverConfig.setCellSeparator("||");

        //解析器
        Json2CsvResolver resolver = new Json2CsvResolver(resolverConfig);

        //映射文件加载器
        Json2CsvMappingMarkdownLoader loader= new Json2CsvMappingMarkdownLoader();

        ClassPathResource mappingResource = new ClassPathResource("mapping/TweeterUserMapping.md");
        String mappingPath = mappingResource.getURL().getFile();
        List<Json2CsvMapping> mappingList = loader.loadFromFile(mappingPath);

        JsonDataResolver jsonDataResolver = new Json2CsvMappingAdaptor(resolver,mappingList);

        BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("json/tweeter-user.jl").getInputStream()));
        String line = null;
        JSONArray ja = new JSONArray();
        while ((line = br.readLine()) != null){
            ja.add(JSONObject.parseObject(line));
        }
        br.close();
        JSONObject jo = ja.getJSONObject(0);

        ResolveResult resolveResult = jsonDataResolver.resolve(jo);
        Map<String, List<String>> resolvedData = resolveResult.getResolvedData();
        for (Map.Entry<String, List<String>> entry : resolvedData.entrySet()) {
            System.out.println("Table: " + entry.getKey());
            System.out.println("---------------------------------");
            for (String row : entry.getValue()) {
                System.out.println(row);
            }
            System.out.println("complete!");
        }
    }
}
```

**执行结果**以||为分隔符
```text
772682964||SitePoint JavaScript||SitePointJS||Melbourne, Australia||Keep up with JavaScript tutorials, tips, tricks and articles at SitePoint.||http://t.co/cCH13gqeUK||false||2145||18||328||1345572393000||57||43200||Wellington
```

## 映射文件
> 映射文件组成

映射文件(json2csv mapping，简称mapping)由markdown文档来组织。以下为mapping中标签的含义
```
# <title>       表示整个文档的名称，可以自由定义
## <tableName>  表示json格式化成csv后，要输出到哪个表
- config        表示这个表的数据来源于json数据的哪个节点。config的内容是json格式。
- fields        从指定的字段中获取指定的字段值。fields的内容是csv格式。
```

总结而言：
config主要是讲json数据是否需要做json2csv的转换
fields主要将哪些字段需要做映射

> config格式

```json
{
  "filter": {       /*过滤器，用来选择json是否符合过滤条件，符合才能做*/
    "match":{}
  },
  "root": "user",
  "comment": "tweeter用户表"
}
```

标签 | 说明 | 用法
---|---|---
filter|过滤器，用来过滤json，符合过滤条件才能进行下一步|按elasticsearch的风格，当前仅支持match语法
filter.match|字符串匹配，json对象，key为jsonpath路径，value为值，value可以为正则表达式| {"name":"[a-z]+BlackList$"}
root|从json中选择根节点，有了根节点，就可以从中取字段了.格式为jsonpath路径| data.user
optionalRoots|可选的根，字符串数组。主要的字段是从root里面拿，有一些字段并非在root字段中，例如root为data.user,但要拿data.requestNo字段，那么可选根就可以填写[data],data.requestNo字段如果在root中找不到，就会去optionalRoots中寻找|["data"]
encryptColumns|要加密的字段，json对象，key为要加密的csv字段(fields中的列),value为加密方法的名字，加密方法通过ResolverConfig.addEncryptHandler方法来注入，由开发人人员自己注入，系统不提供加密方法|{"name":"SHA"}
comment| 表的说明，可用于后期建表语句的生成| 用户表

> fields格式

fields是csv格式,以英文逗号分隔，包含四个列，分别为：field,column,type,comment

列|说明|案例
---|---|---
field|json数据root/optionalRoots下的字段，必填项。字段可以为jsonpath路径。如果以$开头，则字段值会从json数据的根开始查找，而不会从root,optionalRoots下查找| $.data.user.type
column|字段名，可不填。如果不填，则用field转换过来，转换规则为：取jsonpath最后一个点号后面的字符串，将驼峰发改写为下划线的格式，例如field=person.lastName,则colunn为last_name。如果填写，则使用填写的column|user_type
type|用户自己填写，用来定义类型，如不填，则默认为varchar(100)| tinyint
comment|字段描述，如果里面包含英文逗号，则需要用双引号""括起来|用户类型枚举值,1:普通用户;2:会员用户

> 为什么要mapping用markdown格式?

mapping可以融合config(json格式)和fields(csv格式)于一体，方便阅读

对于某种json，一般拥有较多或者较深的层次结构，因而针对不同层次的节点，可以解析成好不同的表数据。这种json数据可以归类为一类数据，这一类数据的映射放在同一个文档中，既方便归类整理，又方便阅读。

映射作为一种元数据，也是需要维护和阅读的。markdown在json2csv中具有两个功能：一是作为映射的存储；
二是作为markdown格式，提供给技术人员和业务人员阅读，让大家能看清数据的来龙去脉。

数据库可用来存储mapping，但存在诸多问题：
1. 不方便大规模的编辑。mapping在初期存在不稳定的情况，需要时常改动。
2. 不直观。对于数据开发人员，数据使用人员，快速了解数据的来龙去脉有利于发现问题，理解数据
3. 存储在数据库中，开发人员需要额外地提供文档给数据分析人员

## json转换函数

> 概述

在mapping文件中，有几个地方用到了jsonpath技术
1. config中的root,optionalRoots
2. fields中的field

jsonpath技术在大多数情况下比较完备了，但在函数上和自定义拓展上还不够开放，为了处理现实遇到的一些转换和计算问题，json2csv项目中对jsonpath进行了函数拓展。

项目中已经内置了一些函数，同时通过api，你可以注入语法和相关处理函数
内置函数有：
- json转换函数
  - split
  - toJson
  - flat
  - entrySet
- 值处理函数
  - sysVar
  - defaultValue
  - dateFormat
  - timestamp

json转换函数，结果输出为JSONObject/JSONArray对象；值处理函数，结果输出为值

> 内置函数


> 自定义函数

## api

## 更多定制化
```text
1. markdown包含更多的配置
2.

```
