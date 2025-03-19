package com.optum.test;

import com.optum.db.DataSource;
import com.optum.excel.ExcelReader;
import com.optum.pojo.Datasource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CompareDataTest {
    @Test
    public void testReadExcel() {

        File file = new File("D:\\Automation\\DataValidationAutomation\\src\\main\\resources\\data\\testData.xlsx");
        ExcelReader excelReader = new ExcelReader(file);
        excelReader.readFile("FICS-EVNT-TYP ");
    }

    @Test
    public void testDb() {

        Datasource ds = new Datasource();
        ds.setName("h2data");
        ds.setUrl("jdbc:h2:mem:dcbapp");
        ds.setUser("sa");
        ds.setPassword("password");
        DataSource.init(ds);
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(DataSource.getDataSource());
        List<Map<String, Object>> output = jdbcTemplate.queryForList("show database");
        System.out.println(output);

    }
}
