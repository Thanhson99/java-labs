package com.example.demo.config;

import com.example.demo.analytics.AnalyticsDataSourceProperties;
import com.example.demo.ratelimit.RegistrationRateLimitProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.Clock;

/**
 * Central application configuration for infrastructure beans.
 */
@Configuration
@EnableConfigurationProperties({AnalyticsDataSourceProperties.class, RegistrationRateLimitProperties.class})
public class AppBeansConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataSource")
    @Primary
    DataSource primaryDataSource(DataSourceProperties primaryDataSourceProperties) {
        return primaryDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "analyticsDataSource")
    DataSource analyticsDataSource(AnalyticsDataSourceProperties properties) {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName(properties.driverClassName())
                .url(properties.url())
                .username(properties.username())
                .password(properties.password())
                .build();
    }

    @Bean
    @Qualifier("primaryJdbcTemplate")
    JdbcTemplate primaryJdbcTemplate(@Qualifier("dataSource") DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }

    @Bean
    JdbcTemplate analyticsJdbcTemplate(@Qualifier("analyticsDataSource") DataSource analyticsDataSource) {
        return new JdbcTemplate(analyticsDataSource);
    }
}
