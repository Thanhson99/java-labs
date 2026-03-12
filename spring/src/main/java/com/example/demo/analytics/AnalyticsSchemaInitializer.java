package com.example.demo.analytics;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Applies SQL scripts to the analytics database on startup.
 */
@Component
public class AnalyticsSchemaInitializer {

    public AnalyticsSchemaInitializer(@Qualifier("analyticsDataSource") DataSource analyticsDataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("db/analytics/V1__create_analytics_events.sql")
        );
        populator.execute(analyticsDataSource);
    }
}
