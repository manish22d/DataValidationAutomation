package com.optum.test;

import com.optum.DataValidationApp;
import com.optum.config.AppConfig;
import com.optum.db.DataSource;
import com.optum.excel.ExcelReader;
import com.optum.pojo.Datasource;
import com.optum.reconcile.Reconcile;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@ContextConfiguration(classes = DataValidationApp.class)
//@SpringBootTest
public class CompareDataTest extends AbstractTestNGSpringContextTests {

    @Autowired
    JdbcTemplate jdbcTemplate;

//    @Autowired
//    ExcelReader excelReader;

    @Test
    public void testReadExcel() {
        String path = "D:\\Automation\\DataValidationAutomation\\src\\main\\resources\\data\\testData.xlsx";
        ExcelReader excelReader = new ExcelReader(path);
        List<Map<String, Object>> outputFromExcel = excelReader.readFile("FICS-EVNT-TYP");
        System.out.println(outputFromExcel);
    }

    @Test
    public void testDb() throws SQLException {
        List<Map<String, Object>> outputFromDb = jdbcTemplate.queryForList("Select * from \"FICS-EVNT-TYP\"");
        System.out.println(outputFromDb);
    }

    @Test
    public void reconcileData() {
        String path = "D:\\Automation\\DataValidationAutomation\\src\\main\\resources\\data\\testData.xlsx";
        ExcelReader excelReader = new ExcelReader(path);
        List<Map<String, Object>> outputFromExcel = excelReader.readFile("FICS-EVNT-TYP");

        List<Map<String, Object>> outputFromDb = jdbcTemplate.queryForList("Select * from \"FICS-EVNT-TYP\"");

        System.out.println(outputFromExcel);
        System.out.println(outputFromDb);
        Map<String, List<Map<String, Object>>> matcher = Reconcile.reconcileLists(outputFromExcel, outputFromDb, "EVNT-TYP-ID");
        System.out.println(matcher);
    }
}
