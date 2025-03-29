package com.optum.test;

import com.optum.DataValidationApp;
import com.optum.excel.ExcelReader;
import com.optum.reconcile.Reconcile;
import com.optum.report.ReconciliationReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = DataValidationApp.class)
@Slf4j
public class CompareDataTest extends AbstractTestNGSpringContextTests {
    Path path = Paths.get("src/main/resources/data/testData.xlsx");
    ExcelReader excelReader;
    @Autowired
    Reconcile reconcile;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeClass
    public void setUp() {
        excelReader = new ExcelReader(path);
    }

    @Test
    public void testReadExcel() {
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
        List<String> sheets = excelReader.getSheets();
        for (String sheetName : sheets) {
            List<Map<String, Object>> outputFromExcel = excelReader.readFile(sheetName);
            List<Map<String, Object>> outputFromDb = jdbcTemplate.queryForList(String.format("Select * from \"%s\"", sheetName));

            log.info(outputFromExcel.toString());
            log.info(outputFromDb.toString());

            ReconciliationReport report = reconcile.reconcile(outputFromExcel, outputFromDb, "EVNT-TYP-ID");
            log.info(report.toString());
            report.generateReportTable();
            Assert.assertTrue(report.mismatched.isEmpty());
            Assert.assertTrue(report.missing.isEmpty());
        }
    }
}
