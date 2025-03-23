package com.optum.config;


import com.optum.pojo.Datasource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Datasource myService() {
        return new Datasource();
    }
}
