package cn.touna.json2csv;

import static org.junit.Assert.assertTrue;

import cn.touna.json2csv.tester.Json2CsvTester;
import org.junit.Test;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        Json2CsvTester tester = new Json2CsvTester();
        String mdFiles = "/home/appuser/workspace/java/tbd/tbd-etl/doc/tmpl/baidu_getBlackList.md";
        String jsonFile = "/home/appuser/workspace/java/tbd/tbd-etl/doc/baidu_getBlackList.json";
        tester.json2csv(mdFiles, jsonFile);
    }
}
