package cn.touna.json2csv;

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
