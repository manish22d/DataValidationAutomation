package com.optum.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class SpringTestngExampleApplicationTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        String sql = "SELECT * FROM users"; // Ensure a table 'users' exists
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
        assertFalse(users.isEmpty(), "Database should contain users");
    }
}
