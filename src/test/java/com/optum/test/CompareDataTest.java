package com.optum.test;

import com.optum.DataValidationApp;
import com.optum.excel.ExcelReader;
import com.optum.reconcile.Reconcile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = DataValidationApp.class)
@Slf4j
public class CompareDataTest extends AbstractTestNGSpringContextTests {
    Path path = Paths.get("src/main/resources/data/testData.xlsx");
    @Autowired
    JdbcTemplate jdbcTemplate;


    @Test
    public void testReadExcel() {
        ExcelReader excelReader = new ExcelReader(path);
        List<Map<String, Object>> outputFromExcel = excelReader.readFile("FICS-EVNT-TYP");
        System.out.println(outputFromExcel);
    }

    @Test
    public void testDb() {
        List<Map<String, Object>> outputFromDb = jdbcTemplate.queryForList("Select * from \"FICS-EVNT-TYP\"");
        System.out.println(outputFromDb);
    }

    @Test
    public void reconcileData() {
        List<String> sheets = List.of("FICS-EVNT-TYP");
        for (String sheet : sheets) {
            ExcelReader excelReader = new ExcelReader(path);
            List<Map<String, Object>> outputFromExcel = excelReader.readFile(sheet);

            List<Map<String, Object>> outputFromDb = jdbcTemplate.queryForList("Select * from \"FICS-EVNT-TYP\"");

            log.info(outputFromExcel.toString());
            log.info(outputFromDb.toString());

            Map<String, List<Map<String, Object>>> matcher = Reconcile.reconcile(outputFromExcel, outputFromDb, "EVNT-TYP-ID");
            log.info(matcher.toString());

            Assert.assertTrue(matcher.get("mismatched").isEmpty());
            Assert.assertTrue(matcher.get("missing").isEmpty());
        }
    }
}
