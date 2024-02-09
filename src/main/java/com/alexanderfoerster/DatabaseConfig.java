package com.alexanderfoerster;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    @Value("${spring.datasource.url}")
    private String dbUrl;

    Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        log.info("Datasource URL from ENV: " + dbUrl);

        config.setJdbcUrl(dbUrl);
        return new HikariDataSource(config);
    }
}
